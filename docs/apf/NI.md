# Nível de influência — características gerais

Avaliação técnica proposta para a aplicação entregue. Equipe: três integrantes. Não representa votos individuais nem consenso fictício; a equipe pode revisar as premissas antes de apresentar.

| Nº | Característica | NI | Justificativa | Evidência |
|---:|---|---:|---|---|
| 1 | Comunicação de dados | 5 | Aplicação on-line com HTTP e MQTT; novo cliente HTTP de fornecedores. Mais de um protocolo. | MqttGateway, controllers e FornecedorGateway |
| 2 | Processamento distribuído | 3 | Envia comandos para processamento no controlador IoT. Retorno de telemetria não demonstra transferência de processamento nas duas direções nem escolha dinâmica de CPU. | IoTService.sinalizar |
| 3 | Desempenho | 1 | Há limites de conexão, paginação e timeouts, mas não foi fornecido SLA crítico nem evidência de análise de desempenho rigorosa. | application.properties, ProdutoRepository |
| 4 | Utilização de equipamento | 2 | Compose define limites de memória e pools; há considerações de recursos e segurança. Não exige CPU exclusiva. | compose.yaml |
| 5 | Volume de transações | 1 | Premissa acadêmica: picos mensais de inventário. Volume real não medido; revisar se houver histórico operacional. | Premissa de estimativa |
| 6 | Entrada on-line | 5 | Mais de 30% dos processos de entrada são interativos: cadastros, movimentos e configurações. | Controllers web |
| 7 | Eficiência do usuário final | 4 | Menus, mouse, paginação, navegação, ajuda contextual e poucas telas; redução de digitação por código de barras, voz e balança. Sem medição formal de eficiência. | templates, speech.js, baixa rápida |
| 8 | Atualização on-line | 4 | Maioria dos ALIs atualizada on-line, com transações, bloqueios e idempotência contra perda/duplicação. Não se assume recuperação de volumes extremos. | EstoqueService, repositories |
| 9 | Processamento complexo | 2 | Dois critérios: auditoria/segurança específica e lógica de negócio de estorno, saldo e permissões. Não se considera matemática extensiva nem reprocessamento batch complexo. | Acesso, EstoqueService, AuditoriaRepository |
| 10 | Reutilização de código | 4 | Biblioteca de regras empacotável, documentada e com consumidor externo. Extensões exigem código; não se atribui customização geral por parâmetros. | docs/reuso/README.md, scripts/empacotar-reuso.ps1 |
| 11 | Facilidade de implantação | 2 | Roteiros de execução e migrações preparados. Não há evidência de conversão automatizada de dados legados para justificar 4/5. | README, Flyway, Compose |
| 12 | Facilidade operacional | 2 | Inicialização por scripts com intervenção (1); consultas e relatórios minimizam papel (1). Não se presume backup/recuperação automática. | scripts/demo.ps1, relatórios |
| 13 | Múltiplos locais | 2 | Premissa: lojas com navegadores e equipamentos similares. Multi-loja implementado; não há evidência de parque heterogêneo e plano de manutenção por local. | Loja, acesso por tenant |
| 14 | Facilidade de mudanças | 4 | Consulta flexível sobre vários ALIs (2) e controles em tabelas alteráveis on-line com efeito imediato, como permissões/recursos (2). | ProdutoRepository.pesquisar, PermissaoService, LojaService |

**NI = 5 + 3 + 1 + 2 + 1 + 5 + 4 + 4 + 2 + 4 + 2 + 2 + 2 + 4 = 41.**

Referência: [tabela das características fornecida pelo professor](../pdfs/Características%20Gerais%20do%20Software.pdf). Notas altas não foram atribuídas apenas porque uma tecnologia está presente: por exemplo, Docker não comprova distribuição dinâmica de processamento, e índices não comprovam SLA de desempenho.

Em 24/10/2026, a inclusão do cliente HTTP de ofertas mantém as mesmas características e notas. A presença de um segundo AIE não soma automaticamente pontos ao NI.

As notas de volume e múltiplos locais são premissas declaradas. Caso não se espere pico mensal, a nota de volume deve passar a zero; sem utilização em múltiplos locais, a nota correspondente também deve ser revista. O script recalcula automaticamente o impacto ao editar contagem.json.

