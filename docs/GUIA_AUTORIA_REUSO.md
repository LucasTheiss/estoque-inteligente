# Requisitos e prova de autoria - entrega sem vídeo

Conferência de 16/10/2026. Escopo: itens 01, 02 e empacotamento do item 03
enviados pelo aluno. Vídeo excluído por solicitação. Equipe de três integrantes
conforme `OPCAO_01.md`: seis CRUDs cobrem dois por pessoa; se a equipe for maior,
essa quantidade não basta. A distribuição abaixo é sugestão, não declaração de autoria.

## Material da disciplina e interpretação

- [Componentes de Reuso - Parte 01](conteudo/Componentes%20de%20Reuso%20-%20Parte%2001.pdf),
  p. 2: dez classes com métodos e atributos, padrões e requisitos iniciais de LPS.
  Não exige dez classes abstratas.
- [Design Patterns para Reuso](conteudo/Design%20Patterns%20para%20Reuso.pdf),
  pp. 4-9: Strategy; pp. 10-15: Singleton; pp. 16-21: Template Method; p. 22:
  contagens da atividade. Strategy é o terceiro padrão escolhido, além dos dois
  obrigatórios da Opção 01; não se trata de inventar um padrão inédito.
- [Componentes de Reuso - Parte 02](conteudo/Componentes%20de%20Reuso%20-%20Parte%2002.pdf),
  pp. 2-6: empacotar significa distribuir código com contrato, versão,
  dependências e documentação; p. 7: item 03. Portanto, apenas remover linhas
  duplicadas não demonstra todo o conteúdo de empacotamento desta aula.
- `02.pdf`, pp. 13-16, e `Aula 02 03 04- Nova.pdf`: variabilidade, ativos comuns
  e variáveis e pontos de variação. Aplicação abaixo: recursos por loja.
- `05 Projeto Medição-Reuso.pdf`, p. 2, menciona seis ativos no projeto mais amplo.
  Isso não acrescenta seis novos módulos ao enunciado enviado. Se também for
  cobrado, seis ativos existentes são: regras numéricas, cálculo de estoque,
  template de cadastro, geração de relatórios, layout Thymeleaf e testes de padrões.
  Reuso interno desses ativos não significa que todos já sejam bibliotecas independentes.

## Situação por requisito

| Exigência | Evidência no projeto | Situação |
| --- | --- | --- |
| 10 classes com atributos e métodos | Dez classes concretas relacionadas abaixo | Implementado |
| 2 Singletons | SistemaConfig e CatalogoPermissoes | Implementado |
| 3 Template Methods | OperacaoEstoque, CadastroTemplate, RelatorioTemplate | Implementado em três processos |
| 3 exemplos de outro padrão | Strategy: Quantidade, CalculoEstoque, FormatoRelatorio | Implementado em três famílias |
| 2 CRUDs por integrante | Produtos, Modelos, Posições, Vínculos, Usuários e Lojas | Implementado para três integrantes |
| 3 ou mais tabelas | Dez tabelas de domínio em V1; V2 acrescenta ativação de lojas | Implementado |
| Variabilidade planejada | Núcleo comum e flags iot, reports, occupancy, fractional por loja | Implementado; matriz abaixo |
| Repositório | Git local e origin configurado para GitHub | Existência local confirmada; acesso remoto não validado nesta sessão |
| Empacotamento para reuso | Spring Boot JAR/Docker já existiam; novo JAR de regras e consumidor isolado | Gerado e consumo verificado |

Esta avaliação é do código e das verificações indicadas no final. Não substitui
o ensaio dos CRUDs com PostgreSQL nem determina a nota atribuída pelo professor.

## Dez classes: o que mostrar

Os caminhos desta seção partem de `src/main/java/br/com/estoque/`.
Repositórios injetados são atributos da classe; seus métodos não ficam nas entidades
apenas para preencher contagem. Os dez exemplos abaixo têm atributos e métodos explícitos.

