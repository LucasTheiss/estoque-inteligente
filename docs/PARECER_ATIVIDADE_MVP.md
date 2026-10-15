# Parecer da atividade — estado atual

Conferido em 15/10/2026 para a equipe de três integrantes e a rubrica enviada.

## Veredito

A implementação da **Opção 01 atende aos requisitos de código descritos na
atividade**: há dois Singletons, três aplicações independentes de Template
Method, três aplicações de Strategy, seis CRUDs ligados ao banco, dez tabelas
de domínio e variabilidade configurável por loja. O histórico abaixo foi
substituído pelo estado atual; consultar [REQUISITOS.md](REQUISITOS.md) para a
rastreabilidade item a item e [OPCAO_01.md](OPCAO_01.md) para os participantes
e roteiro de demonstração dos padrões.

## CRUDs para três integrantes

| CRUD | Rota/tela | Operações disponíveis |
| --- | --- | --- |
| Produtos | `/produtos` | Criar, consultar, editar e excluir logicamente |
| Modelos de produto | `/modelos` | Criar, consultar, editar e excluir, com proteção de referências |
| Posições | `/posicoes` | Criar, consultar, editar e excluir logicamente |
| Vínculos produto/posição | `/vinculos` | Criar, consultar, editar e remover vínculo vazio |
| Usuários | `/usuarios` | Criar, consultar, editar e desativar |
| Lojas | `/global` | Criar, consultar, editar e desativar |

Cada fluxo é implementado por controller, serviço e repositório e persiste no
PostgreSQL. A equipe pode demonstrar Produtos/Modelos, Posições/Vínculos e
Usuários/Lojas, um par por integrante. Inserir os nomes reais da equipe no
material de apresentação.

## Banco e variabilidade

A migração V1 cria dez tabelas de domínio (`tenants`, `users`, `role_permissions`,
`models`, `products`, `positions`, `occupancy`, `movements`, `audit` e
`login_attempts`); Flyway mantém o histórico de migração separadamente. A
variabilidade usa as flags `iot`, `enterprise` e `fractional` por loja. O
roteiro compara as lojas `essencial` e `granel` com o mesmo perfil para mostrar
recursos opcionais e o núcleo comum.

## Evidências e limites

O registro de 14/10/2026 contém resultado de 68 testes Java sem falhas ou
ignorados, nove testes JavaScript, build Docker, verificação de PostgreSQL e
MQTT e capturas de telas desktop/mobile. Veja
[evidências da Opção 01](../evidence/2026-10-14/opcao-01/README.md) e o
[histórico de validação](VALIDACAO.md). O teste MQTT usa dispositivos
simulados; não comprova hardware físico. Alexa externa, medição operacional de
carga/disponibilidade e publicação pública continuam fora do aceite desta
atividade.

Antes da apresentação, resta identificar nominalmente os três integrantes,
ensaiar dois CRUDs por pessoa e executar o roteiro de variabilidade. Não há
pendência de implementação da rubrica identificada nesta revisão.
