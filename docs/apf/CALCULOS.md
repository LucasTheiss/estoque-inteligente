# Memoria de calculo gerada

Fonte: contagem.json. Recalcular com scripts/calcular-apf.ps1. AR/RL = arquivos referenciados ou registros logicos. Em CE, a complexidade e o maior resultado entre entrada e saida.

| ID | Tipo | Funcao | AR/RL | TD | TD entrada CE | TD saida CE | Complexidade | PF |
|---|---|---|---:|---:|---:|---:|---|---:|
| A01 | ALI | Modelos de produto | 1 | 5 | 0 | 0 | Simples | 7 |
| A02 | ALI | Produtos | 1 | 11 | 0 | 0 | Simples | 7 |
| A03 | ALI | Posições | 1 | 11 | 0 | 0 | Simples | 7 |
| A04 | ALI | Estoque por posição / vínculos | 1 | 4 | 0 | 0 | Simples | 7 |
| A05 | ALI | Movimentações | 1 | 12 | 0 | 0 | Simples | 7 |
| A06 | ALI | Usuários | 1 | 6 | 0 | 0 | Simples | 7 |
| A07 | ALI | Permissões por perfil | 1 | 3 | 0 | 0 | Simples | 7 |
| A08 | ALI | Lojas e configuração | 1 | 12 | 0 | 0 | Simples | 7 |
| A09 | ALI | Auditoria consultável | 1 | 6 | 0 | 0 | Simples | 7 |
| X01 | AIE | Fornecedores externos | 1 | 5 | 0 | 0 | Simples | 5 |
| EE01 | EE | Incluir produto, inclusive grade e saldo inicial | 6 | 16 | 15 | 1 | Complexa | 6 |
| EE02 | EE | Alterar produto | 4 | 13 | 12 | 1 | Complexa | 6 |
| EE03 | EE | Excluir produto logicamente | 3 | 3 | 2 | 1 | Moderada | 4 |
| EE04 | EE | Incluir modelo | 2 | 5 | 4 | 1 | Moderada | 4 |
| EE05 | EE | Alterar modelo | 2 | 6 | 5 | 1 | Moderada | 4 |
| EE06 | EE | Excluir modelo | 3 | 3 | 2 | 1 | Moderada | 4 |
| EE07 | EE | Incluir posição | 2 | 10 | 9 | 1 | Moderada | 4 |
| EE08 | EE | Alterar posição | 3 | 11 | 10 | 1 | Complexa | 6 |
| EE09 | EE | Excluir posição logicamente | 3 | 3 | 2 | 1 | Moderada | 4 |
| EE10 | EE | Incluir vínculo | 4 | 4 | 3 | 1 | Moderada | 4 |
| EE11 | EE | Alterar vínculo vazio | 4 | 6 | 5 | 1 | Complexa | 6 |
| EE12 | EE | Excluir vínculo vazio | 4 | 4 | 3 | 1 | Moderada | 4 |
| EE13 | EE | Incluir usuário | 3 | 5 | 4 | 1 | Complexa | 6 |
| EE14 | EE | Alterar usuário | 3 | 6 | 5 | 1 | Complexa | 6 |
| EE15 | EE | Excluir usuário logicamente | 3 | 5 | 4 | 1 | Complexa | 6 |
| EE16 | EE | Incluir loja e administrador | 4 | 10 | 9 | 1 | Complexa | 6 |
| EE17 | EE | Alterar loja e recursos | 3 | 9 | 8 | 1 | Complexa | 6 |
| EE18 | EE | Excluir loja logicamente | 2 | 3 | 2 | 1 | Simples | 3 |
| EE19 | EE | Alterar matriz de permissões | 2 | 5 | 4 | 1 | Moderada | 4 |
| EE20 | EE | Alterar configuração IoT | 2 | 6 | 5 | 1 | Moderada | 4 |
| EE21 | EE | Registrar entrada | 5 | 7 | 6 | 1 | Complexa | 6 |
| EE22 | EE | Registrar saída | 5 | 7 | 6 | 1 | Complexa | 6 |
| EE23 | EE | Ajustar saldo | 5 | 7 | 6 | 1 | Complexa | 6 |
| EE24 | EE | Estornar movimentação | 5 | 4 | 3 | 1 | Moderada | 4 |
| EE25 | EE | Autenticar usuário | 3 | 5 | 4 | 1 | Complexa | 6 |
| EE26 | EE | Selecionar loja de trabalho | 2 | 3 | 2 | 1 | Simples | 3 |
| EE27 | EE | Sinalizar posição por LED | 3 | 3 | 2 | 1 | Moderada | 4 |
| EE28 | EE | Encerrar sessão | 0 | 2 | 1 | 1 | Simples | 3 |
| SE02 | SE | Valor potencial do estoque | 2 | 8 | 2 | 6 | Moderada | 5 |
| SE04 | SE | Fluxo de movimentações | 2 | 9 | 4 | 5 | Moderada | 5 |
| SE06 | SE | Pesquisar estoque e localizar produto | 4 | 18 | 4 | 14 | Complexa | 7 |
| SE07 | SE | Mapa operacional de posições | 4 | 13 | 2 | 11 | Complexa | 7 |
| SE08 | SE | Ler peso líquido e subtotal | 2 | 9 | 4 | 5 | Moderada | 5 |
| SE09 | SE | Exportar histórico | 6 | 13 | 6 | 7 | Complexa | 7 |
| CE04 | CE | Consultar fornecedores externos | 1 | 8 | 3 | 5 | Simples | 3 |
| CE05 | CE | Consultar modelos | 1 | 6 | 3 | 3 | Simples | 3 |
| CE06 | CE | Consultar vínculos | 4 | 14 | 4 | 10 | Complexa | 6 |
| CE07 | CE | Consultar equipe e permissões | 2 | 6 | 3 | 3 | Simples | 3 |
| CE08 | CE | Consultar lojas | 1 | 9 | 3 | 6 | Simples | 3 |
| CE09 | CE | Consultar configuração IoT | 1 | 6 | 2 | 4 | Simples | 3 |
| CE10 | CE | Consultar histórico operacional | 5 | 13 | 5 | 8 | Complexa | 6 |
| CE11 | CE | Consultar auditoria | 2 | 6 | 2 | 4 | Simples | 3 |

## Totais por tipo

- ALI: 9 funcoes; 63 PF.
- AIE: 1 funcoes; 5 PF.
- EE: 28 funcoes; 135 PF.
- SE: 6 funcoes; 36 PF.
- CE: 8 funcoes; 30 PF.

## Resultado

- NI: 41
- PFB: 269
- FA: 1.06
- PFA: 285.14
- esforcoHoras: 2851.40
- prazoDias: 158.41
- prazoDiasUteisInteiros: 159
- custoReais: 71285.00