| Arquivo | Atributos para apontar | Métodos e função efetiva |
| --- | --- | --- |
| config/SistemaConfig.java | INSTANCE, valores | getInstance fornece instância; get recupera configuração, atualmente o nome da aplicação |
| config/CatalogoPermissoes.java | INSTANCE, perfis, permissoes | perfis e permissoes fornecem catálogo imutável de perfis e ações reconhecidas |
| service/ProdutoService.java | produtos, modelos, vinculos, estoque | pesquisar/buscar/salvar/excluir gerenciam SKU, variantes, unidade, preço e atributos; grade pode criar vínculos e entradas iniciais |
| service/PosicaoService.java | posicoes, ocupacoes, acesso | salvar/preparar/persistir gerenciam locais físicos e validam capacidade, unidade, coordenadas e dispositivo; excluir exige saldo zero |
| service/ModeloProdutoService.java | modelos, acesso, auditoria | salvar valida nome/marca/categoria; excluir impede apagar modelo referenciado por produto |
| service/VinculoService.java | produtos, posicoes, ocupacoes | salvar associa produto à posição com unidade compatível; editar/remover exigem vínculo vazio |
| service/UsuarioService.java | usuarios, lojas, senhas | salvar cria/edita/desativa contas, aplica hash de senha e protege o último administrador e o próprio perfil |
| service/LojaService.java | lojas, usuarios, permissoes | salvar cria loja com administrador/permissões ou altera recursos; excluir desativa preservando dados |
| service/EstoqueService.java | OPERACOES, ocupacoes, movimentos | movimentar/estornar autorizam, validam e coordenam saldo, idempotência, transação, histórico e auditoria |
| service/RelatorioService.java | formatos, historico, acesso | exportar verifica permissão/recurso, filtra histórico, escolhe formato e audita geração |

## “Me mostre um Singleton”

Abra [SistemaConfig](../src/main/java/br/com/estoque/config/SistemaConfig.java).
Explique na ordem:

1. O construtor `private` impede `new SistemaConfig()` por código externo.
2. `private static final INSTANCE = new SistemaConfig()` cria a instância na
   inicialização da classe. É inicialização antecipada, não lazy.
3. `getInstance()` retorna sempre a mesma referência nesse carregamento da classe.
4. `get("sistema.nome")` fornece "Estoque Inteligente". `ContextoViewAdvice`
   coloca esse valor em `appName` para as páginas. Não gerencia conexão de banco.
5. `Map.of` torna as configurações imutáveis; não há estado de sessão no Singleton.

Segundo exemplo: [CatalogoPermissoes](../src/main/java/br/com/estoque/config/CatalogoPermissoes.java).
Mesma estrutura de instância, função diferente: guarda perfis e descrições de
permissões. `Acesso.PERFIS` e `Acesso.PERMISSOES` usam esse catálogo. As concessões
reais por perfil/loja vêm do banco, não ficam armazenadas globalmente nele.

Prova: `PadroesTest.singletonsCompartilhamIdentidadeECatalogoImutavel()` usa
`assertSame` e tenta modificar as coleções, esperando rejeição. Duas chamadas ao
mesmo getInstance não contam como dois exemplos: são duas classes distintas.
Singleton aqui é por carregador de classes da JVM, não uma instância universal
entre servidores. `final` na referência não torna um objeto mutável imutável;
neste projeto a imutabilidade vem também das coleções utilizadas.

## “Explique os três Template Methods”

### Estoque

Abra [OperacaoEstoque](../src/main/java/br/com/estoque/service/OperacaoEstoque.java)
e depois `EntradaEstoque`, `SaidaEstoque`, `AjusteEstoque`.
`executar(saldo, quantidade)` é o método template, `final`: valida entradas,
chama `calcular`, rejeita saldo negativo e ausência de mudança. `calcular` é
abstrato; subclasses fornecem soma, subtração ou substituição. A chamada é
resolvida por polimorfismo. O método base não precisa saber qual fórmula foi escolhida.
Exemplo: saldo 10, quantidade 3 -> entrada 13, saída 7, ajuste 3.
As classes calculam; `EstoqueService` coordena a gravação e os controles de acesso.

### Cadastros

