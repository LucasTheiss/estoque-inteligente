import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Provedor independente de ofertas; manutenção fora da fronteira do estoque. */
public class Provedor {
    static final String[] CAMPOS = {"gtin","fornecedor","sku","descricao","marca","unidade","preco","disponibilidade","atualizacao"};
    static void validar(String[] v) {
        if (v.length != 9) throw new IllegalArgumentException("Informe os nove campos da oferta.");
        for (String s : v) if (s.isBlank() || s.chars().anyMatch(c -> c < 32))
            throw new IllegalArgumentException("Campos obrigatórios, sem caracteres de controle.");
        if (!v[0].matches("[0-9]{8,14}") || !Set.of("un","kg").contains(v[5]))
            throw new IllegalArgumentException("GTIN ou unidade inválidos.");
        BigDecimal preco = new BigDecimal(v[6]), quantidade = new BigDecimal(v[7]);
        if (preco.signum() < 0 || preco.scale() > 2 || quantidade.signum() < 0 || quantidade.scale() > 3
                || (v[5].equals("un") && quantidade.stripTrailingZeros().scale() > 0))
            throw new IllegalArgumentException("Preço ou disponibilidade inválidos.");
        LocalDate.parse(v[8]);
    }
    static String escapar(String s) { return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\""; }
    static void gravar(Path arquivo, List<String> linhas) throws Exception {
        Path destino = arquivo.toAbsolutePath();
        Path temp = Files.createTempFile(destino.getParent(), "ofertas-", ".tmp");
        try {
            Files.write(temp, linhas, StandardCharsets.UTF_8);
            try { Files.move(temp, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temp, destino, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }
    public static void main(String[] args) throws Exception {
        Path arquivo = Path.of(System.getenv().getOrDefault("CATALOGO_ARQUIVO", "ofertas.tsv"));
        if (args.length > 0) {
            var linhas = new ArrayList<>(Files.exists(arquivo) ? Files.readAllLines(arquivo) : List.<String>of());
            if (args[0].equals("salvar") && args.length == 10) {
                String[] v = Arrays.copyOfRange(args,1,10); validar(v);
                linhas.removeIf(l -> { var c=l.split("\t",-1); return c.length==9 && c[1].equals(v[1]) && c[2].equals(v[2]); });
                linhas.add(String.join("\t",v));
            } else if (args[0].equals("excluir") && args.length == 3) {
                linhas.removeIf(l -> { var c=l.split("\t",-1); return c.length==9 && c[1].equals(args[1]) && c[2].equals(args[2]); });
            } else throw new IllegalArgumentException("salvar GTIN FORNECEDOR SKU DESCRICAO MARCA UNIDADE PRECO DISPONIBILIDADE DATA; ou excluir FORNECEDOR SKU");
            gravar(arquivo, linhas); System.out.println("Catálogo externo atualizado."); return;
        }
        int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "8093"));
        String host = System.getenv().getOrDefault("BIND_HOST", "127.0.0.1");
        var server = HttpServer.create(new InetSocketAddress(host,porta),0);
        server.createContext("/ofertas", e -> {
            try {
                if (!e.getRequestURI().getPath().equals("/ofertas")) { e.sendResponseHeaders(404,-1); return; }
                if (!e.getRequestMethod().equals("GET")) {
                    e.getResponseHeaders().set("Allow","GET"); e.sendResponseHeaders(405,-1); return;
                }
                var itens = new ArrayList<String>(); var chaves = new HashSet<String>();
                for (String linha : Files.readAllLines(arquivo)) {
                    if (linha.isBlank()) continue;
                    String[] v=linha.split("\t",-1); validar(v);
                    if (!chaves.add(v[1]+"\t"+v[2])) throw new IllegalArgumentException("Oferta duplicada.");
                    var pares=new ArrayList<String>();
                    for(int i=0;i<9;i++) pares.add(escapar(CAMPOS[i])+":"+escapar(v[i]));
                    itens.add("{"+String.join(",",pares)+"}");
                }
                byte[] body=("["+String.join(",",itens)+"]").getBytes(StandardCharsets.UTF_8);
                e.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");
                e.getResponseHeaders().set("Cache-Control","no-store");
                e.sendResponseHeaders(200,body.length); e.getResponseBody().write(body);
            } catch(Exception ex) { e.sendResponseHeaders(503,-1); }
            finally { e.close(); }
        });
        server.start(); System.out.println("Ofertas externas: http://"+host+":"+porta+"/ofertas");
    }
}
