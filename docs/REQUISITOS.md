# Rastreabilidade da atividade acadêmica

Estado conferido em 15/10/2026. A implementação atual da atividade está registrada
na [Opção 01](OPCAO_01.md). Os registros anteriores neste repositório descrevem
etapas históricas e não representam o estado atual.

## Rubrica da entrega

| Requisito | Implementação atual | Evidência |
| --- | --- | --- |
| Ao menos 10 classes principais com atributos e métodos | Atendido. A aplicação Java contém modelos, DTOs, serviços, repositórios e controllers; exemplos de domínio incluem `Produto`, `Posicao`, `Usuario`, `Loja`, `Movimentacao`, `Ocupacao`, `ModeloProduto`, `RegistroAuditoria`, `PermissaoPerfil` e `TentativaLogin`. | `src/main/java/br/com/estoque/` |
| Singleton: 2 exemplos | Atendido por `SistemaConfig` e `CatalogoPermissoes`, ambos com instância explícita, construtor privado e `getInstance()`. | `config/SistemaConfig.java`, `config/CatalogoPermissoes.java`, `PadroesTest.java` |
| Template Method: 3 exemplos | Atendido por `OperacaoEstoque`, `CadastroTemplate` e `RelatorioTemplate`, em três fluxos independentes. | `service/OperacaoEstoque.java`, `service/CadastroTemplate.java`, `view/RelatorioTemplate.java` |
| Um novo padrão: 3 exemplos | Atendido pela opção Strategy: políticas de quantidade/preço, cálculo das operações de estoque e formato de relatório. | `strategy/`, `EstoqueService.java`, `RelatorioService.java`, `PadroesTest.java` |
| Mínimo de 2 CRUDs por integrante (3 integrantes) | Atendido com seis cadastros: Produtos, Modelos, Posições, Vínculos, Usuários e Lojas. Cada fluxo possui tela, controller, serviço e persistência no banco. | `docs/OPCAO_01.md`; templates em `src/main/resources/templates/`; controllers em `src/main/java/br/com/estoque/controller/web/` |
| Pelo menos 3 tabelas | Atendido: a migração inicial cria dez tabelas de domínio. | `src/main/resources/db/migration/V1__estoque.sql` |
| Exemplo de planejamento de variabilidade | Atendido com núcleo comum de estoque e recursos opcionais independentes por loja: IoT, relatórios, ocupação e quantidades fracionadas. As flags são persistidas em `tenants` e verificadas no backend. | `docs/OPCAO_01.md`, `LojaForm.java`, `Acesso.java`, `global.html` |

## Divisão sugerida dos CRUDs

| Integrante | CRUDs para demonstrar |
| --- | --- |
| 1 | Produtos e Modelos |
| 2 | Posições e Vínculos |
| 3 | Usuários e Lojas |

Substituir os números pelos nomes reais dos integrantes na apresentação. A
divisão indica cobertura da demonstração, não autoria individual já registrada.

## Validação disponível

O registro da Opção 01 informa 68 testes Java aprovados, sem falhas, erros ou
testes ignorados, incluindo testes com PostgreSQL e MQTT, além de nove testes
JavaScript. Também há evidências de build Docker, inicialização, conferência do
banco e revisão de telas no navegador. Consulte
[evidências da Opção 01](../evidence/2026-10-14/opcao-01/README.md) e
[histórico e limites dos testes](VALIDACAO.md).

Para a apresentação, ainda é necessário identificar a equipe e demonstrar os
fluxos CRUD e a variabilidade. Isso é preparação da entrega, não funcionalidade
pendente no código.

## Limites que não alteram o aceite desta atividade

Alexa externa, hardware IoT físico, medições de carga/disponibilidade e
publicação na internet não foram comprovados pelas evidências da Opção 01. A
integração IoT demonstrada usa simulador. Esses itens pertencem ao escopo maior
do produto e não são necessários para comprovar os requisitos acadêmicos
listados acima.