Abra [CadastroTemplate](../src/main/java/br/com/estoque/service/CadastroTemplate.java),
`PosicaoService` e `ModeloProdutoService`. O método final `executarCadastro`
exige permissão, chama `preparar`, chama `persistir` e registra auditoria.
As subclasses implementam os dois métodos abstratos. `F` é o formulário de
entrada, `E` é a entidade validada. Os métodos públicos `salvar`, anotados com
`@Transactional`, iniciam a transação antes da chamada ao template protegido.
Não diga que todo CRUD usa esse template: ele organiza o salvamento desses dois cadastros.

### Relatórios

Abra [RelatorioTemplate](../src/main/java/br/com/estoque/view/RelatorioTemplate.java),
`RelatorioCsv` e `RelatorioPdf`. O método final `gerar` valida loja/lista,
copia os registros, rejeita mistura de lojas, chama `renderizarConteudo` e monta
`RelatorioArquivo` com bytes, nome e tipo MIME. O hook abstrato varia por formato.
CSV trata escape de células; PDF organiza páginas. A autorização fica em
`RelatorioService`, antes da geração. São três processos-base diferentes, não
uma contagem de três subclasses do mesmo processo.

## “Qual foi o terceiro padrão e como funciona?”

Strategy define comportamentos por um contrato comum. O cliente escolhe uma
implementação e delega a execução. Mostre interface, implementações e cliente:

| Família | Contrato | Implementações | Cliente e escolha |
| --- | --- | --- | --- |
| Precisão | strategy/Quantidade.java: validar(String) | QuantidadeInteira, QuantidadeFracionada, QuantidadeMonetaria | EstoqueService escolhe por un/kg; ProdutoService usa monetária para preço |
| Operação | strategy/CalculoEstoque.java: executar(saldo, quantidade) | EntradaEstoque, SaidaEstoque, AjusteEstoque, através de OperacaoEstoque | EstoqueService.OPERACOES seleciona pela chave entrada/saida/ajuste |
| Exportação | strategy/FormatoRelatorio.java: gerar(loja, registros) | RelatorioCsv e RelatorioPdf, através de RelatorioTemplate | RelatorioService.formatos seleciona csv/pdf |

Precisão: un rejeita 0.5; kg aceita 0.125; preço aceita 12.50 e rejeita 12.501.
Usa BigDecimal para aritmética decimal. Não arredonda silenciosamente entradas
com precisão excessiva. QuantidadeMonetaria valida preço, não cria unidade monetária de estoque.

Pergunta provável: “Por que estoque e relatório têm dois padrões?”
Resposta: Strategy determina qual objeto/algoritmo o cliente seleciona por
composição; Template Method determina a sequência fixa dentro desse algoritmo
por herança. Uma mesma classe pode participar dos dois papéis.

## CRUDs, banco e demonstração

| Integrante sugerido | CRUD e rota | Dados persistidos |
| --- | --- | --- |
| 1 | Produtos /produtos; Modelos /modelos | products; models |
| 2 | Posições /posicoes; Vínculos /vinculos | positions; occupancy |
| 3 | Usuários /usuarios; Lojas /global | users; tenants |

Cada fluxo tem Controller -> Service -> Repository (JdbcTemplate/SQL) -> PostgreSQL,
com template em `src/main/resources/templates`. Demonstre criar, consultar,
editar, recarregar e excluir/desativar. Usuários/lojas/produtos/posições podem
usar desativação para preservar referências. Vínculo é uma associação com
saldo, não apenas uma tela auxiliar; possui criação, consulta, edição e remoção.
Não conte login, dashboard ou formulário de edição como novos CRUDs.

Dez tabelas em `src/main/resources/db/migration/V1__estoque.sql`:
tenants (lojas), users (contas), role_permissions (concessões), models (modelos),
products (SKUs), positions (locais), occupancy (produto/posição/saldo), movements
(histórico), audit (ações rastreáveis), login_attempts (controle de tentativas).
`V2__lojas_ativas.sql` adiciona ativação de loja. Tabela interna do Flyway não entra na contagem.

Para movimentar um produto novo, crie posição de mesma unidade e vínculo. O
saldo pertence a produto + posição. Ajuste recebe o novo total; não é entrada.
Em vínculo zerado, faça entrada 10, saída 3, ajuste 8 -> 10, 7, 8 e mostre histórico.

