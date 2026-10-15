> Registro histórico de 13/10/2026. Em 14/10/2026, os CRUDs de Lojas, Vínculos e Modelos foram concluídos e a Opção 01 recebeu dois Singletons, três aplicações de Template Method e três de Strategy. A suíte atual aprovou 68 testes Java, sem testes ignorados. Consulte [a entrega atual e seus arquivos](OPCAO_01.md) e o [README](../README.md). As lacunas descritas abaixo documentam a situação anterior.

# Parecer da atividade — MVP acadêmico

13/10/2026. Equipe: três integrantes. Escopo: requisitos enviados na conversa.

## Veredito

Ainda não atende integralmente. São necessários seis CRUDs completos; existem três no código. O banco supera o mínimo de três tabelas. A variabilidade já possui implementação demonstrável. A contagem de exemplos dos padrões precisa ser completada e depende da interpretação de exemplo adotada pelo professor.

## Padrões — Opção 01

| Padrão | Evidência atual | Falta |
| --- | --- | --- |
| Singleton, dois exemplos | SistemaConfig, construtor privado, INSTANCE e getInstance; usado por ContextoViewAdvice | Um segundo exemplo explícito |
| Template Method, três exemplos | OperacaoEstoque.executar final; EntradaEstoque, SaidaEstoque, AjusteEstoque | Há três variantes de uma aplicação; suficiente apenas se variantes contarem |
| Outro padrão, três exemplos | Strategy: QuantidadeInteira e QuantidadeFracionada, selecionadas em EstoqueService | Terceira estratégia, se variantes contarem |

Exemplo de Template Method: saldo 10, entrada 5 -> 15; saída 3 -> 12; ajuste para 8 -> 8. O método comum valida entradas e resultado; subclasses especializam calcular. Strategy: un aceita 2 e rejeita 2.5; kg aceita 1.250 e rejeita mais de três casas decimais. Singleton: chamadas repetidas de getInstance retornam a mesma referência; duas chamadas não são dois exemplos distintos.

Se exemplos significarem aplicações independentes, as famílias atuais contam como um Template Method e um Strategy: faltam duas aplicações de cada, além do segundo Singleton. Beans Spring não foram contados automaticamente como Singletons GoF codificados. Classes antigas removidas no git status não foram contadas.

A Opção 02 não está comprovada: não há três outros padrões explicitamente demonstrados com dois exemplos cada. A Opção 01 reaproveita mais do código. Não inventar unidades de estoque ou alterar o esquema apenas para atingir uma contagem; usar responsabilidade real ou exemplos didáticos separados, se aceitos pela disciplina.

## Telas — seis necessárias

| Cadastro | Rota | Situação |
| --- | --- | --- |
| Produtos | /produtos | Criar, consultar, editar e excluir logicamente |
| Posições | /posicoes | Criar, consultar, editar e excluir logicamente |
| Usuários | /usuarios | Criar, consultar, editar e desativar |
| Lojas | /global | Falta exclusão/desativação |
| Vínculos | /vinculos | Criar, listar e remover; falta edição explícita |
| Modelos de produto | Sem tela própria | Tabela existente, sem CRUD próprio |

Controller -> serviço -> repositório -> PostgreSQL está presente nos três completos. Produtos e posições usam active=false; usuários são desativados e suas sessões revogadas. Exclusão lógica preserva histórico. Não contar login, dashboard, relatórios ou formulário de edição da mesma entidade como CRUDs adicionais.

MVP proposto: completar Lojas, completar Vínculos e criar tela de Modelos. Divisão sugerida: integrante 1, Produtos/Modelos; integrante 2, Posições/Vínculos; integrante 3, Usuários/Lojas. Isso é sugestão de entrega, não atribuição da autoria existente.

Lojas: desativação persistida por migração, filtros de seleção/login e bloqueio de acesso de sessões existentes. Vínculos: editar associação somente com saldo zero, validando unidade/loja e substituindo em transação auditada; não transferir saldo silenciosamente. Modelos: CRUD de nome/marca/categoria sobre models; bloquear exclusão quando houver referência, inclusive de produto inativo. Usar modelo sem referências para demonstrar exclusão. Se o professor não aceitar vínculo como cadastro próprio, substituí-lo por outro cadastro simples pertinente ao domínio.

## Banco e variabilidade

V1__estoque.sql cria dez tabelas de domínio: tenants, users, role_permissions, models, products, positions, occupancy, movements, audit e login_attempts. flyway_schema_history não entrou nessa contagem.

| Variante | IoT | Relatórios/ocupação | Fracionados |
| --- | --- | --- | --- |
| essencial | Não | Não | Não |
| calcados | Sim | Sim | Não |
| granel | Sim | Sim | Sim |

Planejamento de LPS apresentável: núcleo obrigatório de cadastro/posições/vínculos/movimentação; opcionais IoT, relatórios/ocupação e fracionados; escolha por loja em tempo de execução; persistência nas flags iot, enterprise e fractional de tenants. Acesso.recurso verifica recursos no backend e templates ajustam navegação. Produto kg exige fractional; relatórios/ocupação exigem enterprise. Loja com produtos kg ativos não permite desabilitar fractional. Essas configurações demo não impõem dependência obrigatória entre todos os opcionais.

