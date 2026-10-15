package br.com.estoque.service;

import br.com.estoque.repository.AuditoriaRepository;

/** Template Method: autorização, preparação, persistência e auditoria na mesma ordem. */
public abstract class CadastroTemplate<F, E> {
    private final Acesso acesso;
    private final AuditoriaRepository auditoria;
    private final String entidade;

    protected CadastroTemplate(Acesso acesso, AuditoriaRepository auditoria, String entidade) {
        this.acesso=acesso; this.auditoria=auditoria; this.entidade=entidade;
    }

    // O serviço concreto abre a transação antes de chamar este método final.
    protected final void executarCadastro(long loja, F formulario) {
        acesso.exigir(loja,"cadastros");
        E entidadeValidada=preparar(loja,formulario);
        long id=persistir(entidadeValidada);
        auditoria.registrar(loja,acesso.usuario().id(),entidade+".salvar",Long.toString(id));
    }

    protected abstract E preparar(long loja, F formulario);
    protected abstract long persistir(E entidade);
}
