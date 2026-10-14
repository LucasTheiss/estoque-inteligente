package br.com.estoque.service;

import br.com.estoque.repository.*;
import br.com.estoque.security.UsuarioAutenticado;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {
    private final UsuarioRepository usuarios;
    private final TentativaLoginRepository tentativas;
    private final AuditoriaRepository auditoria;
    private final PasswordEncoder encoder;
    private final String hashInexistente;
    public AutenticacaoService(UsuarioRepository usuarios,TentativaLoginRepository tentativas,AuditoriaRepository auditoria,PasswordEncoder encoder) {
        this.usuarios=usuarios;this.tentativas=tentativas;this.auditoria=auditoria;this.encoder=encoder;
        this.hashInexistente=encoder.encode("credencial-inexistente-apenas-para-custo-de-hash");
    }
    public UsuarioAutenticado autenticar(String loja,String nome,String senha) {
        if(loja==null || loja.length()>40 || nome==null || nome.length()>200 || senha==null || senha.getBytes(StandardCharsets.UTF_8).length>72)
            throw new BadCredentialsException("Credenciais inválidas.");
        String chave=UUID.nameUUIDFromBytes((loja+":"+nome).getBytes(StandardCharsets.UTF_8)).toString();
        if(tentativas.registrar(chave).attempts()>10)throw new LockedException("Muitas tentativas. Aguarde 15 minutos.");
        var usuario=usuarios.autenticar(loja,nome).orElse(null);
        if(!encoder.matches(senha,usuario==null?hashInexistente:usuario.senha()) || usuario==null)
            throw new BadCredentialsException("Credenciais inválidas.");
        tentativas.limpar(chave);
        auditoria.registrar(usuario.lojaId(),usuario.id(),"login","Autenticação realizada");
        return new UsuarioAutenticado(usuario);
    }
}
