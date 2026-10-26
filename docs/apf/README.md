# Análise de Pontos de Função — entrega coletiva

Revisão de 25/10/2026. A entrega atual é o **[Excel coletivo](coletiva/entrega-coletiva-apf.xlsx)**, com capa, três integrantes, 63 abas, fichas, 93 capturas reais e fórmulas editáveis. Consulte a [conferência completa](coletiva/README.md) e os registros de validação antes de usar materiais históricos.

Integrantes: Artur Ribeiro de Souza; Cristhian Ruan de Rezende; Lucas Andriani Theiss.

| Tipo | Funções | PF |
|---|---:|---:|
| ALI | 9 | 63 |
| AIE | 2 | 10 |
| EE | 28 | 135 |
| SE | 8 | 48 |
| CE | 8 | 27 |
| **Total** | **55** | **283** |

**NI 38; FA 1,03; PFA 291,49.** Nas premissas de 10 horas/PFA, 3 pessoas com 6 horas produtivas/dia e R$ 25/hora: esforço 2.914,90 horas-pessoa, prazo de 162 dias úteis e custo R$ 72.872,50. A estimativa corresponde ao desenvolvimento integral, não ao restante da atividade.

## Conteúdo para apresentação

- Dados: A01–A09 e X01/X02, com itens e registros lógicos. Os provedores mantêm seus próprios TSV por CLI, fora da fronteira da aplicação de estoque; HTTP isoladamente não caracteriza AIE.
- EEs: 28 processos. Para incluir/alterar/excluir, apresentar EE04/05/06 (modelos); produto, posição, usuário e loja usam exclusão lógica quando aplicável. Movimentações/auditoria não têm edição/exclusão arbitrária.
- Saídas numéricas: SE02 (valor), SE04 (fluxo), SE06 (saldo), SE07 (ocupação), SE08 (peso líquido/subtotal), SE13 (comparação de preços). SE09 é exportação com atualização de auditoria; SE10 deriva o indicador Estornado.
- Consultas destacadas: CE04 fornecedores, CE05 modelos, CE08 lojas e CE12 ofertas. As oito CEs incluem entradas/saídas, AR separados e maior complexidade conforme os slides.
- SE08: loja `granel`, tela `/movimentar`, produto em kg, dispositivo `balanca-demo`. O simulador usa o broker MQTT real; nenhuma evidência é apresentada como acionamento físico.

## Documentação e critérios

[FICHAS.md](FICHAS.md) descreve itens e arquivos por função; [contagem.json](contagem.json) contém a fonte estruturada; [CALCULOS.md](CALCULOS.md) e [resultados.json](resultados.json) trazem os resultados; [NI.md](NI.md) justifica as 14 notas; [VALIDACAO.md](VALIDACAO.md) distingue verificações atuais e históricas.

RL é subgrupo lógico, não quantidade de linhas no banco. TD conta tipos de dados distintos que atravessam a fronteira, não todos os campos internos lidos. Um comando e um campo de feedback são contados por processo; textos alternativos no mesmo campo não multiplicam TD. Canais, paginação, status técnico, CSV/PDF e telas auxiliares da mesma intenção não geram novas funções.

Nas CEs, entrada e saída são avaliadas separadamente e a maior complexidade determina PF. Itens repetidos na saída são retirados conforme a Parte III, páginas 7 e 10–13. AR de cada lado é explicitado por processamento; o total da função usa a união. A fórmula FA = 0,65 + 0,01 × NI segue a Parte IV, página 11; a página 15 contém uma constante e limites incoerentes, explicitados no Excel.

As três planilhas individuais, seus roteiros, o PDF de divisão e os PDFs explicativos anteriores ficam preservados como materiais históricos. Seus recortes se sobrepõem e seus PFs não devem ser somados. A revisão coletiva reclassificou CE10 em SE10 e reviu o NI, razão pela qual seus totais diferem.

O XLSX foi aberto, recalculado e salvo no LibreOffice. Microsoft Excel não está instalado neste ambiente; sua abertura nativa não foi verificada. Evidências completas e limitação estão no [README coletivo](coletiva/README.md).

Para recalcular a memória textual: `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/calcular-apf.ps1`. Para executar a aplicação: seguir o README principal e o Compose com `.env.demo`.
