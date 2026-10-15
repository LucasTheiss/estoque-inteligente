  # Entrega acadêmica — Opção 01

Implementação de 14/10/2026. Equipe de três integrantes: seis CRUDs completos,
dez tabelas de domínio, variabilidade por loja, dois Singletons, três aplicações
de Template Method e três aplicações de Strategy. A contagem usa aplicações
distintas, além das variantes dentro de cada aplicação.

Os caminhos Java deste documento partem de `src/main/java/br/com/estoque/`.

## Singleton: dois exemplos

| Arquivo | O que faz / alteração |
| --- | --- |
| `config/SistemaConfig.java` | Mantido: construtor privado, `INSTANCE`, `getInstance()` e mapa imutável da identidade da aplicação. |
| `controller/web/ContextoViewAdvice.java` | Mantém o uso de `SistemaConfig` para fornecer `appName`; também permite ao superadmin voltar ao painel global quando a loja selecionada foi desativada. |
| `config/CatalogoPermissoes.java` | Novo Singleton explícito. Centraliza perfis e descrições das permissões em coleções imutáveis. Não armazena permissões individuais nem dados de sessão. |
| `service/Acesso.java` | Obtém `PERFIS` e `PERMISSOES` do catálogo único; mantém consulta das concessões reais no banco. Também bloqueia lojas inativas. |

Apresentação: abra os dois Singletons, identifique construtores privados e os
métodos `getInstance()`. Avalie duas chamadas de cada classe com `==`: ambas
retornam a mesma referência. Mostre os consumidores. O catálogo é reutilizado
pela matriz de permissões, pelo provisionamento e pelas verificações de acesso.

## Template Method: três exemplos independentes

### 1. Movimentações de estoque

| Arquivo | O que faz / alteração |
| --- | --- |
| `service/OperacaoEstoque.java` | Mantém `executar()` final: validar entradas, chamar `calcular()`, validar resultado. Passa também a implementar o contrato `CalculoEstoque`. |
| `service/EntradaEstoque.java` | Mantido: especializa `calcular()` somando a quantidade. |
| `service/SaidaEstoque.java` | Mantido: especializa `calcular()` subtraindo a quantidade. |
| `service/AjusteEstoque.java` | Mantido: especializa `calcular()` substituindo o saldo pela quantidade informada. |
| `service/EstoqueService.java` | Continua coordenando autorização, transação e persistência; seleciona a operação pelo contrato `CalculoEstoque`. |

Apresentação: em vínculo zerado, entrada 10, saída 3 e ajuste para 8 resultam em
10, 7 e 8. Mostre que validações comuns permanecem na classe base. Saída superior
ao saldo é rejeitada antes de persistir.

### 2. Cadastros

| Arquivo | O que faz / alteração |
| --- | --- |
| `service/CadastroTemplate.java` | Nova base genérica. `executarCadastro()` final exige `cadastros`, prepara/valida a entidade, persiste e audita. Hooks: `preparar()` e `persistir()`. |
| `service/PosicaoService.java` | Usa o template para salvar posições. Mantém validações de unidade, capacidade, coordenadas, dispositivo e vínculos. |
| `service/ModeloProdutoService.java` | Novo serviço concreto do template para nome, marca e categoria. Mantém isolamento por loja e exclusão protegida por referências. |

Os métodos públicos `salvar()` nos serviços concretos recebem `@Transactional`.
Eles chamam o método protegido final da base dentro da transação. Isso mantém
a interceptação do Spring e o rollback conjunto de persistência e auditoria.

Apresentação: salve uma posição e um modelo. Mostre a mesma sequência base,
os dois hooks e os registros `posicao.salvar` e `modelo.salvar` na tabela `audit`.
Revogue `cadastros` de um perfil e demonstre a rejeição antes da persistência.

### 3. Relatórios

| Arquivo | O que faz / alteração |
| --- | --- |
| `view/RelatorioTemplate.java` | Nova base. `gerar()` final valida loja/lista, fixa uma cópia dos registros, rejeita mistura de lojas, chama `renderizarConteudo()` e monta `RelatorioArquivo`. |
| `view/RelatorioCsv.java` | Herda o template, informa tipo MIME/nome e implementa o hook usando o renderizador CSV existente. Preserva escape de células e proteção contra fórmulas. |
| `view/RelatorioPdf.java` | Herda o template, informa tipo MIME/nome e implementa o hook usando o renderizador PDF existente. Preserva paginação e fechamento de recursos. |
| `service/RelatorioService.java` | Mantém autorização `relatorios`, recurso `enterprise`, consulta e auditoria. Exporta pelo contrato de formato. |

Apresentação: exporte o mesmo histórico em CSV e PDF. Mostre que o método final
é o mesmo, enquanto a renderização varia. O serviço exige o recurso da loja e a
permissão do usuário antes de gerar qualquer arquivo.

## Strategy: três exemplos independentes

### 1. Precisão de quantidades e preços

| Arquivo | O que faz / alteração |
| --- | --- |
| `strategy/Quantidade.java` | Mantido: contrato `validar()` e conversão decimal compartilhada. |
| `strategy/QuantidadeInteira.java` | Mantido: aceita zero casas decimais significativas. |
| `strategy/QuantidadeFracionada.java` | Mantido: aceita até três casas decimais significativas. |
| `strategy/QuantidadeMonetaria.java` | Nova estratégia: aceita até duas casas decimais para preços; não cria uma unidade artificial de estoque. |
| `service/ProdutoService.java` | Valida preço através de `QuantidadeMonetaria`, usando o contrato `Quantidade`. |
| `service/EstoqueService.java` | Mantém escolha de `QuantidadeInteira` ou `QuantidadeFracionada` conforme `un`/`kg`. |

