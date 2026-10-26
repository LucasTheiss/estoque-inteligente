# Verificação da APF

## Conclusão coletiva de 25/10/2026

- Entrega coletiva: 63 abas, 55 funções, 4494 fórmulas e 93 imagens reais incorporadas.
- 322 verificações no LibreOffice: totais, pesos/complexidades, fronteiras das matrizes, TD repetidos e sensibilidade de NI/produtividade/capacidade/equipe/valor-hora. Entradas restauradas; recálculo automático e caches salvos sem erros.
- 217 páginas de impressão A3; 93 imagens inteiras, dentro das páginas, e conteúdo textual das células localizado na extração. Modelo e três planilhas individuais preservados por SHA-256.
- Nova evidência da CE05 e das consultas complementares; SE08 mostra peso/subtotal reais do simulador; EE27 mostra confirmação real do simulador LED; SE09 inclui PDF exportado pelo sistema.
- Totais atuais: PFB 283, NI 38, FA 1,03, PFA 291,49, esforço 2.914,90 horas-pessoa, 162 dias úteis, R$ 72.872,50, sob premissas documentadas.
- Microsoft Excel não instalado nesta máquina: abertura nativa não verificada. O pacote foi regravado e validado no LibreOffice.
- Sem alteração no código da aplicação nesta conclusão; logs anteriores de 90 testes Java e 13 JavaScript preservados, sem alegar nova execução.
- Registros completos: `coletiva/validacao-final.json`, `coletiva/validacao-impressao.json` e `coletiva/capturas/manifesto-incorporado.json`.

## Registros históricos — números e estados das respectivas revisões

## Ampliação de 24/10/2026

- Docker operacional; suíte Java com PostgreSQL e MQTT: **90 testes, zero falhas, zero erros e zero ignorados**. Inclui contrato HTTP de ofertas, erros/limites, filtros, comparação por loja/GTIN/unidade, percentual, preço zero, produto inativo, autorização e renderização/escape das telas novas.
- Build Docker concluído; aplicação e os dois provedores iniciados. Nginx passou na verificação de configuração e encaminha `/ofertas` e `/comparacao-precos`.
- Verificação HTTP autenticada pela porta 8080: 13 respostas 200, incluindo todas as telas dos recortes, ofertas filtradas e comparação de preços. Com preço interno 249,90 e oferta 89,90, foram conferidos diferença −160,00 e percentual −64,03.
- Provedor X02: GET retorna três ofertas; POST retorna 405. Manutenção CLI testada em cópia temporária: inclusão/exclusão e rejeição de preço negativo, sem alterar o catálogo original.
- Contagem completa recalculada: NI 41, PFB 282, FA 1,06, PFA 298,92. Recortes individuais: 52/50/52 PFB, com as mesmas premissas didáticas.
- Três Excel com capa e nomes, 12 protótipos incorporados em cada documento. As 80 fórmulas de cada arquivo foram recalculadas em LibreOffice e comparadas com os valores esperados: 240 resultados coincidentes, sem erro de fórmula.
- Modelo original preservado. Correção da lógica CE autorizada pelo usuário; demais regras de cálculo preservadas com referências adaptadas. A confirmação da contribuição pessoal continua pendente, explicitamente indicada nos documentos.

## Histórico — entrega reduzida

Data: 21/10/2026. Ambiente: JDK 21, Maven, PostgreSQL e MQTT locais por Docker Compose.

- Suíte completa após a redução: **83 testes, zero falhas, zero erros e zero ignorados**, com banco e MQTT habilitados.
- Os testes dos relatórios validam o total conhecido de R$ 110,00, quantidade fracionada, produto sem saldo, isolamento entre lojas, resultado vazio e autorização.
- O fluxo verifica saída e estorno com sinais opostos e o limite do dia em Brasília: 02:59 UTC no dia anterior local e 03:00 UTC no dia seguinte.
- Os testes do cliente externo verificam consulta, filtro, leitura atualizada e falhas do provedor.
- Os testes MVC renderizam os templates reais de fornecedores e dos dois relatórios, com escape de HTML, além das telas originais reaproveitadas.
- O build Docker concluiu; aplicação, banco, MQTT, provedor e NGINX ficaram saudáveis. A configuração do NGINX passou em nginx -t e foi recarregada.
- A verificação HTTP autenticada pela porta 8080 retornou 200 para fornecedores (com e sem filtro), os dois relatórios, modelos, usuários, mapa e tela inicial. O seletor dos relatórios não contém as opções removidas.
- O cálculo foi refeito a partir de contagem.json: **NI 41; PFB 269; FA 1,06; PFA 285,14**. Esforço: 2.851,40 horas-pessoa; prazo arredondado: 159 dias úteis; custo: R$ 71.285,00, nas premissas acadêmicas documentadas.
- O PDF explicativo foi regenerado com seis páginas. Todas foram renderizadas e inspecionadas visualmente, e os totais foram conferidos no texto extraído.

As integrações de teste usam esquemas temporários isolados. Os testes relacionados às funcionalidades adicionais removidas também foram retirados; as verificações das funções originais foram preservadas. Não foi realizado commit nem publicação remota.

## Conferência final de 25/10/2026

Corrigidas as quebras de página herdadas do modelo. Cada Excel final foi renderizado em 28 páginas, com 12 protótipos completos dentro dos limites de impressão. Os textos longos foram conferidos no conteúdo extraído dos PDFs. As 80 fórmulas por arquivo (240 no total) coincidem com o recálculo independente no LibreOffice.

Detalhados os campos internos por ALI nas seis EEs selecionadas, sem alteração das contagens. As 13 verificações HTTP autenticadas foram repetidas e aprovadas; POST no provedor retorna 405. O código da aplicação não mudou nesta revisão: os 90 testes Java registrados anteriormente pertencem à validação anterior.

A única pendência de conteúdo pessoal é a confirmação da contribuição efetiva de cada integrante na identificação das EEs, sinalizada nas planilhas. Os documentos finais estão em docs/entregas-individuais. Não houve commit nem publicação remota.

## Atualização posterior — modelo original solicitado pelo usuário

As três planilhas foram refeitas diretamente de docs/exemploEntregaPf.xlsx, preservando seus estilos, dimensões, mesclagens, âncoras de imagens e configurações de impressão. São 12 abas e 14 espaços de imagens; a primeira aba identifica o integrante e os blocos extras de EE detalham as duas funções já contadas. A renderização mantém as 19 páginas do modelo, incluindo suas continuações entre páginas. Esta versão substitui o layout anterior de 28 páginas. Os 240 resultados recalculados no LibreOffice foram conferidos; PFB, NI, FA, PFA e estimativas permanecem iguais. O modelo original segue intacto. Detalhes atuais em docs/entregas-individuais/README.md.


## Atualização — capturas reais do sistema

Substituídas todas as 42 imagens das três planilhas por capturas reais do navegador autenticado na aplicação local, com os dados de demonstração existentes. Foram registradas 29 capturas distintas; os arquivos originais, URLs e hashes estão em docs/entregas-individuais/capturas-reais. EEs mostram os formulários e seus detalhes, sem enviar cadastros ou movimentos; as CEs mostram entrada e resultado de consultas executadas. Não foram simuladas confirmações de operações. Estilos, dimensões, âncoras e 240 fórmulas/resultados foram preservados. Nome corrigido para Artur Ribeiro de Souza nas planilhas e nos textos; PDFs conferidos sem ocorrência do nome antigo.
