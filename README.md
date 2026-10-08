# Estoque Inteligente — exemplos de padrões

Protótipo acadêmico limitado à atividade de padrões de projeto; não implementa o sistema de estoque completo.

- **Singleton (2):** `SistemaConfig` e `ConexaoBanco`.
- **Template Method (3):** `EntradaEstoque`, `SaidaEstoque` e `AjusteEstoque`, sobre `OperacaoEstoque`.
- **Strategy (3):** `ReposicaoNoMinimo`, `ReposicaoLoteFixo` e `ReposicaoCobertura`, sobre `PoliticaReposicao`.

O terceiro padrão foi escolhido como Strategy por permitir trocar a regra de reposição sem alterar as operações de estoque. As operações usam interfaces de repositório apenas para demonstrar as etapas do template; persistência concreta e API ficam fora desta atividade. Banco SQLite está preparado para uso pela classe singleton `ConexaoBanco`.

Requer Java 21 ou superior e Maven. Execute `mvn test` para verificar as estratégias e operações de estoque e `mvn spring-boot:run` para iniciar a aplicação Spring Boot.

As operações compartilham busca, atualização e registro no Template Method. Entrada e saída recebem uma quantidade positiva; ajuste recebe o saldo final não negativo, inclusive zero, e rejeita saldo sem alteração. As implementações concretas definem o cálculo do saldo e o tipo da movimentação. As interfaces atuais não garantem transação entre atualização do produto e registro do histórico.

As lacunas para evoluir ao sistema completo e as regras que precisam de esclarecimento estão em [Pendências da documentação](docs/PENDENCIAS_DOCUMENTACAO.md).
