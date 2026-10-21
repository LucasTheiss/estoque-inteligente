# Verificação da entrega reduzida

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
