# Estoque Inteligente — exemplos de padrões

Protótipo acadêmico limitado à atividade de padrões de projeto; não implementa o sistema de estoque completo.

- **Singleton (2):** `SistemaConfig` e `ConexaoBanco`.
- **Template Method (3):** `EntradaEstoque`, `SaidaEstoque` e `AjusteEstoque`, sobre `OperacaoEstoque`.
- **Strategy (3):** `ReposicaoNoMinimo`, `ReposicaoLoteFixo` e `ReposicaoCobertura`, sobre `PoliticaReposicao`.

O terceiro padrão foi escolhido como Strategy por permitir trocar a regra de reposição sem alterar as operações de estoque. As operações usam interfaces de repositório apenas para demonstrar as etapas do template; persistência concreta e API ficam fora desta atividade. Banco SQLite está preparado para uso pela classe singleton `ConexaoBanco`.

Requer Java 21 e Maven. Execute `mvn test` para verificar o exemplo de Strategy e `mvn spring-boot:run` para iniciar a aplicação Spring Boot.
