# Correções e ambiguidades da especificação

Fonte vigente: `especificacao.pdf`, 18 páginas, recebida em 07/10/2026. A ausência
anterior da especificação foi resolvida. O usuário revogou as limitações do README
antigo; a arquitetura alvo é Flask/PostgreSQL/Jinja2/MQTT/NGINX (pp. 17–18).

| Referência | Problema que precisa de correção | Decisão de implementação / pendência |
| --- | --- | --- |
| §3.2, p. 17 | Cita Float e simultaneamente exige precisão decimal de peso e preço. | Usar Decimal e PostgreSQL NUMERIC, 3 casas para kg e 2 para preço; unidades convencionais são inteiras. Precisão máxima deve ser formalizada. |
| Tela 8, p. 14 | Texto diz “exportação em pdf para csv”. | Exportações independentes PDF e CSV, exclusivas do Enterprise. |
| Tela 9, p. 15 | Legenda diz “exclusivo administradores”, mas matriz permite CRUD de usuários ao gerente. | Seguir matriz: gerente gerencia usuários não administradores; admin altera permissões e parâmetros IoT. Registrar esta regra explicitamente. |
| RF003/RF004/RNF006 | Não especifica exclusão de vínculo com saldo nem concorrência. | Vínculo só é excluído vazio; alteração de saldo é uma movimentação transacional auditada. Saldo por produto/posição, nunca duplicado no cadastro do produto. |
| RF014 | Não define estorno de ajuste, repetição de estorno ou entrada já consumida. | Estornar apenas entradas/saídas, uma vez; entrada só é estornada se houver saldo suficiente. Justificativa obrigatória; preservar original e gerar movimento inverso. |
| RF001/RF002/RF005/RF016 | Exclusão de cadastros conflita com histórico identificável. | Exclusão lógica de produto, posição e usuário; recusar produto/posição com saldo. Preservar IDs e logs. Não reusar SKU/código/usuário excluídos sem política de restauração. Estorno de cadastro excluído exige restauração, ainda a definir. |
| RNF001 | “Condições normais” e instante de medição não definidos. | Separar consulta, publicação MQTT e confirmação física. PUBACK não prova que o LED acendeu. Falta carga de referência e dispositivo para medir ponta a ponta. |
| RNF005 | Disponibilidade de 99% sem janela de observação ou calendário comercial. | Healthcheck e reinício dos serviços; comprovação depende de operação monitorada durante janela definida. |
| RNF007 / §3.3 | 300 MB sem delimitar contêineres centrais; estimativa Flask de 64 MB não é medição. | Compose limita app + PostgreSQL + MQTT + NGINX a 272 MiB (cerca de 285 MB); túnel opcional fora do grupo. Medir consumo e comportamento sob carga antes de afirmar conformidade. |
| Tela 11 | Não define unidade/capacidade, mistura gavetas e volume físico. | Capacidade por posição em un ou kg, compatível com produto. Percentual = soma dos saldos / capacidade; sobreocupação exibida, não bloqueada. Volume geométrico exige dimensões não especificadas. |
| RF008 | “Todas as informações” não define operadores ou paginação. | Busca por termos nos campos, características JSON, saldo, setor e posição; 100 resultados/página. Definir filtros especializados se necessários. |
| Histórico | Não define retenção, fuso nem limite de exportação. | Datas UTC; até 5.000 movimentos por consulta/exportação e 100 logs recentes. Formalizar retenção e paginação para volumes maiores. |
| RF009/§3.3 | Faltam skill ID, vínculo de contas, credenciais e endereço HTTPS Alexa. | Integração externa requer provisionamento da skill, conta vinculada e túnel HTTPS. Voz no navegador não substitui entrega da skill. |
| RNF010/Tela 10 | Não define payload/ACK, TLS, pinagem nem credenciais por dispositivo. | Tópicos segregados por slug; comandos não retidos com timeout. Formalizar contrato e homologar firmware/dispositivos. |
| §1.3 | Anuncia seis personas e mapas de empatia; só apresenta um mapa de empatia. | Completar os demais mapas se forem entregáveis acadêmicos. |

Estas decisões não reduzem o escopo do PDF. Itens dependentes de hardware,
credenciais externas e observação operacional permanecem explicitamente pendentes.
