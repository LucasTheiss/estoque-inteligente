# Três entregas individuais — Estoque Inteligente

Refeitas conforme a solicitação de preservar exatamente a formatação do modelo, a partir de `docs/exemploEntregaPf.xlsx`, preservado intacto. Cada Excel usa as 12 abas originais. A primeira aba identifica o integrante; dados, transações, protótipos e estimativas ocupam os espaços do modelo, sem uma aba adicional de capa.

| Integrante | Documento | ALIs | EEs | SEs | PFB | PFA | Esforço | Prazo | Custo |
| --- | --- | --- | --- | --- | ---: | ---: | ---: | ---: | ---: |
| Artur Ribeiro de Souza | [Catálogo e produtos](catalogo_produtos.xlsx) | A01/A02 | EE04/EE01 | SE06/SE02 | 52 | 55,12 | 551,20 h | 92 dias úteis | R$ 13.780,00 |
| Cristhian Ruan de Rezende | [Posições e ocupação](posicoes_ocupacao.xlsx) | A03/A04 | EE07/EE10 | SE07/SE04 | 50 | 53,00 | 530,00 h | 89 dias úteis | R$ 13.250,00 |
| Lucas Andriani Theiss | [Movimentações e usuários](movimentacoes_usuarios.xlsx) | A05/A06 | EE21/EE13 | SE04/SE13 | 52 | 55,12 | 551,20 h | 92 dias úteis | R$ 13.780,00 |

Todas usam X01/X02 e CE04/CE12. Os recortes se sobrepõem: não somar os três PFBs como tamanho da aplicação. Na contagem completa, PFB = 282 e PFA = 298,92.

## PDFs para estudar e apresentar

- [Roteiro de Artur Ribeiro de Souza](roteiro_catalogo_produtos.pdf): 15 páginas, com o roteiro original e leitura detalhada das abas e células do seu Excel.
- [Roteiro de Cristhian Ruan de Rezende](roteiro_posicoes_ocupacao.pdf): 15 páginas, com funções, exemplos, cálculos e explicação detalhada da planilha.
- [Roteiro de Lucas Andriani Theiss](roteiro_movimentacoes_usuarios.pdf): 15 páginas, com funções, exemplos, cálculos e explicação detalhada da planilha.
- [Guia da estrutura dos três Excel](guia_estrutura_dos_excels.pdf): 9 páginas, explicando códigos como A01 e X01, siglas, as 12 abas e o mapa das 19 páginas impressas.

Os três Markdown foram ampliados com o mesmo conteúdo dos PDFs. O [guia geral também tem fonte Markdown](guia_estrutura_dos_excels.md). Os textos dos quatro PDFs foram comparados às respectivas fontes, sem omissões, e os limites das páginas foram conferidos. Os arquivos Excel não foram alterados para produzir estes guias.

## Conferência das fórmulas e roteiros em Markdown

Roteiros de estudo e apresentação: [Artur Ribeiro de Souza](roteiro_catalogo_produtos.md), [Cristhian Ruan de Rezende](roteiro_posicoes_ocupacao.md) e [Lucas Andriani Theiss](roteiro_movimentacoes_usuarios.md). Cada roteiro explica as funções do respectivo Excel, os protótipos, os cálculos, as justificativas de NI e perguntas prováveis. Os roteiros acompanham as funções e os cálculos das planilhas.

- Matrizes e pesos ALI/AIE/EE/SE foram mantidos do modelo; faixas e pesos conferidos com o material da disciplina. Referências e faixas de células foram adaptadas à quantidade real de campos e funções.
- **Correção autorizada nas CEs:** o modelo aplicava uma matriz única ao total de dados. A Parte III, p. 10–12, exige entrada e saída separadas e escolha da maior complexidade. As cópias fazem isso, sem contar dados repetidos duas vezes. Ambas as CEs permanecem simples, 3 PF cada.
- NI = soma das 14 notas = 41; FA = 0,65 + 0,01 × NI = 1,06; PFA = PFB × FA. Parte IV, p. 11, sustenta a constante 0,65. A p. 15 apresenta 0,35 e limites inconsistentes; essa divergência está registrada nas planilhas.
- Premissas: 10 h/PFA, uma pessoa por recorte, jornada de 8 h × 75% = 6 h produtivas/dia, R$ 25/h, reserva zero, custos adicionais zero. São hipóteses didáticas; os slides não fixam essas taxas.
- Esforço = PFA × 10; prazo = teto(esforço/6); custo = esforço × 25. As fórmulas de reserva, capacidade e arredondamento do modelo foram preservadas.

