package br.com.estoque.config;

import br.com.estoque.service.DemonstracaoService;
import org.springframework.boot.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DemonstracaoConfig implements ApplicationRunner {
    private final DemonstracaoService demonstracao;
    @Value("${app.demo}") boolean demo;
    @Value("${app.demo-password}") String senhaDemo;
    @Value("${app.superadmin-password}") String senhaGlobal;
    public DemonstracaoConfig(DemonstracaoService demonstracao) { this.demonstracao=demonstracao; }
    @Override public void run(ApplicationArguments args) { demonstracao.provisionar(demo,senhaDemo,senhaGlobal); }
}
