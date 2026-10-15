package br.com.estoque.service;

import br.com.estoque.dto.ModeloProdutoForm;
import br.com.estoque.model.ModeloProduto;
import br.com.estoque.repository.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModeloProdutoService extends CadastroTemplate<ModeloProdutoForm, ModeloProduto> {
    private final ModeloProdutoRepository modelos;
    private final Acesso acesso;
    private final AuditoriaRepository auditoria;

    public ModeloProdutoService(
            ModeloProdutoRepository modelos, Acesso acesso, AuditoriaRepository auditoria) {
        super(acesso, auditoria, "modelo");
        this.modelos = modelos;
        this.acesso = acesso;
        this.auditoria = auditoria;
    }

    public List<ModeloProduto> listar(long loja) {
        acesso.exigir(loja, "cadastros");
        return modelos.listar(loja);
    }

    public ModeloProduto buscar(long loja, long id) {
        acesso.exigir(loja, "cadastros");
        return modelos.buscar(loja, id);
    }

    @Transactional
    public void salvar(long loja, ModeloProdutoForm f) {
        executarCadastro(loja, f);
    }

    @Override
    protected ModeloProduto preparar(long loja, ModeloProdutoForm f) {
        if (f.id() != null) modelos.bloquear(loja, f.id());
        return new ModeloProduto(
                f.id(),
                loja,
                Campos.texto(f.name(), "nome", true),
                Campos.texto(f.brand(), "marca", false),
                Campos.texto(f.category(), "categoria", false));
    }

    @Override
    protected long persistir(ModeloProduto modelo) {
        return modelos.salvar(modelo);
    }

    @Transactional
    public void excluir(long loja, long id) {
        acesso.exigir(loja, "cadastros");
        modelos.bloquear(loja, id);
        if (modelos.emUso(loja, id))
            throw new IllegalArgumentException(
                    "Modelo vinculado a produtos. Mantenha-o para preservar o histórico.");
        modelos.excluir(loja, id);
        auditoria.registrar(loja, acesso.usuario().id(), "modelo.excluir", Long.toString(id));
    }
}
