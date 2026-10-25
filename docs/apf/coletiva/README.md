# Entrega coletiva de APF — revisão parcial

Estado em 25/10/2026: trabalho interrompido a pedido do usuário para commit imediato. **Ainda não entregar o Excel ao professor.**

## Arquivos e resultado da revisão

- `entrega-coletiva-apf.xlsx`: rascunho coletivo, 63 abas, 55 funções, fórmulas nativas, fichas, protótipos ilustrativos identificados e 32 capturas reais utilizadas como fontes de imagens incorporadas.
- `catalogo-revisado.json`: catálogo usado no rascunho, alterações fundamentadas e cálculo independente. Não substitui silenciosamente `../contagem.json`, que ainda representa a contagem anterior.
- `capturas/`: capturas adicionais da execução atual; o manifesto registra origem, data, descrição e SHA-256. Parte dessas capturas ainda não foi incorporada ao Excel.
- `testes-java.log` e `testes-js.log`: verificações efetivamente executadas nesta revisão.
- `originais-sha256.json`: hashes do modelo e dos três Excel individuais para conferir sua preservação.
- `loja-demonstracao.json`: identificação da loja exclusiva usada nas operações, desativada ao final, com histórico preservado.

| Tipo | Quantidade | PF |
|---|---:|---:|
| ALI | 9 | 63 |
| AIE | 2 | 10 |
| EE | 28 | 135 |
| SE | 8 | 48 |
| CE | 8 | 27 |
| Total | 55 | 283 |

NI proposto revisado: 38. FA = 0,65 + 0,01 × 38 = 1,03. PFA = 283 × 1,03 = 291,49.

Premissas acadêmicas: 10 horas-pessoa/PFA, 3 pessoas, 8 horas/dia com 75% de produtividade, R$ 25/hora. Desenvolvimento integral: 2.914,90 horas-pessoa, 161,9389 dias úteis, arredondados para 162, e R$ 72.872,50. Não é estimativa do esforço restante para concluir esta atividade.

Esses resultados foram conferidos no motor de geração e por cálculo independente. **O recálculo no Microsoft Excel não foi concluído.**

## Inconsistências encontradas

1. **CE10 passa a SE10:** `MovimentacaoRepository.historico` calcula `EXISTS` de movimento reversor; `historico.html` apresenta “Estornado”. O indicador é derivado, não atributo armazenado do movimento original. A mesma função foi reclassificada, sem duplicação; peso passa de 6 para 7 PF.
2. **CE06:** a ficha anterior contava código de barras, tamanho, cor, dispositivo e pino, presentes no DTO, mas não exibidos em `vinculos.html`. A revisão conta SKU, nome, código da posição, setor, unidade e quantidade. Peso permanece 6 PF.
3. **SE06:** a ficha anterior incluía campos não apresentados pelo dashboard. A revisão remove código de barras, características e setor dessa saída e inclui quantidade de produtos exibidos. Peso permanece 7 PF.
4. **SE09:** tipo aparece no CSV/PDF e deve estar descrito na saída; como também é entrada, conta uma única vez. Peso permanece 7 PF.
5. **NI:** desempenho 1→0, pois timeout/paginação não comprovam requisito solicitado e revisto; implantação 2→1, pois migração de esquema não comprova conversão de dados legados; operação 2→1, pois inicialização sem procedimento demonstrado de backup/recuperação não comprova o critério composto. NI 41→38. São propostas técnicas, não consenso inventado.
6. **Balança:** o painel de leitura está em `/movimentar`, no template `movimento.html`, não em `/baixa-rapida`. O rascunho Excel ainda precisa dessa correção. A tentativa atual de obter leitura não produziu evidência de sucesso dentro do tempo de espera; não alegar leitura validada nesta sessão.
7. **Documentos anteriores:** números 282/41/298,92 e trechos antigos de cobertura continuam nas fontes anteriores. Precisam ser conciliados após fechar a revisão; não somar recortes individuais.
8. **Slides/modelo:** a CE do modelo usa uma avaliação única, enquanto a Parte III exige entrada e saída separadas e maior complexidade. Parte IV p. 11 usa 0,65; p. 15 usa 0,35 e limites incoerentes. O rascunho adota p. 11. Convenção de um campo de feedback diverge do exemplo EE com duas mensagens específicas e continua explicitada para validação acadêmica.

## O que já foi efetivamente verificado

