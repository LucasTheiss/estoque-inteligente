# Capturas reais do Estoque Inteligente

As 29 imagens JPEG desta pasta foram capturadas pelo Chromium/Playwright, autenticado na loja demonstrativa Calçados Aurora, com a aplicação local em execução em `http://localhost:8080`. São screenshots reais, sem montagem, redesenho ou substituição do conteúdo da página. Foram incorporadas nos 42 espaços de imagem das três planilhas, mantendo as âncoras e dimensões de apresentação do modelo Excel.

- `A01` a `A06`: telas reais dos grupos internos de dados; A05 usa o histórico de movimentos existentes.
- `X01` e `X02`: consultas reais aos provedores externos de fornecedores e ofertas. A tela de consulta não é uma tela de manutenção do provedor.
- `EE*-entrada`: formulários reais de inclusão; em EE21 a opção Entrada está selecionada.
- `EE*-detalhe`: continuação da tela, campos expandidos ou registros existentes. EE01 mostra grade e saldo inicial; EE21 mostra o histórico existente. Essas imagens não são confirmações de novas operações realizadas para obter os prints.
- `SE*`: pesquisa com produto selecionado, mapa e relatórios reais, com os valores calculados pelo sistema.
- `CE*-entrada`: filtros preenchidos antes de enviar a consulta; a tabela ainda corresponde à consulta anterior.
- `CE*-saida`: resultados reais após submeter os filtros.

Nenhum cadastro, movimentação ou alteração de permissão foi enviado para produzir as imagens. Os registros exibidos são dados de demonstração, não dados de produção. Foram usados apenas login, navegação, preenchimento de filtros, seleção da operação Entrada, expansão da grade e consultas.

[manifesto.json](manifesto.json) registra função, URL, momento, dimensões e SHA-256 de cada imagem. [incorporacao.json](incorporacao.json) relaciona cada imagem aos arquivos internos dos três Excel e registra a preservação das fórmulas. Os bytes das imagens incorporadas são idênticos aos JPEGs capturados pelo navegador.
