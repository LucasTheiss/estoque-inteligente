# Estoque Inteligente

Sistema de gestão de estoque físico e sinalização IoT da equipe REUSO, orientado
pela [especificação fornecida](docs/especificacao.pdf). Backend Flask/Python,
templates Jinja2, PostgreSQL, MQTT e proxy NGINX. A documentação do produto, e não
as limitações do antigo exemplo Java, define o escopo de implementação.

## Padrões de projeto e participantes

| Padrão | Classes / componentes | Aplicação |
| --- | --- | --- |
| Template Method | `OperacaoEstoque`, `EntradaEstoque`, `SaidaEstoque`, `AjusteEstoque` (`estoque/domain.py`) | O método `executar` aplica os invariantes de saldo; cada operação especializa o cálculo. |
| Strategy | `QuantidadeInteira`, `QuantidadeFracionada` | Validação conforme a unidade do produto. A mesma transação atende calçados e produtos a granel. |
| Adapter | `BalancaAdapter`, `MqttAdapter` | Converte peso/tara em quantidade e subtotal precisos; adapta comandos e telemetria ao MQTT. |
| Service Layer / Unit of Work | `EstoqueService`, contexto transacional `psycopg.Connection` | Grava saldo, histórico e auditoria na mesma transação, com controle concorrente. |
| Application Factory | `create_app` | Instâncias Flask configuráveis para execução e testes, com módulos separados. |
| Decorator | `require` | Autenticação, permissões e habilitação de recursos por loja no servidor. |
| Reúso por composição | `base.html`, `macros.html`, módulo `catalog` | Estrutura visual, formulários, consultas e cadastros compartilhados. |

As classes Java originais permanecem em `src/` como exemplos acadêmicos:
Singleton (`SistemaConfig`, `ConexaoBanco`), Template Method (`OperacaoEstoque` e
suas três subclasses) e Strategy (`PoliticaReposicao`, `ReposicaoNoMinimo`,
`ReposicaoLoteFixo`, `ReposicaoCobertura`). O aplicativo web operacional utiliza
a arquitetura Python/PostgreSQL definida na seção 3.3 do PDF.

## Execução e verificação

Consulte [execução e operação](docs/EXECUCAO.md),
[rastreabilidade dos requisitos](docs/REQUISITOS.md) e
[correções necessárias na documentação](docs/PENDENCIAS_DOCUMENTACAO.md).

Testes Python: `python -m pytest tests -q` (configure `TEST_DATABASE_URL` para
incluir integração e concorrência em PostgreSQL).
Exemplos Java: `mvn test` com Java 21 ou superior.
