# Estoque Inteligente
<!-- impeccable:product-schema 1 -->

## Platform
web

## Stack
Flask, Jinja2, PostgreSQL, MQTT, NGINX e Docker, conforme seção 3.3 do PDF.

## Users
Vendedores, estoquistas, gerentes e administradores de infraestrutura. Localização
rápida de itens no balcão e no depósito, em desktop e smartphone.

## Product Purpose
Consultar produto, saldo e posição; sinalizar a gaveta por LED; movimentar estoque
com rastreabilidade, controle de acesso e isolamento entre lojas.

## Capabilities and Constraints
RF001–RF016, RNF001–RNF011 e onze telas descritos no PDF são a referência de aceite.
Variações por loja: IoT, plano Enterprise, produto simples/calçados e saída por peso.
O módulo transacional é compartilhado por todas as variantes.

## Brand Commitments
Reproduzir a interface operacional em português das páginas 11–16: superfícies
azul-escuras, ação principal violeta, estados semânticos e navegação compacta.
Tema selecionável no login. Não usar dados fictícios como telemetria real.

## Evidence on Hand
`docs/especificacao.pdf`, fornecido pelo usuário. As limitações do README anterior
foram explicitamente revogadas. Pendências de definição ficam em documento dedicado.
