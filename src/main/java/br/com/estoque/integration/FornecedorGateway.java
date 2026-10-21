package br.com.estoque.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** AIE: o estoque não persiste nem altera o cadastro do provedor. */
@Component
public class FornecedorGateway {
    public record Fornecedor(String codigo, String nome, String documento, String email, String telefone) {}
    private final URI endpoint;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();

    public FornecedorGateway(@Value("${app.fornecedores-url:http://127.0.0.1:8092/fornecedores}") String url,
            ObjectMapper json) {
        endpoint = URI.create(url);
        this.json = json;
    }

    public List<Fornecedor> consultar(String busca) {
        try {
            var request = HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(3)).GET().build();
            var response = http.send(request, HttpResponse.BodyHandlers.ofInputStream());
            try (var body = response.body()) {
                if (response.statusCode() != 200) throw new IllegalStateException();
                byte[] bytes = body.readNBytes(262145);
                if (bytes.length > 262144) throw new IllegalStateException();
                var registros = json.readValue(bytes, Fornecedor[].class);
                String filtro = busca.toLowerCase(Locale.ROOT);
                return Arrays.stream(registros).peek(f -> {
                    if (f.codigo() == null || f.nome() == null || f.documento() == null
                            || f.email() == null || f.telefone() == null) throw new IllegalStateException();
                }).filter(f -> (f.codigo() + " " + f.nome() + " " + f.documento())
                        .toLowerCase(Locale.ROOT).contains(filtro)).toList();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Consulta interrompida. Tente novamente.");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Cadastro externo de fornecedores indisponível. Tente novamente mais tarde.");
        }
    }
}