No vídeo, comparar essencial e granel usando o mesmo perfil admin, para mostrar variabilidade do produto e não diferença de permissão do usuário.

## Preparação antes de gravar

Abrir Docker Desktop e executar na raiz:

~~~powershell
docker compose --env-file .env.demo up -d --build --wait
docker compose --env-file .env.demo ps
docker compose --env-file .env.demo --profile check run --rm verify
~~~

Abrir http://localhost:8080/login. Usar as contas locais descritas em EXECUCAO.md. As credenciais públicas padronizadas estão em DEMONSTRACAO_MVP.md. A reconstrução deve concluir com sucesso; testes ignorados não equivalem a aprovados.

Flyway aplica a migração automaticamente. Com demonstração habilitada, DemonstracaoService provisiona três lojas, quatro perfis por loja e superadmin. Em banco novo, cada loja recebe quatro produtos, oito posições, quatro vínculos, saldos 6/9/12/15, movimentações e auditoria. Banco já usado pode conter mais ou menos dados: lojas existentes são preservadas, sem redefinição automática de suas senhas ou reposição dos dados.

Não executar SQL legado Flask nem inserir usuários manualmente. Para conferir esquema/dados:

~~~powershell
docker compose --env-file .env.demo exec db psql -U estoque -d estoque
~~~

Dentro do psql:

~~~sql
SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename;
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
SELECT slug, iot, enterprise, fractional FROM tenants ORDER BY slug;
SELECT t.slug, count(*) AS produtos_ativos
FROM products p JOIN tenants t ON t.id=p.tenant_id
WHERE p.active GROUP BY t.slug ORDER BY t.slug;
~~~

Reservar códigos novos como VIDEO-20261013-01 para produto e VIDEO-P01 para posição. Exclusão lógica não libera necessariamente SKU/código. Para exclusão, usar registros sem saldo e usuário descartável que não seja o próprio admin nem o último administrador. Para movimentação, preparar outro produto un, posição un e vínculo de saldo zero. Para demonstrar variabilidade, confirmar essencial sem recursos opcionais e granel com fractional habilitado. Não apagar volumes para reiniciar dados.

## Roteiro do vídeo

1. Apresentar equipe e duas telas por integrante, depois de concluir as três faltantes.
2. Mostrar tabelas no PostgreSQL e relações entre lojas, modelos, produtos, posições e vínculos.
3. Em cada um dos seis CRUDs: criar, localizar, editar, recarregar, excluir/desativar e conferir resultado. Mostrar campos persistidos e active=false quando aplicável. Não exibir hashes de senha.
4. Criar um registro marcador, reiniciar somente app, aguardar /health, entrar novamente e mostrar persistência. Comando: docker compose --env-file .env.demo restart app.
5. Em vínculo inicialmente zerado, registrar entrada 10, saída 3, ajuste para 8; mostrar histórico e código do Template Method.
6. Demonstrar Singleton e Strategy, incluindo os exemplos que ainda precisam ser adicionados; mostrar comportamento e participantes, não só nomes de arquivos.
7. Exibir matriz de variabilidade e comparar essencial/granel como admin; mostrar recurso fracionado habilitado somente na variante correspondente e explicar núcleo comum/opcionais/configuração.

## Próximos passos e critério de parada

1. Fechar interpretação de exemplos de padrões e de telas CRUD.
2. Completar três CRUDs reaproveitando tabelas e fluxos existentes.
3. Completar a Opção 01 conforme essa interpretação.
4. Reconstruir e verificar código atual, incluindo recuperação de sessão e saldo inicial de grade se permanecerem na versão apresentada.
5. Ensaiar e gravar exatamente a versão validada.

Parar quando houver seis CRUDs demonstrados, contagem de padrões aceita e demonstrada, pelo menos três tabelas e variabilidade planejada/documentada funcionando. Alexa externa, hardware físico, refinamento visual, carga, disponibilidade e publicação na internet não são prioridade desse recorte. Simulação IoT não comprova hardware.

## Evidências e limites

Inspeção dos fontes, templates, serviços, repositórios e migração realizada nesta sessão. GET /health e /login em localhost:8080 retornaram 200; login listou as três lojas demo e uma loja antiga de testes. Não houve alteração de código funcional ou dados de negócio.

Acesso ao Compose indisponível nesta sessão: leitura da configuração Docker negada. Maven offline foi executado pelo Java local e terminou com BUILD FAILURE na compilação dos 84 fontes, apontando target/m2/org/yaml/snakeyaml/2.4/snakeyaml-2.4.jar. A fase de testes não foi alcançada. Essa falha impede certificar o build atual; não é um teste funcional reprovado. Não foi realizado ensaio visual completo ou teste atual de persistência após reinício.

Evidências HTTP de 09/10/2026 referem-se a imagem anterior, com diferenças documentadas para o fonte. Não certificam as alterações atuais. O mapa automático Cartographer está desatualizado; o parecer usa inspeção do código real. Os três CRUDs completos são confirmados no código, não certificados integralmente em execução nesta sessão.