Apresentação: `un` rejeita `0.5`; `kg` aceita `0.125`; preço aceita `12.50` e
rejeita `12.501`. O fluxo cliente delega a validação à política escolhida.

### 2. Tipo de operação de estoque

| Arquivo | O que faz / alteração |
| --- | --- |
| `strategy/CalculoEstoque.java` | Novo contrato Strategy com `executar(saldo, quantidade)`. |
| `service/OperacaoEstoque.java` | Implementa o contrato usando o Template Method existente. |
| `service/EntradaEstoque.java`, `SaidaEstoque.java`, `AjusteEstoque.java` | Estratégias concretas herdadas: somar, subtrair e ajustar. |
| `service/EstoqueService.java` | O mapa `OPERACOES` usa `CalculoEstoque`; seleciona a estratégia pela chave `entrada`, `saida` ou `ajuste`. |

Apresentação: destaque o mapa no cliente e a chamada pelo contrato. A escolha
do algoritmo é Strategy. A ordem invariável dentro do algoritmo é Template Method.
Os padrões são combinados, sem contar subclasses como aplicações independentes.

### 3. Formato de exportação

| Arquivo | O que faz / alteração |
| --- | --- |
| `strategy/FormatoRelatorio.java` | Novo contrato Strategy: gerar um arquivo a partir da loja e dos registros. |
| `view/RelatorioTemplate.java` | Implementa o contrato, compartilhando a sequência de geração. |
| `view/RelatorioCsv.java`, `RelatorioPdf.java` | Estratégias concretas intercambiáveis de exportação. |
| `service/RelatorioService.java` | Substitui o `switch` de renderização por mapa de estratégias. Escolhe por `csv`/`pdf`, valida a chave e chama `gerar()`. |

Apresentação: altere o formato mantendo filtros, autorização, dados e auditoria.
Mostre que o serviço conhece o contrato, e não os detalhes de construção do arquivo.

## CRUDs concluídos

| CRUD | Arquivos principais | Regra |
| --- | --- | --- |
| Lojas | `controller/web/LojaController.java`, `service/LojaService.java`, `repository/LojaRepository.java`, `templates/global.html` | Superadmin cria, consulta, edita e desativa. `tenants.active=false` preserva histórico e impede acesso. |
| Vínculos | `controller/web/VinculoController.java`, `service/VinculoService.java`, `repository/OcupacaoRepository.java`, `templates/vinculos.html` | `cadastros` permite criar, consultar, editar e remover. Edição exige saldo zero; destino duplicado, unidade incompatível e outra loja são rejeitados. |
| Modelos | `dto/ModeloProdutoForm.java`, `controller/web/ModeloProdutoController.java`, `service/ModeloProdutoService.java`, `repository/ModeloProdutoRepository.java`, `templates/modelos.html` | `cadastros` permite CRUD. Excluir exige ausência de qualquer produto associado, inclusive inativo. |

Templates estão em `src/main/resources/templates/`. A migração
`src/main/resources/db/migration/V2__lojas_ativas.sql` adiciona a flag de ativação.
`UsuarioRepository` filtra lojas inativas no login. `Acesso` verifica a loja nas
sessões existentes, inclusive quando selecionada por superadmin. O painel global
continua acessível ao superadmin para escolher outra loja.

`dto/LojaForm.java` normaliza checkboxes ausentes para `false`, permitindo criar
ou editar uma loja com todos os recursos opcionais desmarcados.

`layout.html` inclui Modelos na navegação existente. `deploy/nginx.conf` libera a
rota `/modelos` no proxy. Os POSTs mantêm proteção CSRF do Spring Security.

Divisão sugerida: integrante 1, Produtos/Modelos; integrante 2, Posições/Vínculos;
integrante 3, Usuários/Lojas. Cada integrante demonstra criar, consultar, editar,
recarregar e excluir/desativar. Use registros descartáveis e códigos inéditos.

## Variabilidade e execução

O núcleo de estoque é comum. As flags `iot`, `enterprise` e `fractional` de
`tenants` determinam recursos opcionais. Compare `essencial` e `granel` com o
mesmo perfil admin; mostre menus, exportação e movimentação fracionada. Depois,
mostre as flags em `/global`, sua persistência e `Acesso.recurso()` no backend.

Os comandos para subir, popular, testar e parar estão no [README](../README.md).
Lojas existentes são preservadas; reiniciar não restaura uma loja desativada.

## Testes da entrega

Validação concluída: **68 testes Java e nove JavaScript aprovados**. Nenhum teste
Java ignorado na execução integrada. Build Docker, população inicial e telas no
navegador também conferidos. Logs e capturas em
[evidence/2026-10-14/opcao-01](../evidence/2026-10-14/opcao-01/README.md).

- `src/test/java/br/com/estoque/CrudsTest.java`: banco PostgreSQL real, CRUDs,
  permissões por perfil, revogação de `cadastros`, CSRF, isolamento, rollback de
  vínculo, referências de modelos, desativação e bloqueio de sessões.
- `src/test/java/br/com/estoque/TelasTest.java`: renderização das telas novas e
  de edição, além das telas existentes.
- `src/test/java/br/com/estoque/service/PadroesTest.java`: identidade e imutabilidade
  dos Singletons, estratégias, ordem do template de cadastro, exportação e isolamento.
- `src/test/java/br/com/estoque/service/OperacaoEstoqueTest.java`: invariantes de
  entrada, saída e ajuste, incluindo limites das quantidades.
