# Roteiro da apresentacao Database First

## Objetivo da atividade

Mostrar que um banco existente pode ser o ponto de partida para gerar código. Nesta entrega há dois exemplos complementares:

- O jOOQ consulta o PostgreSQL ativo e gera classes e DAOs Java. A aplicação local usa esse código gerado em um CRUD real.
- O Bootify importa um arquivo SQL exportado do mesmo banco e pode gerar entidades, camada de CRUD e telas Thymeleaf.

O primeiro fluxo é executável e testado dentro do repositório. O Bootify é um gerador separado: a importação e o download do projeto são feitos no site. O CRUD gerado não é conectado diretamente ao sistema real, pois precisa receber as regras de loja e autorização.

## Antes da aula

Pré-requisitos: Docker Desktop iniciado, internet para baixar as imagens na primeira execução e dois terminais PowerShell na raiz do repositório.

### Terminal 1: iniciar o banco

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/iniciar-banco.ps1
```

Esse script chama `demo.ps1 prepare`. Ele cria as credenciais locais, inicia o PostgreSQL isolado, espera o healthcheck, aplica as migrations originais com Flyway e insere os dados da loja de demonstração. O banco fica na porta `5544`; não usa o PostgreSQL do projeto principal.

Deixe o terminal aberto até aparecer `Banco pronto`.

### Terminal 2: gerar e iniciar o restante

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/gerar-e-iniciar-demo.ps1
```

O script executa estas etapas em sequência:

1. `demo.ps1 generate`: o jOOQ conecta ao PostgreSQL e gera tabelas, Records, POJOs, DAOs e referências a chaves em `DatabaseFirst/target/generated-sources/jooq/`.
2. `demo.ps1 export-bootify`: exporta somente `tenants` e `models`, sem linhas de dados, para `DatabaseFirst/target/bootify-schema.sql`.
3. `demo.ps1 test`: compila e executa os testes da demonstração contra o banco. Se os testes falharem, o script para e não inicia a aplicação.
4. `demo.ps1 run`: empacota o projeto e inicia o site local em `http://127.0.0.1:8082/`.

O processo web ocupa esse terminal. Use `Ctrl+C` para encerrá-lo.

### Terminal 3: entrar na demonstração local

Com a aplicação rodando, abra outro PowerShell e consulte as credenciais criadas para esta demonstração:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 credentials
```

Entre em `http://127.0.0.1:8082/`. Cadastre um modelo, edite-o e exclua-o. Para provar que os dados foram persistidos, em outro terminal rode:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 inspect
```

## Demonstrar o gerador de CRUD e telas

O arquivo de importação produzido pelo segundo script é `DatabaseFirst/target/bootify-schema.sql`.

1. Abra [Bootify](https://bootify.io/) e escolha um projeto Spring Boot com Java, PostgreSQL e Thymeleaf.
2. Importe o SQL e confira no preview as tabelas `tenants` e `models`.
3. Habilite CRUD e frontend para `models`; deixe `tenants` como entidade de apoio.
4. Use o modo de esquema **None (validate)** para validar um banco existente, em vez de tentar criá-lo.
5. Gere e baixe a aplicação. Mostre uma entidade gerada, o controlador/serviço e a página Thymeleaf.

O Bootify documenta a importação de SQL e a geração de código CRUD e frontend por entidade. Recursos SQL não reconhecidos podem precisar de ajuste no preview; por isso, esta aplicação gerada é um scaffold separado, não uma substituição automática da demonstração segura em execução. Veja [o fluxo e os limites de integração](BOOTIFY_AUTOMATICO.md).

## O que explicar

| Peça | O que faz | Como explicar |
| --- | --- | --- |
| PostgreSQL | Guarda as tabelas e os registros da demonstração. | “Começamos pelo banco que já existe.” |
| Flyway | Aplica as migrations versionadas ao banco isolado. | “O Flyway prepara a estrutura; não gera as classes Java.” |
| jOOQ Code Generator | Lê metadados do PostgreSQL e grava código Java. | “O jOOQ observa tabelas, colunas e chaves e gera os tipos usados pelo compilador.” |
| Maven | Organiza a geração, compilação, testes e empacotamento. | “O perfil `codegen` liga o gerador à fase de geração de fontes.” |
| Bootify | Importa o SQL e gera um scaffold de aplicação. | “Além das classes, esta ferramenta consegue gerar o CRUD e a interface escolhidos.” |
| Spring Boot e Thymeleaf | Executam o site local da demonstração. | “A tela local demonstra as operações persistidas; esta tela foi implementada à mão e usa os Records do jOOQ.” |

Uma explicação curta:

> “Database First começa pelo esquema do banco. O Flyway prepara um PostgreSQL isolado. O jOOQ se conecta a esse banco e gera classes e DAOs, que usamos no CRUD demonstrável. Para mostrar geração de telas também, exportamos o esquema e o importamos no Bootify, que gera o scaffold web a partir das tabelas selecionadas. O CRUD do Bootify é um projeto separado; o CRUD local aplica as regras de loja, autorização e auditoria do sistema.”

## Encerrar depois da apresentação

Pare a aplicação com `Ctrl+C`. Depois, no PowerShell:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File DatabaseFirst/demo.ps1 stop
```

O comando para os containers dessa demonstração e preserva o volume do banco. Não use `down -v`, pois isso apagaria o banco preparado.