## Protótipos e autoria

Cada arquivo preserva os 14 espaços de imagens do modelo: quatro funções de dados, quatro imagens das duas EEs (formulário e detalhe de tela), duas SEs e quatro entradas/saídas das CEs. Os dois blocos extras de EE detalham funções já contadas e acrescentam zero PF. Todos os 42 espaços de imagem das três planilhas agora contêm capturas reais do navegador com o sistema local em execução. Os registros são dados de demonstração. Nas EEs, os prints mostram formulários, seus detalhes ou registros existentes; não representam novos cadastros enviados durante a captura. Nas CEs, há imagens antes e depois de executar a consulta.

O enunciado exige declarar a contribuição pessoal nas EEs. Os nomes e recortes foram informados pelo grupo; **cada integrante ainda deve confirmar sua contribuição efetivamente realizada na aba Contribuição**. Não foi inventada autoria. As planilhas identificam essa pendência expressamente.

O plano PDF anterior foi preservado, mas está desatualizado: X02/CE12/SE13 agora estão implementados e EE10 recebe produto/posição, criando vínculo com saldo zero. Os formulários e operações de movimentação fazem a manutenção do saldo.

## Histórico — conferência da versão anterior de 28 páginas

Corrigidas as quebras de página herdadas do modelo. Cada Excel final foi renderizado em 28 páginas, com 12 protótipos completos dentro dos limites de impressão. Os textos longos foram conferidos no conteúdo extraído dos PDFs. As 80 fórmulas por arquivo (240 no total) coincidem com o recálculo independente no LibreOffice.

Detalhados os campos internos por ALI nas seis EEs selecionadas, sem alteração das contagens. As 13 verificações HTTP autenticadas foram repetidas e aprovadas; POST no provedor retorna 405. O código da aplicação não mudou nesta revisão: os 90 testes Java registrados anteriormente pertencem à validação anterior.

A única pendência de conteúdo pessoal é a confirmação da contribuição efetiva de cada integrante na identificação das EEs, sinalizada nas planilhas. Os documentos finais estão em docs/entregas-individuais. Não houve commit nem publicação remota.


## Versão atual — formato original preservado

As três cópias foram refeitas diretamente do arquivo original por substituição de conteúdo no XML. Fontes, cores, bordas, alinhamentos, mesclagens, alturas, larguras, âncoras e dimensões das imagens, margens e quebras de página foram preservados. O arquivo de estilos é idêntico ao do modelo. Foram adaptados textos, nomes de abas, valores, imagens e referências/contagens necessárias aos dados do projeto; as correções autorizadas de CE permanecem. Quando a quantidade de campos excede as linhas disponíveis, os campos são separados por ponto e vírgula e a coluna D indica quantos TD são representados. Não houve inserção de linhas ou abas.

As quebras e áreas de impressão são as originais, inclusive os blocos que continuam na página seguinte. Esta revisão substitui a configuração anterior de 28 páginas. As 80 fórmulas de cada arquivo foram recalculadas no LibreOffice, com os mesmos PFB/PFA e estimativas anteriores. O modelo original não foi alterado.


## Capturas reais e nome corrigido

Nome atualizado para Artur Ribeiro de Souza nas planilhas e na documentação. As imagens originais do navegador estão em [capturas-reais](capturas-reais/README.md): 29 capturas utilizadas em 42 posições, 14 por Excel. O manifesto registra a URL local, o momento da captura e o SHA-256. A incorporação foi conferida por igualdade dos bytes: nenhuma imagem foi redesenhada ou montada para simular a aplicação.

A substituição preservou estilos, dimensões, mesclagens, âncoras das imagens e as 240 fórmulas com seus resultados. O modelo original permanece intacto. Cada integrante ainda precisa confirmar sua contribuição pessoal nas EEs.
