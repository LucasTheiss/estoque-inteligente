package br.com.estoque.security;

import br.com.estoque.model.Usuario;
import java.util.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record UsuarioAutenticado(Usuario usuario) implements UserDetails {
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(new SimpleGrantedAuthority("ROLE_"+usuario.perfil())); }
    @Override public String getPassword() { return usuario.senha(); }
    @Override public String getUsername() { return usuario.nome(); }
    @Override public boolean isEnabled() { return usuario.ativo(); }
}
