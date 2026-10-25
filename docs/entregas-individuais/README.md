# Três entregas individuais — Estoque Inteligente

Geradas em 24/10/2026 a partir de `docs/exemploEntregaPf.xlsx`, preservado intacto. Cada Excel é um documento único com capa, nome completo, dados, transações, protótipos incorporados e estimativas.

| Integrante | Documento | ALIs | EEs | SEs | PFB | PFA | Esforço | Prazo | Custo |
| --- | --- | --- | --- | --- | ---: | ---: | ---: | ---: | ---: |
| Arthur Ribeiro | [Catálogo e produtos](catalogo_produtos.xlsx) | A01/A02 | EE04/EE01 | SE06/SE02 | 52 | 55,12 | 551,20 h | 92 dias úteis | R$ 13.780,00 |
| Cristhian Ruan de Rezende | [Posições e ocupação](posicoes_ocupacao.xlsx) | A03/A04 | EE07/EE10 | SE07/SE04 | 50 | 53,00 | 530,00 h | 89 dias úteis | R$ 13.250,00 |
| Lucas Andriani Theiss | [Movimentações e usuários](movimentacoes_usuarios.xlsx) | A05/A06 | EE21/EE13 | SE04/SE13 | 52 | 55,12 | 551,20 h | 92 dias úteis | R$ 13.780,00 |

Todas usam X01/X02 e CE04/CE12. Os recortes se sobrepõem: não somar os três PFBs como tamanho da aplicação. Na contagem completa, PFB = 282 e PFA = 298,92.

## Conferência das fórmulas

Roteiros de estudo e apresentação: [Arthur Ribeiro](roteiro_catalogo_produtos.md), [Cristhian Ruan de Rezende](roteiro_posicoes_ocupacao.md) e [Lucas Andriani Theiss](roteiro_movimentacoes_usuarios.md). Cada roteiro explica as funções do respectivo Excel, os protótipos, os cálculos, as justificativas de NI e perguntas prováveis. As planilhas permanecem inalteradas.

- Matrizes e pesos ALI/AIE/EE/SE foram mantidos do modelo; faixas e pesos conferidos com o material da disciplina. Referências e faixas de células foram adaptadas à quantidade real de campos e funções.
- **Correção autorizada nas CEs:** o modelo aplicava uma matriz única ao total de dados. A Parte III, p. 10–12, exige entrada e saída separadas e escolha da maior complexidade. As cópias fazem isso, sem contar dados repetidos duas vezes. Ambas as CEs permanecem simples, 3 PF cada.
- NI = soma das 14 notas = 41; FA = 0,65 + 0,01 × NI = 1,06; PFA = PFB × FA. Parte IV, p. 11, sustenta a constante 0,65. A p. 15 apresenta 0,35 e limites inconsistentes; essa divergência está registrada nas planilhas.
- Premissas: 10 h/PFA, uma pessoa por recorte, jornada de 8 h × 75% = 6 h produtivas/dia, R$ 25/h, reserva zero, custos adicionais zero. São hipóteses didáticas; os slides não fixam essas taxas.
- Esforço = PFA × 10; prazo = teto(esforço/6); custo = esforço × 25. As fórmulas de reserva, capacidade e arredondamento do modelo foram preservadas.

## Protótipos e autoria

Cada arquivo incorpora 12 imagens de baixa fidelidade: quatro para funções de dados, duas para EEs, duas para SEs e quatro para as entradas/saídas das duas CEs. São exemplos ilustrativos baseados no código, não capturas de produção. AIEs possuem manutenção externa implementada; os registros demonstrativos são fictícios.

O enunciado exige declarar a contribuição pessoal nas EEs. Os nomes e recortes foram informados pelo grupo; **cada integrante ainda deve confirmar sua contribuição efetivamente realizada na aba Contribuição**. Não foi inventada autoria. As planilhas identificam essa pendência expressamente.

O plano PDF anterior foi preservado, mas está desatualizado: X02/CE12/SE13 agora estão implementados e EE10 recebe produto/posição, criando vínculo com saldo zero. Os formulários e operações de movimentação fazem a manutenção do saldo.

## Conferência final de 25/10/2026

Corrigidas as quebras de página herdadas do modelo. Cada Excel final foi renderizado em 28 páginas, com 12 protótipos completos dentro dos limites de impressão. Os textos longos foram conferidos no conteúdo extraído dos PDFs. As 80 fórmulas por arquivo (240 no total) coincidem com o recálculo independente no LibreOffice.

Detalhados os campos internos por ALI nas seis EEs selecionadas, sem alteração das contagens. As 13 verificações HTTP autenticadas foram repetidas e aprovadas; POST no provedor retorna 405. O código da aplicação não mudou nesta revisão: os 90 testes Java registrados anteriormente pertencem à validação anterior.

A única pendência de conteúdo pessoal é a confirmação da contribuição efetiva de cada integrante na identificação das EEs, sinalizada nas planilhas. Os documentos finais estão em docs/entregas-individuais. Não houve commit nem publicação remota.
