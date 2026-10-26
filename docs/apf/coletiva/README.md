# Entrega coletiva de Análise de Pontos de Função

Revisão concluída em 25/10/2026 (horário de Brasília). Entregável principal: [entrega-coletiva-apf.xlsx](entrega-coletiva-apf.xlsx). A cópia em `outputs/apf-coletiva/entrega-coletiva-apf.xlsx` é idêntica.

Integrantes: **Artur Ribeiro de Souza**, **Cristhian Ruan de Rezende** e **Lucas Andriani Theiss**.

## Conteúdo e atendimento ao enunciado

| Exigência | Onde conferir no Excel | Atendimento |
|---|---|---|
| ALIs, AIEs, itens e registros lógicos | A01–A09, X01, X02; Catálogo | 9 ALIs e 2 AIEs; TD/RL e PF calculados; capturas reais |
| EE, incluir/alterar/excluir, ALIs e dados | EE01–EE28 | 28 EEs; CRUD de modelos EE04/05/06; referências, TD/AR e imagens de entrada/resultado |
| Ao menos cinco SEs e derivação | SE02/04/06/07/08/13; Exemplos SE | Seis saídas numéricas; regras e exemplos calculáveis; também SE09 (exportação/auditoria) e SE10 (indicador de estorno) |
| Quatro CEs com entrada/saída | CE04/05/08/12 em destaque | Oito CEs no catálogo; todas com entrada e saída reais, referências por lado e complexidade máxima |
| NI, PFB, FA e PFA | Resumo, Matrizes, NI e estimativas | Fórmulas nativas; 14 notas NI justificadas |
| Esforço, prazo e custo | NI e estimativas | Premissas editáveis, unidades e fórmulas vinculadas |
| Documento único com identificação | Capa e demais 62 abas | Imagens incorporadas; não é necessário anexar arquivos para visualizar os protótipos |

## Resultado coletivo

| Tipo | Quantidade | PF |
|---|---:|---:|
| ALI | 9 | 63 |
| AIE | 2 | 10 |
| EE | 28 | 135 |
| SE | 8 | 48 |
| CE | 8 | 27 |
| **Total** | **55** | **283** |

- NI = 38; FA = 0,65 + 0,01 × 38 = 1,03; PFA = 283 × 1,03 = **291,49**.
- Premissas acadêmicas: 10 horas-pessoa/PFA; 3 integrantes; 8 horas/dia × 75% produtivos = 6 horas úteis/pessoa/dia; R$ 25/hora-pessoa.
- Esforço: **2.914,90 horas-pessoa**. Prazo: **161,9389 dias úteis**, arredondado para **162**. Custo: **R$ 72.872,50**.
- Estima-se o desenvolvimento integral da aplicação. Não é estimativa das horas restantes da atividade; infraestrutura, impostos e reserva estão explicitados como não estimados/zero.

## Correções da revisão

CE10 foi reclassificada como SE10: o indicador Estornado é derivado por EXISTS de um movimento reversor. Não houve duplicação de função. Foram corrigidos os TD de CE06, SE06 e SE09 e reduzidas três notas NI conforme a evidência disponível (41 → 38). O catálogo registra os motivos.

As referências de entrada e saída das CEs foram revistas separadamente. CE07 usa A06 na seleção e A06/A07 na saída; CE09 e CE11 não têm validação de dado de negócio na entrada, usando os arquivos para a saída. A pontuação dessas consultas não mudou. O seletor q de produtos na tela de vínculos reutiliza SE06.

SE08 foi corrigida para `/movimentar`, loja `granel`. A captura mostra 1,425 kg e R$ 42,61, derivados de 1,440 − 0,015 kg e preço R$ 29,90/kg. LED e balança usam o simulador MQTT real e estão identificados como simulação. SE09 inclui a primeira página do PDF efetivamente exportado.

## Validação realizada nesta conclusão

- Abertura, gravação e recálculo automático no **LibreOffice Calc**, via UNO.
- **4494 fórmulas** preservadas, sem erros e com valores recalculados salvos.
- **322 verificações** de resultados, classificação, limites de matrizes, TD repetidos e sensibilidade de NI/produtividade/equipe/capacidade/valor-hora, com restauração das entradas.
- **93 imagens reais incorporadas** nas 55 fichas. Manifesto identifica arquivo, função, origem, data e hash.
- Impressão configurada em A3: **217 páginas** conferidas por extração de texto e limites das imagens. Nenhuma imagem dividida ou fora da página; nenhum texto de célula ausente na extração.
- Modelo e três Excel individuais preservados, com SHA-256 conferido.
- Os logs anteriores registram 90 testes Java e 13 JavaScript aprovados; essa suíte não foi repetida nesta conclusão, que não alterou o código da aplicação.

O Microsoft Excel **não está instalado nesta máquina**. O pacote XLSX foi regravado e validado no LibreOffice, mas não se afirma ter reproduzido ou confirmado a solução do erro COM observado no Excel do ambiente anterior. A abertura nativa no Microsoft Excel permanece uma limitação de verificação, não ocultada por conversão para PDF.

## Fontes e reprodução

- `catalogo-revisado.json`: catálogo e justificativas da revisão; `../contagem.json`, `../resultados.json`, `../CALCULOS.md`, `../FICHAS.md` e `../NI.md`: documentação coletiva sincronizada.
- `capturas/manifesto-incorporado.json`: todas as imagens do Excel. `capturas/SE09-exportacao-real.pdf`: saída preservada da aplicação.
- `validacao-final.json` e `validacao-impressao.json`: resultados detalhados da conferência.
- `originais-sha256.json`: integridade do modelo e dos três recortes.
- Scripts em `scripts/`: `sincronizar-apf.py`, `calcular-apf.ps1`, `finalizar-apf.py`, `validar-apf-libreoffice.py`, `auditar-entrega-apf.py`, `registrar-conclusao-apf.py` e scripts de captura. Após editar/gerar o XLSX, executar o recálculo antes de publicar a cópia.

As notas NI são avaliação técnica com premissas documentadas. Não se inventam contribuições individuais, votos ou consenso. Os recortes individuais e PDFs explicativos antigos representam suas revisões históricas e não devem ser somados nem usados como totais atuais do coletivo.

SHA-256 do XLSX final: `32d6958bf8074f05773b69fb414c44b69bcd4a9d8320a64379b0712dca85dadc`.
