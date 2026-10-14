# Escopo do redesenho

Modo: Operate. Implementação direta em código, confirmada pelo usuário.
Substituição completa da direção visual confirmada em 13/10/2026.

## Cobertura

| Superfície | Rotas / templates | Contratos preservados |
| --- | --- | --- |
| Acesso e recuperação | login, erro, error | Loja, autenticação, CSRF, mensagens e retorno |
| Consulta | /, dashboard | Busca, paginação, seleção, saldo, posição e sinalização |
| Operações | /movimentar, /baixa-rapida, /estorno | Validação, vínculo, unidade, idempotência e justificativa |
| Espaço físico | /mapa, /ocupacao | Posições, capacidade, produtos e recursos por loja |
| Cadastros | /produtos, /posicoes, /vinculos | CRUD, grade, saldo inicial e exclusão protegida |
| Rastreabilidade | /historico | Filtros, datas UTC, CSV/PDF e auditoria |
| Equipe | /usuarios | Perfis, matriz de permissões e revogação |
| Assistência e IoT | /busca-assistida, /iot/configuracao | Voz progressiva, LED com ACK e conexão MQTT |
| Administração global | /global | Criação, seleção e edição de lojas e recursos |
| Sistema compartilhado | layout, CSS, JavaScript | Navegação, foco, estados, responsividade e movimento reduzido |

## Validação inicial

Os modelos, controladores, serviços e permissões confirmam o propósito registrado
em PRODUCT.md. A referência Cartographer ainda aponta para a implementação
Python anterior; a arquitetura real validada é Java MVC e Thymeleaf.

Os testes Java atuais executaram com sucesso via Maven local e JDK 23. Oito
testes dependentes de infraestrutura foram ignorados. Docker Desktop respondeu
que não consegue iniciar. Nenhum navegador estava disponível na ferramenta de
inspeção. As imagens existentes em .impeccable/mocks são comps da direção antiga,
não capturas da implementação atual; não servem como prova visual de execução.

## Decisões

A direção Wholesale catalog foi selecionada pelo usuário: navegação cobalto,
superfícies claras e tabelas compactas, com implementação direta em código.
A seleção de tema claro, escuro e sistema foi implementada no login e no sistema.
Serviços, banco, integrações e autorização foram preservados pelo redesenho.

## Conclusão — 14/10/2026

Revisão visual independente: `ship`, sem correções materiais. Vinte capturas
válidas cobrem desktop, celular, temas, navegação e erro. As capturas da revisão
usam fixtures identificadas; não representam operações reais.
Após a recuperação do Docker, 51 testes Java passaram, incluindo as oito
integrações PostgreSQL/MQTT antes ignoradas. Nove testes JavaScript passaram.
Hardware físico não foi validado. A seção de validação inicial acima registra
as limitações históricas da primeira inspeção, superadas onde indicado aqui.
