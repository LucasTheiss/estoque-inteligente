# Ambiguidades da especificação e limites ainda abertos

Revisado em 15/10/2026. Este arquivo registra decisões de produto e limites de
validação que ainda podem ser relevantes para o escopo completo do sistema. As
pendências abaixo **não representam itens faltantes da rubrica acadêmica**; a
situação da atividade está em [REQUISITOS.md](REQUISITOS.md).

A fonte funcional é `especificacao.pdf` (19 páginas). A arquitetura atual é
Java/Spring MVC/Thymeleaf/PostgreSQL/MQTT/NGINX; a aplicação Flask anterior e seu
banco legado não são evidência da implementação atual.

## Decisões já implementadas e verificadas

| Tema | Decisão atual | Evidência/limite |
| --- | --- | --- |
| Precisão numérica (§3.2) | `BigDecimal` e PostgreSQL `NUMERIC`; unidades `un` inteiras, `kg` com até três casas decimais e preço com duas. | Testes de precisão na validação da Opção 01. |
| Exportação (tela 8) | CSV e PDF são formatos independentes, disponíveis quando `enterprise` está habilitado. | Strategy de relatório e testes de conteúdo CSV/PDF. |
| Permissão para equipe (tela 9) | Segue a matriz de permissões: gerente pode gerenciar usuários não administradores; ações administrativas elevadas são protegidas. | `UsuarioService`, `PermissaoService` e testes integrados. |
| Vínculos e estoque (RF003/RF004/RNF006) | Vínculo só pode ser removido ou alterado vazio; alterações de saldo são movimentações transacionais auditadas. | `VinculoService`, `EstoqueService` e testes PostgreSQL. |
| Estorno (RF014) | Estorno gera movimento inverso, não altera o registro original e impede repetição; entrada só é estornada se houver saldo disponível. | `EstoqueService` e testes integrados. |
| Exclusão e histórico (RF001/RF002/RF005/RF016) | Produtos, posições e usuários são desativados logicamente; bloqueios por saldo ou referências preservam integridade e histórico. | CRUDs e testes PostgreSQL. |
| Grade e saldo inicial (RF013) | Cadastro de grade aceita `tamanho=quantidade` por linha e uma posição comum; vínculos e entradas iniciais são gravados na mesma transação. | Validado na suíte integrada da Opção 01; não está pendente de compilação ou aceite PostgreSQL. |
| Resposta por voz (RF011) | Síntese de voz no navegador tem ação explícita, opção de interrupção e alternativa textual. | Testes JavaScript passaram; isso não comprova a integração Alexa externa. |

## Limites do escopo completo

| Referência | O que ainda precisa de definição ou validação | Situação atual |
| --- | --- | --- |
| RNF001 | Carga de referência e medição de latência ponta a ponta, separando resposta da aplicação, publicação MQTT e confirmação física. | Não medida. ACK MQTT não comprova acionamento do LED. |
| RNF005 | Janela e calendário para demonstrar disponibilidade de 99%. | Healthcheck e reinício estão configurados; não há observação operacional prolongada. |
| RNF007 / §3.3 | Medição de memória e comportamento sob carga para confirmar o limite total de recursos. | Compose define limites; consumo real ainda não foi medido. |
| Tela 11 | Dimensões físicas se a ocupação precisar representar volume geométrico. | Capacidade atual é por posição e unidade (`un` ou `kg`); percentual usa saldo sobre capacidade. |
| RF008 | Operadores, combinação de filtros e paginação para volumes além do limite atual. | Busca implementada por termos e características; consulta é paginada. Requisitos adicionais precisam ser especificados. |
| Histórico | Retenção, fuso e paginação para volumes superiores aos limites atuais. | Datas são tratadas em UTC e as consultas/exportações têm limites; política de retenção não foi definida. |
| RF009 / §3.3 | ID da skill, vínculo de contas, credenciais e endpoint HTTPS para Alexa. | Integração externa não configurada; busca por voz no navegador é funcional separadamente. |
| RNF010 / tela 10 | Contrato final de payload/ACK, TLS, gestão de credenciais e homologação de firmware. | MQTT e simulador foram verificados; hardware físico não foi usado. |
| Migração de dados legados | Compatibilidade de hashes antigos e migração do esquema Flask para PostgreSQL Java. | O volume Java é separado; não apontar a aplicação nova diretamente ao banco legado sem plano de migração. |
| §1.3 | Completar mapas de empatia caso sejam exigidos como entregável acadêmico separado. | A especificação menciona seis personas e apresenta um mapa. |

As decisões e limites acima descrevem o produto além da atividade de reuso. A
rubrica da entrega está atendida conforme o parecer atual e as evidências
listadas em [OPCAO_01.md](OPCAO_01.md).
