"""Strategy para quantidades e Template Method para cálculo de movimentação."""
from decimal import Decimal, InvalidOperation


class ValidationError(ValueError):
    pass


def decimal(value, scale=3):
    try:
        result = Decimal(str(value))
        if not result.is_finite() or abs(result) >= Decimal('1000000000000'):
            raise InvalidOperation
        if result != result.quantize(Decimal(1).scaleb(-scale)):
            raise InvalidOperation
        return result
    except (InvalidOperation, ValueError):
        raise ValidationError(f'Informe um número válido com até {scale} casas decimais.') from None


class QuantidadeFracionada:
    def validar(self, value):
        result = decimal(value)
        if result < 0:
            raise ValidationError('A quantidade não pode ser negativa.')
        return result


class QuantidadeInteira(QuantidadeFracionada):
    def validar(self, value):
        result = super().validar(value)
        if result != result.to_integral_value():
            raise ValidationError('Este produto aceita apenas unidades inteiras.')
        return result


class OperacaoEstoque:
    def executar(self, saldo, quantidade):
        final = self.calcular(saldo, quantidade)
        if final < 0:
            raise ValidationError('Estoque insuficiente nesta posição.')
        if final == saldo:
            raise ValidationError('A operação não altera o saldo.')
        return final


class EntradaEstoque(OperacaoEstoque):
    def calcular(self, saldo, quantidade):
        return saldo + quantidade


class SaidaEstoque(OperacaoEstoque):
    def calcular(self, saldo, quantidade):
        return saldo - quantidade


class AjusteEstoque(OperacaoEstoque):
    def calcular(self, saldo, quantidade):
        return quantidade


OPERACOES = {'entrada': EntradaEstoque(), 'saida': SaidaEstoque(), 'ajuste': AjusteEstoque()}


class BalancaAdapter:
    """Converte peso bruto e tara MQTT em peso líquido e subtotal decimal."""
    def converter(self, payload, preco):
        bruto = QuantidadeFracionada().validar(payload.get('gross'))
        tara = QuantidadeFracionada().validar(payload.get('tare'))
        liquido = bruto - tara
        if liquido <= 0:
            raise ValidationError('O peso líquido deve ser positivo.')
        return {'quantity': liquido, 'subtotal': (liquido * decimal(preco, 2)).quantize(Decimal('.01'))}
