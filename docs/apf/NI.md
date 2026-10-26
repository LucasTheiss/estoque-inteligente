# Nível de influência — características gerais

Avaliação técnica documental da aplicação; notas de 0 a 5. Premissas acadêmicas são identificadas, sem atribuir consenso ou votos não informados.

| Nº | Característica | NI | Justificativa | Evidência |
|---:|---|---:|---|---|
| 1 | Comunicação de dados | 5 | Aplicação on-line com HTTP e MQTT; novo cliente HTTP de fornecedores. Mais de um protocolo. | MqttGateway, controllers e FornecedorGateway |
| 2 | Processamento distribuído | 3 | Envia comandos para processamento no controlador IoT. Retorno de telemetria não demonstra transferência de processamento nas duas direções nem escolha dinâmica de CPU. | IoTService.sinalizar |
| 3 | Desempenho | 0 | Timeout e paginação não comprovam requisitos de desempenho solicitados e revistos pelo usuário. Sem essa evidência, aplica-se nota 0 da tabela. | application.properties, ProdutoRepository |
| 4 | Utilização de equipamento | 2 | Compose define limites de memória e pools; há considerações de recursos e segurança. Não exige CPU exclusiva. | compose.yaml |
| 5 | Volume de transações | 1 | Premissa acadêmica: picos mensais de inventário. Volume real não medido; revisar se houver histórico operacional. | Premissa de estimativa |
| 6 | Entrada on-line | 5 | Mais de 30% dos processos de entrada são interativos: cadastros, movimentos e configurações. | Controllers web |
| 7 | Eficiência do usuário final | 4 | Menus, mouse, paginação, navegação, ajuda contextual e poucas telas; redução de digitação por código de barras, voz e balança. Sem medição formal de eficiência. | templates, speech.js, baixa rápida |
| 8 | Atualização on-line | 4 | Maioria dos ALIs atualizada on-line, com transações, bloqueios e idempotência contra perda/duplicação. Não se assume recuperação de volumes extremos. | EstoqueService, repositories |
| 9 | Processamento complexo | 2 | Dois critérios: auditoria/segurança específica e lógica de negócio de estorno, saldo e permissões. Não se considera matemática extensiva nem reprocessamento batch complexo. | Acesso, EstoqueService, AuditoriaRepository |
| 10 | Reutilização de código | 4 | Biblioteca de regras empacotável, documentada e com consumidor externo. Extensões exigem código; não se atribui customização geral por parâmetros. | docs/reuso/README.md, scripts/empacotar-reuso.ps1 |
| 11 | Facilidade de implantação | 1 | Há procedimento de implantação; migrações Flyway criam/evoluem esquema, mas não comprovam requisitos e roteiros testados de conversão de dados legados. | README, Flyway, Compose |
| 12 | Facilidade operacional | 1 | Relatórios minimizam papel. Scripts de inicialização, sem procedimento demonstrado de backup e recuperação, não comprovam o critério composto adicional. | scripts/demo.ps1, relatórios |
| 13 | Múltiplos locais | 2 | Premissa: lojas com navegadores e equipamentos similares. Multi-loja implementado; não há evidência de parque heterogêneo e plano de manutenção por local. | Loja, acesso por tenant |
| 14 | Facilidade de mudanças | 4 | Consulta flexível sobre vários ALIs (2) e controles em tabelas alteráveis on-line com efeito imediato, como permissões/recursos (2). | ProdutoRepository.pesquisar, PermissaoService, LojaService |

**NI = 38; FA = 0,65 + 0,01 × 38 = 1.03.**

Referência: docs/pdfs/Características Gerais do Software.pdf. A Parte IV, p. 11, define a constante 0,65; a p. 15 contém 0,35 e limites incoerentes. Adota-se a p. 11, com a divergência explicitada também no Excel.
