package br.com.estoque.databasefirst;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class DemoSecurity {
    @Bean UserDetailsService users(Environment env) {
        String password = env.getRequiredProperty("DF_WEB_PASSWORD");
        if (password.length() < 12) throw new IllegalStateException("Senha deve ter ao menos 12 caracteres.");
        return new InMemoryUserDetailsManager(User.withUsername(env.getRequiredProperty("DF_WEB_USER"))
            .password("{bcrypt}" + new BCryptPasswordEncoder().encode(password)).roles("DEMO").build());
    }
    @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(a -> a.requestMatchers("/style.css", "/error").permitAll()
                .anyRequest().hasRole("DEMO"))
            .formLogin(f -> f.defaultSuccessUrl("/", true))
            .logout(l -> l.logoutSuccessUrl("/login?logout")).build();
    }
}
