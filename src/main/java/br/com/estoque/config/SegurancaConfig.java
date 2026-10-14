package br.com.estoque.config;

import br.com.estoque.service.AutenticacaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Configuration
public class SegurancaConfig {
    public static class LoginDetails extends WebAuthenticationDetails {
        final String loja;
        LoginDetails(HttpServletRequest request) { super(request);loja=request.getParameter("loja"); }
    }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean AuthenticationProvider autenticacao(AutenticacaoService service) {
        return new AuthenticationProvider() {
            @Override public Authentication authenticate(Authentication auth) {
                String loja=auth.getDetails() instanceof LoginDetails d?d.loja:"";
                var principal=service.autenticar(loja,auth.getName(),String.valueOf(auth.getCredentials()));
                return UsernamePasswordAuthenticationToken.authenticated(principal,null,principal.getAuthorities());
            }
            @Override public boolean supports(Class<?> type) { return UsernamePasswordAuthenticationToken.class.isAssignableFrom(type); }
        };
    }
    @Bean SecurityFilterChain security(HttpSecurity http,AuthenticationProvider provider) throws Exception {
        return http.authenticationProvider(provider)
            .authorizeHttpRequests(a->a.requestMatchers("/login","/assets/**","/health","/error").permitAll().anyRequest().authenticated())
            .formLogin(f->f.loginPage("/login").authenticationDetailsSource(LoginDetails::new).defaultSuccessUrl("/",true).failureUrl("/login?error"))
            .logout(l->l.logoutSuccessUrl("/login?logout"))
            .headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; font-src 'self'; img-src 'self' data:; frame-ancestors 'none'; form-action 'self'; base-uri 'self'")))
            .build();
    }
}
