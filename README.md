# Estoque Inteligente

Sistema de gestão de estoque físico e sinalização IoT da equipe REUSO, orientado
pela [especificação fornecida](docs/especificacao.pdf). Aplicação Java 21 com
Spring Boot MVC, Thymeleaf, PostgreSQL, MQTT e proxy NGINX. A documentação do
produto define o escopo de implementação; este README descreve a arquitetura,
os padrões aplicados e seus participantes.

## Padrões de projeto e participantes

| Padrão | Classes / componentes | Aplicação |
| --- | --- | --- |
| Singleton | `SistemaConfig` | Uma instância imutável compartilha a identidade da aplicação com as views. |
| Template Method | `OperacaoEstoque`, `EntradaEstoque`, `SaidaEstoque`, `AjusteEstoque` | O método final `executar` aplica os invariantes de saldo; cada operação especializa o cálculo. `EstoqueService` coordena a persistência transacional. |
| Strategy | `Quantidade`, `QuantidadeInteira`, `QuantidadeFracionada` | A validação é selecionada conforme a unidade do produto, com precisão decimal adequada a unidades e quilogramas. |

As responsabilidades seguem MVC. Modelos em `model` representam os conceitos
persistentes; repositórios em `repository` concentram SQL e mapeamento JDBC.
Serviços em `service` aplicam regras, autorização e transações. Controladores em
`controller/web` preparam os modelos das views e selecionam templates; os de
`controller/api` retornam respostas JSON. DTOs transportam formulários e
resultados de consulta sem expor a persistência aos controladores.

`Usuario`, `Produto`, `Loja` e `Posicao` possuem repositórios e serviços próprios.
`ModeloProduto`, `Ocupacao`, `Movimentacao`, `RegistroAuditoria`,
`PermissaoPerfil` e `TentativaLogin` também possuem representação tipada e
repositórios; suas regras ficam nos serviços que coordenam cada caso de uso.
Tabelas internas do Flyway não recebem entidades artificiais.

Templates ficam em `src/main/resources/templates`, com composição por fragmentos
Thymeleaf. CSS e JavaScript ficam em `src/main/resources/static/assets`.
`MqttGateway` cuida do transporte; `IoTService` aplica permissões, interpretação
de peso e confirmação de comandos. `RelatorioService` coordena exportações;
`RelatorioCsv` e `RelatorioPdf` renderizam os arquivos.

## Execução e verificação

Consulte [execução e operação](docs/EXECUCAO.md),
[rastreabilidade dos requisitos](docs/REQUISITOS.md),
[correções necessárias na documentação](docs/PENDENCIAS_DOCUMENTACAO.md) e
[resultados de validação](docs/VALIDACAO.md).

Roteiro pronto, dados iniciais e credenciais públicas: [demonstração MVP](docs/DEMONSTRACAO_MVP.md).

Demonstração completa: `docker compose --env-file .env.demo up -d --build --wait`.
Acesso: `http://localhost:8080/login`.

Desenvolvimento local: inicie `db` e `mqtt` com o Compose e execute
`mvn spring-boot:run "-Dspring-boot.run.profiles=local"`.
Acesso: `http://localhost:8081/login`.

Testes Java: `mvn test` com Java 21 ou superior. Configure TEST_DATABASE_URL
para incluir integração e concorrência em PostgreSQL. Testes ignorados não
comprovam funcionamento. Alterações textuais estão em [registro de mudanças](docs/alteracoes/2026-10-08.md).
