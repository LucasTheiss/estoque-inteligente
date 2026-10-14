# Estoque Inteligente
<!-- impeccable:product-schema 1 -->

## Platform
web

## Stack
Java, Spring MVC, Thymeleaf, PostgreSQL, MQTT, NGINX e Docker, conforme atualização
solicitada pelo usuário para a seção 3.3 do PDF.

## Users
Vendedores, estoquistas, gerentes, administradores locais e superadministrador global. Localização
rápida de itens no balcão e no depósito, em desktop e smartphone.

## Product Purpose
Consultar produto, saldo e posição; sinalizar a gaveta por LED; movimentar estoque
com rastreabilidade, controle de acesso e isolamento entre lojas.

## Capabilities and Constraints
RF001–RF016, RNF001–RNF011 e onze telas descritos no PDF são a referência de aceite.
Variações por loja: IoT, plano Enterprise, produto simples/calçados e saída por peso.
O módulo transacional é compartilhado por todas as variantes.
Arquitetura MVC com Singleton, Template Method e Strategy. Provisionamento Docker
de lojas de demonstração com usuários, permissões, produtos e posições configurados.

## Brand Commitments
Preservar as funções das páginas 11–16, substituindo inteiramente sua estética.
Em 13/10/2026, o usuário confirmou a substituição completa da direção visual anterior.
Redesenho de todas as telas; preservar funções, dados e limites de acesso.
Em 14/10/2026, o usuário selecionou Wholesale catalog: navegação cobalto,
superfícies claras e tabelas compactas. Implementação direta em código confirmada.
Animações fluidas discretas, respeitando a preferência de movimento reduzido.
Tema selecionável no login. Não usar dados fictícios como telemetria real.

## Evidence on Hand
`docs/especificacao.pdf`, fornecido pelo usuário. As limitações do README anterior
foram explicitamente revogadas. Pendências de definição ficam em documento dedicado.

## Validação do contexto — 13/10/2026

Inspeção do código confirma Spring MVC/Thymeleaf, PostgreSQL, MQTT, quatro perfis
por loja e superadministrador, isolamento por loja, produtos por unidade ou peso,
LEDs com confirmação, histórico e estorno. Referências: `Acesso`, `IoTService`,
`DashboardController`, `MapaController`, `pom.xml` e `docs/REQUISITOS.md`.
A seleção de tema no login foi implementada no redesenho: claro, escuro e sistema,
com preferência persistida quando o armazenamento do navegador estiver disponível. Simulação de IoT não comprova hardware real.
Em 14/10/2026, após a recuperação do Docker, a suíte completa executou com
PostgreSQL 17 e MQTT: 51 testes Java aprovados, sem falhas nem testes ignorados.
Os nove testes JavaScript também passaram. A integração usa dispositivos
simulados; LEDs e balança físicos ainda exigem validação no equipamento.
