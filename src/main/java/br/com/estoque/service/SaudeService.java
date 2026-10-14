package br.com.estoque.service;

import br.com.estoque.repository.SaudeRepository;
import org.springframework.stereotype.Service;

@Service
public class SaudeService {
    private final SaudeRepository saude;
    public SaudeService(SaudeRepository saude) {this.saude=saude;}
    public boolean disponivel() {return saude.disponivel();}
}
