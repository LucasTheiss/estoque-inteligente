"""Transação única de saldo, histórico e auditoria, compartilhada por todas as lojas."""
import hashlib
import json
from .db import audit
from .domain import OPERACOES, QuantidadeInteira, QuantidadeFracionada, ValidationError


class EstoqueService:
    def __init__(self, db, tenant, actor):
        self.db, self.tenant, self.actor = db, tenant, actor

    def movimentar(self, product, position, kind, quantity, reason, key, reverse_of=None):
        if not key or len(key) > 100:
            raise ValidationError('Chave da operação inválida. Recarregue a página.')
        if not reason or len(reason) > 1000:
            raise ValidationError('Informe a justificativa (até 1000 caracteres).')
        fingerprint = hashlib.sha256(json.dumps(
            [product, position, kind, str(quantity), reason, self.actor, reverse_of]).encode()).hexdigest()
        # Uma trava por pedido permite repetição segura sem serializar operações distintas.
        self.db.execute('SELECT pg_advisory_xact_lock(hashtextextended(%s,0))', (f'{self.tenant}:{key}',))
        prior = self.db.execute('SELECT * FROM movements WHERE tenant_id=%s AND request_key=%s',
                                (self.tenant, key)).fetchone()
        if prior:
            if prior['fingerprint'] != fingerprint:
                raise ValidationError('Esta chave já foi usada em outra operação.')
            return prior
        item = self.db.execute('SELECT p.* FROM products p WHERE tenant_id=%s AND id=%s AND active FOR SHARE',
                               (self.tenant, product)).fetchone()
        place = self.db.execute('SELECT * FROM positions WHERE tenant_id=%s AND id=%s AND active FOR SHARE',
                                (self.tenant, position)).fetchone()
        if not item or not place:
            raise ValidationError('Produto ou posição não encontrado nesta loja.')
        if item['unit'] != place['unit']:
            raise ValidationError('Produto e posição devem usar a mesma unidade.')
        strategy = QuantidadeInteira() if item['unit'] == 'un' else QuantidadeFracionada()
        quantity = strategy.validar(quantity)
        if item['unit'] == 'kg' and not self.db.execute('SELECT fractional FROM tenants WHERE id=%s',
                                                      (self.tenant,)).fetchone()['fractional']:
            raise ValidationError('Saída fracionada não habilitada nesta loja.')
        balance = self.db.execute('SELECT quantity FROM occupancy WHERE tenant_id=%s AND product_id=%s '
                                  'AND position_id=%s FOR UPDATE', (self.tenant, product, position)).fetchone()
        if not balance:
            raise ValidationError('Vincule o produto à posição antes de movimentar.')
        before = balance['quantity']
        if kind == 'estorno' and reverse_of:
            original = self.db.execute('SELECT * FROM movements WHERE tenant_id=%s AND id=%s FOR UPDATE',
                                       (self.tenant, reverse_of)).fetchone()
            if not original or original['kind'] not in ('entrada', 'saida'):
                raise ValidationError('Apenas entradas e saídas podem ser estornadas.')
            if original['product_id'] != product or original['position_id'] != position:
                raise ValidationError('O estorno deve usar o produto e posição originais.')
            if self.db.execute('SELECT id FROM movements WHERE tenant_id=%s AND reverse_of=%s',
                               (self.tenant, reverse_of)).fetchone():
                raise ValidationError('Movimentação já estornada.')
            after = before - original['delta']
            if after < 0:
                raise ValidationError('Saldo insuficiente para estornar a entrada.')
        elif kind in OPERACOES and reverse_of is None:
            after = OPERACOES[kind].executar(before, quantity)
        else:
            raise ValidationError('Tipo de movimentação inválido.')
        self.db.execute('UPDATE occupancy SET quantity=%s WHERE tenant_id=%s AND product_id=%s AND position_id=%s',
                        (after, self.tenant, product, position))
        result = self.db.execute('''INSERT INTO movements
            (tenant_id,product_id,position_id,actor_id,kind,delta,before_qty,after_qty,reason,request_key,fingerprint,reverse_of)
            VALUES(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) RETURNING *''',
            (self.tenant, product, position, self.actor, kind, after-before, before, after, reason, key,
             fingerprint, reverse_of)).fetchone()
        audit(self.db, self.tenant, self.actor, kind, movement=result['id'], before=str(before), after=str(after), reason=reason)
        return result

    def estornar(self, movement, reason, key):
        original = self.db.execute('SELECT * FROM movements WHERE tenant_id=%s AND id=%s',
                                   (self.tenant, movement)).fetchone()
        if not original:
            raise ValidationError('Movimentação não encontrada nesta loja.')
        return self.movimentar(original['product_id'], original['position_id'], 'estorno',
                              abs(original['delta']), reason, key, original['id'])
