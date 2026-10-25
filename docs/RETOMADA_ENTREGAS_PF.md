# Retomada — trabalho concluído em 24/10/2026

Branch entrega-trabalho. Alterações salvas, sem commit nem publicação remota.

Implementados X02/CE12/SE13, telas, autorização, integração HTTP, provedor externo, configuração Docker e encaminhamento Nginx. 90 testes Java passaram com PostgreSQL/MQTT. Build e verificação HTTP autenticada concluídos. Docker está operacional.

Três Excel gerados em docs/entregas-individuais: Artur Ribeiro de Souza (catálogo/produtos), Cristhian Ruan de Rezende (posições/ocupação), Lucas Andriani Theiss (movimentações/usuários). Cada arquivo tem capa, 12 imagens de protótipos, campos/RL/AR/TD, fórmulas e estimativas. PFB 52/50/52; PFA 55,12/53/55,12; NI 41; FA 1,06. Código completo: PFB 282.

O usuário autorizou corrigir a lógica de CE nas cópias. O modelo original está intacto. Matrizes e fórmulas conferidas com slides; ressalva da constante 0,35 da p. 15 da Parte IV documentada (adotado 0,65 da p. 11). Todas as 240 fórmulas foram recalculadas independentemente em LibreOffice e coincidem.

Única pendência de conteúdo pessoal: confirmar o que cada integrante efetivamente realizou na identificação das EEs. Não inventar autoria; os documentos marcam essa confirmação.

Arquivos principais: docs/entregas-individuais/README.md, docs/CONFERENCIA_ENTREGAS_PF.md, docs/apf/VALIDACAO.md. Material antigo em PDFs foi preservado; o PDF de divisão ainda chama as funções novas de propostas.

Ferramentas temporárias em tmp: gerar_entregas.py (geração), check_entregas.py (conferência com LibreOffice), smoke_ofertas.py (HTTP autenticado), docdeps (bibliotecas Python usadas via container), lo-check (cópias recalculadas e PDFs de inspeção). Não são dependências do aplicativo.

Serviços de demonstração estão iniciados via Docker Compose; aplicação em http://localhost:8080. Não descartar arquivos não rastreados do usuário (modelo Excel, PDF de divisão e slides).

## Conferência final de 25/10/2026

Corrigidas as quebras de página herdadas do modelo. Cada Excel final foi renderizado em 28 páginas, com 12 protótipos completos dentro dos limites de impressão. Os textos longos foram conferidos no conteúdo extraído dos PDFs. As 80 fórmulas por arquivo (240 no total) coincidem com o recálculo independente no LibreOffice.

Detalhados os campos internos por ALI nas seis EEs selecionadas, sem alteração das contagens. As 13 verificações HTTP autenticadas foram repetidas e aprovadas; POST no provedor retorna 405. O código da aplicação não mudou nesta revisão: os 90 testes Java registrados anteriormente pertencem à validação anterior.

A única pendência de conteúdo pessoal é a confirmação da contribuição efetiva de cada integrante na identificação das EEs, sinalizada nas planilhas. Os documentos finais estão em docs/entregas-individuais. Não houve commit nem publicação remota.

## Atualização posterior — modelo original solicitado pelo usuário

As três planilhas foram refeitas diretamente de docs/exemploEntregaPf.xlsx, preservando seus estilos, dimensões, mesclagens, âncoras de imagens e configurações de impressão. São 12 abas e 14 espaços de imagens; a primeira aba identifica o integrante e os blocos extras de EE detalham as duas funções já contadas. A renderização mantém as 19 páginas do modelo, incluindo suas continuações entre páginas. Esta versão substitui o layout anterior de 28 páginas. Os 240 resultados recalculados no LibreOffice foram conferidos; PFB, NI, FA, PFA e estimativas permanecem iguais. O modelo original segue intacto. Detalhes atuais em docs/entregas-individuais/README.md.


## Atualização — capturas reais do sistema

Substituídas todas as 42 imagens das três planilhas por capturas reais do navegador autenticado na aplicação local, com os dados de demonstração existentes. Foram registradas 29 capturas distintas; os arquivos originais, URLs e hashes estão em docs/entregas-individuais/capturas-reais. EEs mostram os formulários e seus detalhes, sem enviar cadastros ou movimentos; as CEs mostram entrada e resultado de consultas executadas. Não foram simuladas confirmações de operações. Estilos, dimensões, âncoras e 240 fórmulas/resultados foram preservados. Nome corrigido para Artur Ribeiro de Souza nas planilhas e nos textos; PDFs conferidos sem ocorrência do nome antigo.
