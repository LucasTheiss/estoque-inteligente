# Componente estoque-regras

Biblioteca Java com as regras de quantidade e cálculo já usadas pelo Estoque
Inteligente. A distribuição é um JAR comum, consumível por outro programa Java.
As mesmas fontes da aplicação são compiladas: não há uma segunda implementação.

## Contrato e dependências

- Plataforma: Java 21 ou superior. Dependências externas: nenhuma; usa a biblioteca padrão Java.
- Versão: obtida de `project.version` do `pom.xml` e gravada no manifesto do JAR.
- `Quantidade.validar(String)` retorna `BigDecimal` e rejeita entrada inválida com
  `IllegalArgumentException`. Texto decimal usa ponto; não há arredondamento silencioso.
- `QuantidadeInteira`: até zero casas significativas; `QuantidadeFracionada`: até
  três; `QuantidadeMonetaria`: até duas. Zero é aceito; negativos e valores
  iguais ou superiores a 1.000.000.000.000 são rejeitados.
- `CalculoEstoque.executar(BigDecimal saldo, BigDecimal quantidade)` retorna o
  novo saldo. `EntradaEstoque` soma, `SaidaEstoque` subtrai e `AjusteEstoque`
  substitui. `OperacaoEstoque` aplica as validações comuns antes/depois do cálculo.
- Cálculos rejeitam entradas nulas/negativas, saldo final negativo e ausência de
  alteração. Validar precisão com `Quantidade` é responsabilidade do consumidor,
  assim como na aplicação original.
- A biblioteca não persiste saldo nem autentica usuários. Loja, vínculo,
  autorização, transação, idempotência e auditoria pertencem ao `EstoqueService`
  da aplicação. Este JAR é de cálculo, não um substituto do serviço transacional.

## Gerar e verificar

Na raiz do repositório, com JDK 21+ no PATH ou JAVA_HOME:

```powershell
powershell -NoProfile -File scripts/empacotar-reuso.ps1
```

O script compila nove tipos selecionados com `--release 21`, cria
`target/reuso/estoque-regras-0.0.1-SNAPSHOT.jar` (nome acompanha a versão do POM),
inclui este documento e compila `examples/reuso/ConsumidorEstoque.java` usando
somente esse JAR. Executa o consumidor com asserções habilitadas (`-ea`).
Não precisa iniciar Spring, Docker, PostgreSQL ou MQTT.

O JAR fica fora da raiz de `target`, preservando o seletor do Dockerfile existente.
Arquivos de cada compilação ficam em diretório exclusivo para não reaproveitar
classes antigas. Os binários em `target` não são versionados; as fontes e o
procedimento de geração ficam no Git.

## Consumir em outro projeto

Copie o JAR e o exemplo para um diretório externo ao projeto. Nesse diretório:

```powershell
javac --release 21 -cp estoque-regras-0.0.1-SNAPSHOT.jar ConsumidorEstoque.java
java -ea -cp "estoque-regras-0.0.1-SNAPSHOT.jar;." ConsumidorEstoque
```

Resultado esperado: entrada 10, saída 3 e ajuste 8 produzem 10, 7 e 8; os testes
também verificam zerar saldo, precisão e rejeição de operações inválidas.

## Distribuição e direitos

Repositório de origem: https://github.com/LucasTheiss/estoque-inteligente

Nenhuma licença de terceiros é incorporada porque o JAR não inclui dependências
externas. O projeto não declara licença de redistribuição neste momento; este
complemento não concede uma licença em nome dos autores. O componente é entregue
para demonstração acadêmica; redistribuição pública deve respeitar os direitos
dos autores. Não foi publicado em Maven Central ou outro registro de pacotes.

## Relação com a disciplina

`docs/conteudo/Componentes de Reuso - Parte 02.pdf`, páginas 2 a 6, descreve
empacotamento como artefato distribuível com contrato, versão, dependências e
documentação. A página 7 contém o item 03. O JAR e o consumidor tornam esse
reuso demonstrável sem copiar a implementação. O projeto já tinha distribuição
da aplicação completa via Spring Boot JAR/Docker; este complemento explicita o
consumo de um componente por outro programa. Vídeo fora do escopo por solicitação.
