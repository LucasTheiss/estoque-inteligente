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

O JAR não inclui dependências externas. O projeto não declara uma licença de
redistribuição; o uso e a distribuição devem respeitar os direitos dos autores.
A biblioteca não foi publicada em Maven Central ou outro registro de pacotes.
