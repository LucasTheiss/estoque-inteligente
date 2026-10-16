# Estoque Inteligente

Sistema de gestão de estoque físico e sinalização IoT da equipe REUSO, orientado
pela [especificação fornecida](docs/especificacao.pdf). Aplicação Java 21 com
Spring Boot MVC, Thymeleaf, PostgreSQL, MQTT e proxy NGINX. A documentação do
produto define o escopo de implementação; este README descreve a arquitetura,
os padrões aplicados e seus participantes.

## Padrões de projeto e participantes

| Padrão | Classes / componentes | Aplicação |
| --- | --- | --- |
| Singleton — 2 aplicações | `SistemaConfig`; `CatalogoPermissoes` | Identidade compartilhada nas views e catálogo imutável compartilhado pela autorização, provisionamento e telas. Ambos possuem construtor privado e instância única explícita. |
| Template Method — 3 aplicações | `OperacaoEstoque`; `CadastroTemplate`; `RelatorioTemplate` | Sequência fixa para movimentações; autorização/preparação/persistência/auditoria de cadastros; validação/renderização/montagem de relatórios. |
| Strategy — 3 aplicações | `Quantidade`; `CalculoEstoque`; `FormatoRelatorio` | Políticas de precisão; operações escolhidas pelo tipo de movimento; renderizadores escolhidos pelo formato da exportação. |

Entrega pela **Opção 01**. Veja [arquivos, alterações e roteiro dos padrões](docs/OPCAO_01.md).
Para estudar a prova de autoria e conferir cada requisito, consulte o
[guia de autoria e empacotamento para reuso](docs/GUIA_AUTORIA_REUSO.md).
O [componente de regras](docs/reuso/README.md) pode ser gerado e testado com
`powershell -NoProfile -File scripts/empacotar-reuso.ps1` (JDK 21+).
As três aplicações de cada padrão comportamental estão integradas aos fluxos reais.
Template Method fixa etapas por herança; Strategy permite ao cliente escolher um comportamento pelo contrato.
O fluxo de estoque e o de relatórios combinam os dois padrões, com papéis distintos.

## Seis CRUDs e permissões

| Cadastro | Rota | Autorização |
| --- | --- | --- |
| Produtos | `/produtos` | Permissão `cadastros` da loja |
| Modelos de produto | `/modelos` | Permissão `cadastros` da loja |
| Posições | `/posicoes` | Permissão `cadastros` da loja |
| Vínculos | `/vinculos` | Permissão `cadastros` da loja |
| Usuários | `/usuarios` | Permissão `usuarios`, com proteção do próprio usuário e do último administrador |
| Lojas | `/global` | Exclusivamente superadministrador |

Os cadastros permitem criar, consultar, editar e excluir/desativar. As permissões
são verificadas no backend, incluindo requisições diretas. Na matriz inicial,
admin, gerente e estoquista possuem `cadastros`; vendedor não possui. Alterações
na matriz passam a valer também para Modelos e Vínculos. Superadmin pode operar
os cadastros após selecionar uma loja ativa.

Editar vínculo exige saldo zero e unidades compatíveis; a troca é transacional,
auditada e preserva movimentações anteriores. Modelo referenciado por produto,
mesmo inativo, não pode ser excluído. Desativar loja preserva dados, remove a loja
do login e bloqueia sessões existentes. A migração `V2__lojas_ativas.sql` atualiza
bancos existentes automaticamente, sem recriar tabelas nem apagar dados.

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

Pré-requisitos: Docker Desktop instalado e iniciado, com containers Linux, e
terminal aberto na raiz do projeto. Java e Maven locais não são necessários.

### Subir e popular o banco

```powershell
docker compose --env-file .env.demo up -d --build --wait
docker compose --env-file .env.demo ps
```

Abra **http://localhost:8080/login**. Esse comando compila a aplicação Java 21,
inicia PostgreSQL, MQTT e NGINX, aplica todas as migrações Flyway e popula o banco.
`.env.demo` habilita o provisionamento e o simulador IoT.

Na primeira inicialização, são criadas as lojas `essencial`, `calcados` e `granel`,
quatro perfis por loja e um superadmin: 13 usuários, 12 modelos, 12 produtos,
24 posições, 12 vínculos e suas entradas iniciais, permissões e auditoria.

| Acesso de demonstração | Usuário | Senha |
| --- | --- | --- |
| Qualquer loja demo | `admin`, `gerente`, `estoquista` ou `vendedor` | `Loja-demo-local-2026!` |
| Administração global | `superadmin` | `Global-demo-local-2026!` |

Reexecutar o comando preserva lojas existentes, senhas e alterações. Lojas demo
ausentes são criadas; lojas existentes não são repovoadas nem reativadas. Use
uma loja descartável para demonstrar desativação. O volume persiste entre reinícios.
Não use `down -v` para reiniciar: esse comando apagaria o banco. Um volume antigo
mantém sua senha PostgreSQL; mudar `.env.demo` não troca a senha daquele volume.

### Testar e conferir a população

```powershell
docker compose --env-file .env.demo --profile check run --rm verify
docker compose --env-file .env.demo exec db psql -U estoque -d estoque -c "SELECT slug, active, iot, reports, occupancy, fractional FROM tenants ORDER BY slug;"
docker compose --env-file .env.demo exec db psql -U estoque -d estoque -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

`verify` executa os testes Java, inclusive CRUDs, permissões, isolamento,
transações, padrões e MQTT. Os testes de integração usam esquemas exclusivos,
separados dos dados da demonstração. Não basta executar `mvn test` sem banco:
os testes que dependem de `TEST_DATABASE_URL` seriam ignorados.

### Atualizar, acompanhar e parar

```powershell
# Depois de modificar o código:
docker compose --env-file .env.demo up -d --build --wait
# Logs:
docker compose --env-file .env.demo logs --tail=100 app
# Reiniciar somente a aplicação, preservando registros:
docker compose --env-file .env.demo restart app
# Parar os serviços, preservando o volume:
docker compose --env-file .env.demo down
```

Atalhos PowerShell equivalentes: `scripts/demo.ps1` e `scripts/test.ps1`.

Desenvolvimento local: inicie `db` e `mqtt` com o Compose e execute
`mvn spring-boot:run "-Dspring-boot.run.profiles=local"`.
Acesso: `http://localhost:8081/login`.

Testes Java: `mvn test` com Java 21 ou superior. Configure TEST_DATABASE_URL
para incluir integração e concorrência em PostgreSQL. Testes ignorados não
comprovam funcionamento. Alterações textuais estão em [registro de mudanças](docs/alteracoes/2026-10-08.md).
