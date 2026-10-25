package br.com.estoque.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** X02: ofertas mantidas externamente, sem escrita pelo estoque. */
@Component
public class OfertaGateway {
    public record Oferta(String gtin, String fornecedor, String sku, String descricao, String marca,
            String unidade, BigDecimal preco, BigDecimal disponibilidade, String atualizacao) {}
    private final URI endpoint;
    private final ObjectMapper json;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    public OfertaGateway(@Value("${app.ofertas-url:http://127.0.0.1:8093/ofertas}") String url, ObjectMapper json) {
        endpoint = URI.create(url); this.json = json;
    }
    public List<Oferta> consultar(String gtin, String fornecedor) {
        if (gtin == null || fornecedor == null || gtin.length() > 100 || fornecedor.length() > 100)
            throw new IllegalArgumentException("Filtros devem ter até 100 caracteres.");
        try {
            var response = http.send(HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(3)).GET().build(),
                    HttpResponse.BodyHandlers.ofInputStream());
            try (var body = response.body()) {
                if (response.statusCode() != 200) throw new IllegalStateException();
                byte[] bytes = body.readNBytes(262145);
                if (bytes.length > 262144) throw new IllegalStateException();
                var registros = json.readValue(bytes, Oferta[].class);
                var chaves = new HashSet<String>();
                for (var o : registros) {
                    if (o == null || o.gtin() == null || !o.gtin().matches("[0-9]{8,14}")
                            || o.fornecedor() == null || o.fornecedor().isBlank() || o.sku() == null || o.sku().isBlank()
                            || o.descricao() == null || o.descricao().isBlank() || o.marca() == null
                            || !Set.of("un", "kg").contains(Objects.toString(o.unidade(), ""))
                            || o.preco() == null || o.preco().signum() < 0 || o.preco().scale() > 2
                            || o.disponibilidade() == null || o.disponibilidade().signum() < 0
                            || o.disponibilidade().scale() > 3 || o.atualizacao() == null
                            || !chaves.add(o.fornecedor() + "\t" + o.sku())) throw new IllegalStateException();
                    if (o.unidade().equals("un") && o.disponibilidade().stripTrailingZeros().scale() > 0)
                        throw new IllegalStateException();
                    LocalDate.parse(o.atualizacao());
                }
                return Arrays.stream(registros).filter(o -> gtin.isBlank() || o.gtin().equals(gtin.strip()))
                        .filter(o -> fornecedor.isBlank() || o.fornecedor().equalsIgnoreCase(fornecedor.strip())).toList();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("Consulta interrompida. Tente novamente.");
        } catch (Exception ex) {
            throw new IllegalArgumentException("Catálogo externo de ofertas indisponível ou inválido. Tente novamente mais tarde.");
        }
    }
}
