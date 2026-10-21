import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/** Aplicação externa demonstrativa. Executar com Java 21; não usa o banco do estoque. */
public class Provedor {
    private static String escapar(String texto) {
        return "\"" + texto.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
    public static void main(String[] args) throws Exception {
        Path arquivo = Path.of(System.getenv().getOrDefault("CATALOGO_ARQUIVO", "fornecedores.tsv"));
        if (args.length > 0 && args[0].equals("salvar")) {
            if (args.length != 6) throw new IllegalArgumentException("salvar CODIGO NOME DOCUMENTO EMAIL TELEFONE");
            for (int i=1;i<args.length;i++) if (args[i].isBlank() || args[i].chars().anyMatch(c -> c<32))
                throw new IllegalArgumentException("Campos obrigatórios, sem caracteres de controle.");
            var linhas = new ArrayList<>(Files.exists(arquivo) ? Files.readAllLines(arquivo) : List.<String>of());
            linhas.removeIf(l -> l.startsWith(args[1]+"\t"));
            linhas.add(String.join("\t", Arrays.copyOfRange(args,1,6)));
            Files.write(arquivo, linhas);
            System.out.println("Fornecedor salvo no sistema externo."); return;
        }
        if (args.length == 2 && args[0].equals("excluir")) {
            var linhas = new ArrayList<>(Files.readAllLines(arquivo));
            linhas.removeIf(l -> l.startsWith(args[1]+"\t")); Files.write(arquivo,linhas); return;
        }
        int porta = Integer.parseInt(System.getenv().getOrDefault("PORT", "8092"));
        String host = System.getenv().getOrDefault("BIND_HOST", "127.0.0.1");
        var server = HttpServer.create(new InetSocketAddress(host,porta),0);
        server.createContext("/fornecedores", exchange -> {
            try {
                if (!exchange.getRequestMethod().equals("GET")) {
                    exchange.getResponseHeaders().set("Allow","GET"); exchange.sendResponseHeaders(405,-1); return;
                }
                var itens = new ArrayList<String>();
                String[] campos = {"codigo","nome","documento","email","telefone"};
                for (String linha : Files.readAllLines(arquivo)) {
                    if (linha.isBlank()) continue;
                    String[] valores=linha.split("\t",-1);
                    if(valores.length!=5) throw new IllegalStateException("Cadastro inválido");
                    var pares=new ArrayList<String>();
                    for(int i=0;i<5;i++) pares.add(escapar(campos[i])+":"+escapar(valores[i]));
                    itens.add("{"+String.join(",",pares)+"}");
                }
                byte[] body=("["+String.join(",",itens)+"]").getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type","application/json; charset=utf-8");
                exchange.getResponseHeaders().set("Cache-Control","no-store");
                exchange.sendResponseHeaders(200,body.length); exchange.getResponseBody().write(body);
            } catch(Exception ex) { exchange.sendResponseHeaders(503,-1); }
            finally { exchange.close(); }
        });
        server.start(); System.out.println("Cadastro externo demonstrativo: http://"+host+":"+porta+"/fornecedores");
    }
}