## Planejamento da variabilidade

Domínio: estoque multi-loja. Núcleo obrigatório: cadastros, posições, vínculos,
movimentações e controle de acesso. Pontos opcionais independentes: IoT, relatórios,
ocupação e quantidades fracionadas. A seleção ocorre por configuração de loja em tempo
de execução, sem criar outra implementação do sistema.

| Variante demo | iot | reports | occupancy | fractional |
| --- | --- | --- | --- | --- |
| essencial — Almoxarifado Base | não | não | não | não |
| calcados — Calçados Aurora | sim | sim | sim | não |
| granel — Mercado Raiz | sim | sim | sim | sim |

As flags ficam em tenants; `LojaService.salvar` persiste a seleção;
`Acesso.recurso` bloqueia funcionalidades desabilitadas; templates adaptam a
interface. Produtos kg exigem fractional; exportação exige reports; ocupação exige occupancy.
No painel global, os checkboxes Relatórios (CSV/PDF) e Ocupação do estoque são
independentes. As quatro combinações são permitidas. A permissão de usuário
`relatorios` continua necessária nos dois casos. V3 preserva a configuração
antiga copiando enterprise para ambos; a seed não sobrescreve lojas existentes.
Não é permitido desligar fractional com produtos fracionados ativos. Leitura
de balança na tela exige IoT e fracionados. As três variantes demo não implicam
que todos os opcionais dependam obrigatoriamente uns dos outros.
Compare essencial/granel com o mesmo perfil admin: isso demonstra configuração
do produto, não diferença de permissão. Recursos e permissões são controles distintos.

## Item 03: empacotamento concreto e prova de consumo

A aplicação já possuía POM versionado e Dockerfile que empacota Spring Boot.
O complemento é um JAR comum das regras de cálculo, sem framework ou banco,
com documentação e versão do POM no manifesto. Usa as mesmas nove fontes da
aplicação; não há cópia mantida da lógica nem mudança das regras de negócio.

Execute `powershell -NoProfile -File scripts/empacotar-reuso.ps1` com JDK 21+.
Abra [o contrato e as instruções do componente](reuso/README.md) e
[o consumidor independente](../examples/reuso/ConsumidorEstoque.java).
Mostre as chamadas a Quantidade e CalculoEstoque, o resultado e as rejeições.
O consumidor compila com o JAR no classpath, não com os fontes da aplicação.

“O que foi reutilizado?” Validação de valores e algoritmos de saldo.
“Como?” Empacotamento dos tipos existentes em JAR e dependência via classpath.
“O que o JAR não faz?” Autenticação, transação, banco, idempotência e auditoria.
“Qual a dependência?” Java 21+, sem bibliotecas externas.
“A API é o quê?” Os contratos públicos Quantidade.validar e CalculoEstoque.executar,
com BigDecimal como resultado e IllegalArgumentException em entradas inválidas.

## Limites de validação

O empacotamento e o consumidor foram executados em Java 23 com bytecode alvo 21.
O teste cobre entrada, saída, ajuste, zerar, precisão e rejeições. A primeira
execução isolada teve erro de acesso do javac ao JAR; a repetição com acesso
autorizado terminou sem esse erro. Não se alterou o código para contorná-lo.
Também passaram **48 testes locais**, sem falhas, erros ou ignorados:
OperacaoEstoqueTest (2), PadroesTest (4), ProdutoServiceTest (10) e TelasTest (32).
Comando: `mvn -o -Dmaven.repo.local=target/m2 -Dtest=PadroesTest,OperacaoEstoqueTest,ProdutoServiceTest,TelasTest test`.
O build Maven foi incremental; o componente foi compilado em diretório novo.
TelasTest usa serviços simulados, portanto não comprova persistência real.
Docker indisponível: testes atuais de PostgreSQL/MQTT não foram executados.
As evidências integradas anteriores estão em `evidence/2026-10-14/opcao-01`.
GitHub não respondeu à consulta remota nesta sessão; não houve publicação.
O vídeo foi expressamente excluído do escopo. Este guia não atesta autoria pessoal:
cada integrante precisa executar e explicar o código que apresentar.
