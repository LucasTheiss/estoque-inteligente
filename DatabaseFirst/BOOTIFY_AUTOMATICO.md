# Abrir o CRUD Bootify no banco existente

Todos os comandos abaixo partem de `estoque-inteligente\DatabaseFirst`.

## 1. Preparar o banco

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-banco.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File .\gerar-codigo.ps1
```

Importe `target\bootify-schema.sql` no Bootify, escolha PostgreSQL e Thymeleaf e habilite CRUD/frontend. Baixe e extraia o projeto em `bootify\ei-front`: essa pasta deve conter `pom.xml`, `mvnw.cmd` e `src` diretamente.

## 2. Configurar, compilar e abrir

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-bootify.ps1
```

O script ajusta automaticamente as entidades, le as credenciais de `.env`, desativa o Flyway do aplicativo, exige a validacao do esquema existente, compila com o Maven Wrapper e inicia o JAR. Ele usa o Java de `JAVA_HOME`, ou do PATH se JAVA_HOME nao estiver definido, e passa essa versao ao Maven sem editar o POM. Instale um JDK completo (com javac), compativel com o codigo baixado; o script exige pelo menos Java 17. Se o download usar recursos de Java mais recente, selecione esse JDK em JAVA_HOME.

A primeira compilacao precisa de internet para baixar dependencias e ferramentas do frontend. Aguarde `BUILD SUCCESS` e a mensagem `Started` do aplicativo. Abra:

- http://localhost:8083/models — cadastrar, consultar, editar e excluir modelos.
- http://localhost:8083/tenants — lojas.

Para um modelo, selecione a loja `Loja exclusiva Database First`. Para conferir a gravacao, em outro terminal dentro de DatabaseFirst:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 inspect
```

Se o download tiver outro nome, informe o caminho relativo a DatabaseFirst (ou um caminho absoluto):

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\iniciar-bootify.ps1 -Projeto .\bootify\meu-projeto
```

Nao e preciso mudar o nome do projeto para o nome do banco. A conexao usa `estoque_databasefirst`, porta 5544. O banco e preparado por Docker/Flyway; jOOQ apenas le o esquema e gera codigo.

## O que foi automatizado e por que

| Ajuste | Motivo |
| --- | --- |
| Model usa a tabela models; Tenant usa tenants | Os nomes Java gerados nao correspondem automaticamente aos nomes plurais do banco. |
| IDs usam models_id_seq e tenants_id_seq, com allocationSize 1 | Essas sequences ja existem e fornecem os proximos IDs. A primary_sequence do download nao e usada. |
| Flyway do aplicativo desligado durante a execucao | O banco ja tem migrations e tabelas; a migration inicial do download conflita com esse historico. |
| Hibernate em validate | Confere os mapeamentos sem recriar as tabelas. |
| Credenciais lidas do .env | Reutiliza o banco da atividade sem gravar senha nos fontes Java. |
| Maven Wrapper compila e empacota | Executa o build do backend e do frontend configurado no POM baixado. |

`preparar-bootify.ps1` altera somente as duas entidades, preservando as demais propriedades e as restricoes unicas. Salva o primeiro original ao lado de cada Java com extensao `.databasefirst.bak`. Valida os dois arquivos antes de escrever e pode ser executado novamente. Para apenas aplicar esses ajustes, sem compilar:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\preparar-bootify.ps1
```

`iniciar-bootify.ps1` chama esse preparador e configura a conexao/Flyway no processo. Portanto, use esse iniciador toda vez; abrir o JAR diretamente nao reaplica essas variaveis. Nenhuma tabela ou sequence e criada por esses dois scripts.

## Roteiro para explicar ao professor

1. Mostre o banco preparado e as tabelas existentes.
2. Execute gerar-codigo.ps1: o jOOQ le o banco e gera classes; o script tambem exporta SQL para Bootify.
3. Mostre a importacao e o download do Bootify: ele gera entidades, CRUD e telas.
4. Execute iniciar-bootify.ps1: ele adapta os nomes gerados ao esquema existente, compila e inicia.
5. Cadastre, edite e exclua um modelo na tela. Mostre demo.ps1 inspect para conferir os dados.

Fala sugerida: “O banco e a origem. jOOQ gera o acesso tipado e Bootify gera o aplicativo com CRUD e telas. Nosso script automatiza a adaptacao dos mapeamentos e a execucao; nao escreve os controladores ou as telas.”

Se baixar o projeto novamente, basta extrair e repetir o iniciador. A importacao/download no site continua manual. O projeto baixado fica ignorado pelo Git.

## Parar e limites de validacao

Pressione Ctrl+C no terminal do site. O script retorna a pasta original. Para parar o banco, dentro de DatabaseFirst:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\demo.ps1 stop
```

Os dados permanecem no volume. Use o banco isolado da atividade: o CRUD gerado permite acessar outras lojas tambem.

A automacao dos mapeamentos foi testada com exemplos do formato gerado, incluindo repeticao e rejeicao de arquivos inesperados. O download ei-front nao estava presente neste checkout durante essa validacao; a compilacao e o uso do site real ainda precisam ser testados com ele.