- Branch `entrega-trabalho`; controllers, serviços, repositórios, templates, segurança e provedores externos confrontados com catálogo.
- Matrizes ALI, EE, SE e CE dos slides renderizadas e examinadas; textos da Parte IV e da tabela NI consultados.
- Provedores externos mantêm TSV por CLI; estoque usa clientes somente leitura. Essa separação fundamenta os AIEs, além do simples uso de HTTP.
- Docker Compose reconstruído e iniciado. Nginx recarregado após a recriação da aplicação.
- **90 testes Java: zero falhas, erros ou ignorados. 13 testes JavaScript: todos aprovados.**
- CRUD de modelo temporário executado e excluído fisicamente.
- Na loja exclusiva APF: criação/alteração/desativação da loja e usuário; seleção de loja; permissões e IoT; criação/alteração/desativação de produto e posição; inclusão/alteração/exclusão de vínculo; entrada, saída, ajuste, estorno e exportação CSV. Resultados de sucesso registrados em capturas. Comando de sinalização retornou HTTP 200, sem comprovar confirmação do dispositivo.
- Loja APF desativada ao final. Histórico preservado; dados preexistentes não foram usados na limpeza. A associação dos registros à loja APF foi conferida por SQL antes da limpeza.
- O arquivo gerado é ZIP íntegro e pode ser lido por openpyxl; o motor de geração avaliou as 55 pontuações e os totais sem erros encontrados.

## Pendências exatas para finalizar

### 1. Corrigir a abertura nativa do Excel — bloqueador

O Microsoft Excel instalado falha ao abrir o arquivo coletivo com “Não é possível obter a propriedade Open da classe Workbooks”. O modelo original, um arquivo criado pelo próprio Excel e um arquivo mínimo criado pelo mesmo gerador abrem. Portanto, não atribuir a falha apenas à ausência de Excel.

A causa no XLSX coletivo ainda não foi isolada. Tentativas em cópias sem imagens, sem fórmulas e com menos abas não resolveram. Corrigir a exportação/estrutura do pacote e abrir o arquivo normalmente, sem perda de fórmulas ou conteúdo. Não entregar apenas por passar na leitura do ZIP/openpyxl.

### 2. Completar e conferir evidências visuais

- Incorporar as capturas adicionais de entrada/saída das operações da loja APF, substituindo imagens apenas contextuais quando houver evidência específica.
- Completar capturas de entrada/saída das CEs destacadas, especialmente CE05. O script complementar parou na tentativa da balança, antes das consultas seguintes.
- Corrigir SE08 para `/movimentar` e obter leitura simulada recente, mostrando peso líquido, subtotal e indicador de simulação; investigar a falha atual sem alegar sucesso.
- Mostrar a resposta do comando LED e, se for alegada confirmação, registrar a confirmação real do simulador/dispositivo.
- Conferir todas as abas completas e imagens em tamanho legível. Foram geradas prévias de todas as abas, mas a inspeção visual integral e a impressão ainda não foram concluídas. Os blocos numéricos precisam de espaçamento/alinhamento melhor entre valor e rótulo seguinte.
- Os blocos ilustrativos atuais descrevem entrada/resultado em células; revisar se a apresentação atende à expectativa de protótipo do professor, sem confundi-los com execução real.

### 3. Validar fórmulas no Excel e salvar com recálculo automático

Após resolver a abertura: ativar cálculo automático; recalcular; procurar erros; conferir subtotais/PFB/NI/FA/PFA/estimativas; alterar e restaurar uma nota NI, produtividade, capacidade, valor-hora e TD/AR de CE; testar limites das matrizes. Salvar mantendo fórmulas. Ainda não afirmar que o recálculo automático ou a sensibilidade foram verificados no Excel.

### 4. Conciliar documentação e contagem

Revisar AR de entrada e saída de cada CE por processamento efetivo, especialmente a convenção de usar os mesmos arquivos dos dois lados. Atualizar `contagem.json`, `resultados.json`, `CALCULOS.md`, `FICHAS.md`, `NI.md`, `README.md` e `VALIDACAO.md` de forma consistente com a revisão aprovada. Corrigir o roteiro da balança. Preservar modelo e três planilhas individuais. Conferir hashes finais e referências.

### 5. Confirmações que dependem da equipe/professor

- Cada integrante confirma sua contribuição pessoal real; não inferir autoria pelos recortes.
- Equipe confirma as 14 notas e registra o consenso, incluindo premissas de volume e múltiplos locais.
- Validar a convenção de um campo de feedback e a interpretação das referências de entrada/saída das CEs com a disciplina, se houver exigência diferente.

Não é necessário criar funcionalidades adicionais apenas para atingir os mínimos: há CRUD, mais de cinco saídas calculadas e mais de quatro consultas no código. PDF é opcional e não resolve o bloqueador do Excel.

## Instrução para quem retomar

Comece pela abertura nativa do XLSX. Depois incorpore as capturas já preservadas, corrija SE08 e complete sua evidência. Revalide TD/AR e notas NI, sincronize os documentos, execute os testes de sensibilidade no Excel e faça a conferência visual integral. Só então retire o aviso de revisão parcial. Não reutilize números ou alegações de testes sem distinguir o que foi executado nesta revisão.
