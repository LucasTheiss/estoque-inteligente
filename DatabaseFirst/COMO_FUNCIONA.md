# Como funciona a demonstração Database First

## O que foi implementado

A pasta `DatabaseFirst` contém uma demonstração executável de geração de código Java a partir de um banco PostgreSQL existente. Ela também possui uma página para cadastrar, consultar, editar e excluir modelos de produtos usando esse código gerado.

O objetivo é mostrar, na prática, que a estrutura do banco pode servir de base para o código de acesso aos dados da aplicação. A ferramenta responsável por essa geração é o **jOOQ**.

A demonstração é separada do Estoque Inteligente original: tem seu próprio projeto Maven, banco e aplicação. Os repositórios JDBC do sistema principal continuam como estavam.

## O que significa Database First neste trabalho

Database First significa começar pela estrutura do banco de dados e, a partir dela, produzir código para a aplicação.

Neste projeto, o processo acontece nesta ordem:

1. O PostgreSQL é iniciado em um ambiente exclusivo de demonstração.
2. O Flyway aplica os scripts SQL originais do Estoque Inteligente, criando suas tabelas e relacionamentos nesse banco.
3. Com as tabelas já existentes, o jOOQ se conecta ao PostgreSQL e consulta sua estrutura.
4. O jOOQ gera arquivos Java correspondentes às tabelas, colunas e chaves encontradas.
5. A aplicação utiliza esses arquivos para executar operações reais no banco.

```text
Scripts SQL originais
        ↓ Flyway
Tabelas existentes no PostgreSQL
        ↓ Gerador do jOOQ
Classes Java e código de acesso aos dados
        ↓ Aplicação Spring Boot
CRUD apresentado na página web
```

O Flyway e o jOOQ têm funções diferentes: **o Flyway prepara e versiona o banco; o jOOQ lê esse banco e gera código Java**. O gerador não está apenas interpretando um arquivo SQL: ele consulta o PostgreSQL em execução.

## Tecnologias utilizadas e para que servem

| Tecnologia | Função na implementação |
|---|---|
| **Java 21** | Linguagem e ambiente de execução da aplicação e das classes geradas. |
| **Spring Boot 3.5.6** | Inicializa a aplicação e integra servidor web, serviços, segurança e acesso ao banco. |
| **PostgreSQL 17** | Armazena as tabelas e os registros reais da demonstração. |
| **jOOQ 3.19.26 — edição gratuita** | Lê a estrutura do banco, gera classes Java e oferece recursos para montar e executar operações SQL usando essas classes. |
| **Flyway** | Aplica as três migrations originais no banco exclusivo de demonstração. |
| **Maven** | Gerencia dependências, executa o gerador, compila o projeto, roda testes e produz a aplicação executável. |
| **Thymeleaf** | Monta o HTML da página de CRUD no servidor, preenchendo a tela com os dados consultados. |
| **Spring Security** | Exige login e protege os formulários contra requisições sem token CSRF. |
| **Docker Compose** | Organiza os containers do PostgreSQL e do ambiente Java/Maven, sem depender do Java instalado no Windows. |
| **PowerShell** | Oferece comandos simples para preparar, gerar, executar, testar e restaurar a demonstração. |
| **JUnit e MockMvc** | Testam as operações e as rotas da aplicação, usando PostgreSQL real. |

O jOOQ 3.19.26 foi escolhido por ser a versão gerenciada pelo Spring Boot 3.5.6 utilizado no projeto. A implementação foi compilada e executada com Java 21 e PostgreSQL 17.

## Qual banco e quais tabelas são usados

O banco da demonstração se chama **`estoque_databasefirst`**. Ele recebe a mesma estrutura definida nas migrations do projeto principal, mas seus dados são exclusivos para teste.

O gerador lê as dez tabelas do esquema `public`, excluindo a tabela de controle do Flyway. A apresentação se concentra nestas entidades:

| Tabela | O que representa | Papel na demonstração |
|---|---|---|
| `tenants` | Lojas do sistema. | Identifica a loja exclusiva dos registros de teste. |
| `models` | Modelos de produtos, com nome, marca e categoria. | É a tabela utilizada no CRUD da página. |
| `products` | Produtos vinculados aos modelos. | Mostra relacionamentos e permite verificar se um modelo está em uso. |
| `positions` | Posições de armazenamento. | Serve para mostrar outros campos, tipos e relacionamentos gerados. |

O CRUD usa `models` porque é um cadastro simples e o sistema original permite excluir um modelo quando ele não está vinculado a produtos. Produtos e posições possuem regras de estoque e histórico que tornariam a apresentação mais complexa.

Gerar classes de várias tabelas não significa que todas receberam uma tela de cadastro. **A interface implementada trabalha apenas com modelos.**

## O que o jOOQ realmente gera

Os arquivos gerados ficam em:

```text
DatabaseFirst/target/generated-sources/jooq/
```

Dentro dessa pasta, o pacote utilizado é `br.com.estoque.databasefirst.generated`.

