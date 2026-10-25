# Conferência final — branch entrega-trabalho

Atualização de 24/10/2026, após completar e validar o código.

O sistema agora possui os elementos necessários aos três recortes do plano: seis ALIs atribuídos, dois AIEs compartilhados e as EEs, SEs e CEs escolhidas. Foram implementados o catálogo externo de ofertas (X02), a consulta CE12 e a comparação de preços SE13, incluindo telas, permissões, integração HTTP, configuração Docker e rotas Nginx.

As três entregas estão em [entregas-individuais](entregas-individuais/README.md), um Excel por integrante, com capa/nome, dicionários, protótipos incorporados, referências, contagens, justificativas de NI e estimativas. Na versão atual, todos os protótipos incorporados são capturas reais do navegador com o sistema em execução local e dados de demonstração.

| Integrante | Recorte | PFB | PFA |
| --- | --- | ---: | ---: |
| Artur Ribeiro de Souza | Catálogo e produtos | 52 | 55,12 |
| Cristhian Ruan de Rezende | Posições e ocupação | 50 | 53,00 |
| Lucas Andriani Theiss | Movimentações e usuários | 52 | 55,12 |

NI 41 e FA 1,06. Os recortes se sobrepõem; não somar seus PFBs. Contagem completa: 282 PFB e 298,92 PFA.

A contribuição real de cada integrante na identificação das EEs ainda precisa ser confirmada na aba Contribuição. Não foi presumida autoria a partir apenas da divisão de temas.

O modelo Excel original e o PDF do plano foram preservados. O PDF continua com as propostas antigas; as planilhas e docs/apf descrevem o estado atual. EE10 foi documentada corretamente como criação de vínculo vazio, sem receber quantidade; as movimentações mantêm saldo.

Conferência das fórmulas: matrizes/pesos e NI/PFB/FA/PFA mantidos. CE corrigida nas cópias com autorização: entrada e saída separadas e maior complexidade, conforme Parte III p. 10–12. Parte IV p. 11 usa 0,65 no FA, enquanto p. 15 contém 0,35 e limites inconsistentes; adotada a fórmula da p. 11, já usada no modelo. Reserva e custos adicionais são zero, com 10 h/PFA, 6 h produtivas/dia e R$ 25/h.

Validação: 90 testes Java com PostgreSQL/MQTT, sem falhas/erros/ignorados; build Docker; 13 verificações HTTP autenticadas; manutenção do provedor em cópia temporária; 240 resultados de fórmulas conferidos por recálculo independente em LibreOffice. Detalhes em [VALIDACAO.md](apf/VALIDACAO.md).

## Conferência final de 25/10/2026

Corrigidas as quebras de página herdadas do modelo. Cada Excel final foi renderizado em 28 páginas, com 12 protótipos completos dentro dos limites de impressão. Os textos longos foram conferidos no conteúdo extraído dos PDFs. As 80 fórmulas por arquivo (240 no total) coincidem com o recálculo independente no LibreOffice.

Detalhados os campos internos por ALI nas seis EEs selecionadas, sem alteração das contagens. As 13 verificações HTTP autenticadas foram repetidas e aprovadas; POST no provedor retorna 405. O código da aplicação não mudou nesta revisão: os 90 testes Java registrados anteriormente pertencem à validação anterior.

A única pendência de conteúdo pessoal é a confirmação da contribuição efetiva de cada integrante na identificação das EEs, sinalizada nas planilhas. Os documentos finais estão em docs/entregas-individuais. Não houve commit nem publicação remota.

## Atualização posterior — modelo original solicitado pelo usuário

As três planilhas foram refeitas diretamente de docs/exemploEntregaPf.xlsx, preservando seus estilos, dimensões, mesclagens, âncoras de imagens e configurações de impressão. São 12 abas e 14 espaços de imagens; a primeira aba identifica o integrante e os blocos extras de EE detalham as duas funções já contadas. A renderização mantém as 19 páginas do modelo, incluindo suas continuações entre páginas. Esta versão substitui o layout anterior de 28 páginas. Os 240 resultados recalculados no LibreOffice foram conferidos; PFB, NI, FA, PFA e estimativas permanecem iguais. O modelo original segue intacto. Detalhes atuais em docs/entregas-individuais/README.md.


## Atualização — capturas reais do sistema

Substituídas todas as 42 imagens das três planilhas por capturas reais do navegador autenticado na aplicação local, com os dados de demonstração existentes. Foram registradas 29 capturas distintas; os arquivos originais, URLs e hashes estão em docs/entregas-individuais/capturas-reais. EEs mostram os formulários e seus detalhes, sem enviar cadastros ou movimentos; as CEs mostram entrada e resultado de consultas executadas. Não foram simuladas confirmações de operações. Estilos, dimensões, âncoras e 240 fórmulas/resultados foram preservados. Nome corrigido para Artur Ribeiro de Souza nas planilhas e nos textos; PDFs conferidos sem ocorrência do nome antigo.