| Arquivo ou tipo | Para que serve |
|---|---|
| `tables/Models.java` | Representa a tabela `models` e disponibiliza referências Java às suas colunas, como `MODELS.NAME`. |
| `tables/records/ModelsRecord.java` | Representa uma linha da tabela, com campos e métodos de acesso. Quando ligado à configuração do jOOQ, permite persistir alterações pelos métodos herdados, como `store()` e `delete()`. |
| `tables/pojos/Models.java` | É um objeto Java simples para transportar os valores consultados, como nome, marca e categoria. |
| `tables/daos/ModelsDao.java` | É uma classe de acesso aos dados, com consultas geradas e operações de CRUD disponíveis pela estrutura do jOOQ. |
| `Keys.java` | Contém referências às chaves primárias, únicas e estrangeiras identificadas no banco. |

Embora o nome seja **Record**, `ModelsRecord` é uma classe de registro do jOOQ; não é uma declaração `record` da linguagem Java.

Na validação desta implementação foram gerados **45 arquivos Java**. Eles foram produzidos pelo gerador oficial, a partir da conexão com o banco, e não escritos manualmente.

Esses arquivos ajudam a escrever consultas com referências e tipos Java conhecidos pelo compilador. Por exemplo, o serviço usa `MODELS.TENANT_ID` para se referir à coluna da loja, em vez de repetir seu nome dentro de strings SQL em cada consulta.

Isso não elimina todas as possíveis falhas em execução. Restrições de unicidade, conexões indisponíveis e regras do banco ainda precisam ser tratadas.

## Como o CRUD funciona

CRUD é o conjunto das quatro operações básicas de persistência:

| Operação | Ação na página | Resultado no PostgreSQL |
|---|---|---|
| **CREATE** | Preencher nome, marca e categoria e cadastrar. | Insere uma linha em `models`. |
| **READ** | Visualizar a lista ou abrir um modelo para edição. | Consulta os modelos da loja de demonstração. |
| **UPDATE** | Alterar os campos e salvar. | Atualiza a linha selecionada. |
| **DELETE** | Clicar em excluir. | Remove a linha, desde que não existam produtos vinculados. |

O caminho de uma operação é:

```text
Pessoa preenche o formulário no navegador
        ↓
ModelController recebe a requisição
        ↓
ModelService valida os dados e aplica as regras
        ↓
Tipos gerados + biblioteca jOOQ executam a operação SQL
        ↓
PostgreSQL grava ou consulta os dados
        ↓
A página é carregada com o resultado atualizado
```

O código escrito manualmente fica em `src/main/java/br/com/estoque/databasefirst/`, dentro da pasta da demonstração.

### Cadastro e edição

Em `ModelService.java`, um cadastro começa com `db.newRecord(MODELS)`. Para editar, o serviço busca o `ModelsRecord` já existente. Depois preenche os campos com métodos como `setName(...)` e chama `model.store()`.

O jOOQ usa o estado desse registro para executar a inserção ou a atualização correspondente. A aplicação utiliza, portanto, a classe gerada a partir de `models` para persistir os dados.

### Consulta

O serviço usa `db.selectFrom(MODELS)` com um filtro pela loja de demonstração. Os resultados são convertidos para o POJO gerado `Models`, usado para preencher a página.

### Exclusão

O serviço busca e bloqueia o modelo durante a transação, verifica se existem produtos vinculados e chama `model.delete()` apenas quando a exclusão é permitida.

O CRUD principal utiliza **Records e referências de tabelas gerados**. O `ModelsDao` também é gerado e utilizado nos testes, mas não é a classe que executa todas as operações da tela.

## Quais regras e proteções foram mantidas

A página não oferece um cadastro livre sobre qualquer loja do banco. O serviço trabalha com uma loja exclusiva, definida na preparação, e aplica esse filtro nas consultas e alterações.

Também foram implementados:

- Nome obrigatório e limite de 200 caracteres por campo.
- Remoção de espaços nas extremidades dos valores informados.
- Respeito à restrição de unicidade de nome, marca e categoria por loja.
- Bloqueio da exclusão de modelos vinculados a produtos.
- Transações para manter a operação e seu registro de auditoria consistentes.
- Login com credenciais locais e proteção CSRF nos formulários.
- Recusa de inicialização fora do perfil de demonstração ou em outro banco.

O login desta aplicação é separado dos usuários do sistema principal. Por isso, a auditoria registra a operação e a loja, mas usa ator nulo: o usuário de demonstração em memória não corresponde a uma linha da tabela `users`.

## Como a alteração do banco aparece no Java

A segunda parte da apresentação adiciona uma coluna de exemplo à tabela `models`:

```sql
ALTER TABLE public.models ADD COLUMN IF NOT EXISTS demo_note TEXT;
```

Essa alteração ocorre somente no banco de demonstração. Neste momento, a coluna já existe no PostgreSQL, mas os arquivos Java continuam com a estrutura anterior.

Ao executar novamente o gerador, o jOOQ encontra a nova coluna e passa a produzir referências e métodos como:

```java
MODELS.DEMO_NOTE
getDemoNote()
setDemoNote(...)
```

Esse é o ponto principal da prova de Database First: **a mudança começa no banco e é refletida no código pela ferramenta de geração**.

A regeneração não é uma sincronização contínua. É necessário executar o comando novamente e recompilar a aplicação. Ela também não adiciona automaticamente um campo ao formulário Thymeleaf: a tela foi escrita manualmente e precisaria de uma alteração própria para editar essa nova coluna.

O comando de restauração remove a coluna de exemplo e os registros da loja de demonstração, gera novamente as classes e executa os testes. Na validação realizada, o arquivo `ModelsRecord.java` restaurado ficou idêntico ao inicial, confirmado por SHA-256.

## O que foi automático e o que foi manual

| Parte | Como foi produzida |
|---|---|
| Tabelas do banco de demonstração | Criadas pelo Flyway a partir das migrations SQL já existentes no projeto. |
| Classes de tabelas, Records, POJOs, DAOs e chaves | Geradas automaticamente pelo jOOQ consultando o PostgreSQL. |
| Novo campo Java após adicionar `demo_note` | Gerado automaticamente ao executar novamente o jOOQ. |
| Controller e Service | Escritos manualmente para receber requisições, aplicar regras e utilizar o código gerado. |
| Página HTML e CSS | Escritos manualmente com Thymeleaf. |
| Configuração de segurança, scripts e testes | Escritos manualmente para tornar a demonstração executável e verificável. |

O jOOQ **não gerou o sistema inteiro**. Ele gerou a representação Java da estrutura do banco e a base de acesso aos dados. A aplicação construída ao redor desse código permite demonstrar seu uso real.

## Como isso se relaciona com o sistema original

O Estoque Inteligente original continua com sua arquitetura e seus repositórios JDBC. A demonstração não substitui esses repositórios nem converte automaticamente a aplicação inteira para jOOQ.

A pasta `DatabaseFirst` utiliza as migrations originais para trabalhar com uma estrutura real do projeto, mas em outro banco e outro volume Docker. A página fica disponível localmente em `http://127.0.0.1:8082/`, e o PostgreSQL de demonstração utiliza a porta local `5544`.

As credenciais são criadas na preparação e ficam no arquivo local `.env` da demonstração, ignorado pelo Git. Não foi necessário colocar senhas fixas nos fontes Java.

O gerador é ativado pelo perfil Maven `codegen`. Assim, gerar classes depende de uma ação explícita e não interfere no build normal do projeto principal. A pasta `target` contém arquivos reconstruíveis: se ela for apagada, é necessário gerar as classes novamente antes de compilar a demonstração.

## O que já foi comprovado

Na validação registrada para esta implementação:

- O PostgreSQL iniciou e recebeu as três migrations originais.
- O jOOQ conectou ao banco, gerou os arquivos e eles compilaram.
- As quatro operações do CRUD funcionaram por HTTP, com conferência dos resultados diretamente no PostgreSQL.
- Os seis testes da demonstração passaram antes da alteração, com a coluna adicional e após a restauração.
- A regeneração criou os métodos do novo campo sem edição manual das classes.
- Os 75 testes Java e os 13 testes JavaScript do projeto original passaram.

Esses resultados são da execução já documentada; este texto não representa uma nova rodada de testes. Os relatórios e os limites da validação estão em [VALIDACAO.md](VALIDACAO.md).

## Como explicar ao professor

> “Nós usamos o banco do Estoque Inteligente como ponto de partida. Primeiro, preparamos um PostgreSQL separado com as migrations originais. Depois, o jOOQ lê as tabelas desse banco e gera classes Java, Records, POJOs e DAOs. Criamos uma tela simples que usa esses tipos gerados para cadastrar, consultar, editar e excluir modelos. Para comprovar Database First, adicionamos uma coluna no PostgreSQL e executamos o gerador novamente: o campo aparece no Java sem escrevermos esse código à mão. A tela e as regras foram implementadas por nós; a geração automática é da estrutura Java e do acesso aos dados.”

Para executar a apresentação, siga o [ROTEIRO_DEMONSTRACAO.md](ROTEIRO_DEMONSTRACAO.md), que contém os comandos completos e os arquivos que devem ser mostrados.

## Geracao automatica de CRUD e tela

A demonstracao jOOQ gera classes e acesso ao banco; o Controller e a pagina Thymeleaf deste modulo foram escritos manualmente. Para apresentar uma ferramenta que tambem gere essas camadas, siga `BOOTIFY_AUTOMATICO.md`: o comando `demo.ps1 export-bootify` extrai o DDL ativo de `tenants` e `models`, e a importacao no Bootify permite gerar entidades, controlador, servico e frontend CRUD para `models`.

O scaffold baixado e um projeto separado e precisa ser revisado antes de conectar a dados reais. O CRUD em execucao neste repositorio continua sendo o modulo jOOQ validado, com isolamento por loja, auditoria e protecao contra exclusao de modelos vinculados.
