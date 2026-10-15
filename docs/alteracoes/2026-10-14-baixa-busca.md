# Baixa rápida e busca assistida — 14/10/2026

## Correções

- Baixa rápida: `min=0.001` combinado com `step=1` deslocava os valores válidos
  para 1.001, 2.001 etc. O mínimo agora acompanha a unidade: 1 para `un` e 0.001
  para `kg`. Ajuste de saldo continua aceitando zero. Nenhum valor é arredondado
  automaticamente ao trocar o produto; a validação do servidor permanece ativa.
- O seletor compartilhado agora identifica `input[data-quantity]`, evitando
  confundi-lo com opções de posição que também informam `data-quantity`.
  Isso corrige também a seleção da quantidade na tela Movimentar e na balança.
- Voz: a transcrição perde espaços e pontuação final da frase antes de preencher
  o campo. Acentos, hífens de SKU e pontos internos de números são preservados.
- Busca: normalização dos termos e dos acentos latinos no SQL permite pesquisar
  `tenis horizon.` e encontrar `Tênis Horizon`. Os termos continuam parciais,
  independentes da ordem e combinados por AND. Códigos e decimais internos são
  preservados, assim como o escape literal de `%`/`_` e o isolamento por loja.
  A correção vale para busca assistida e demais consumidores de `pesquisar()`.

## Arquivos

- `src/main/resources/static/assets/app.js`: mínimo, passo, teclado, seletor de
  quantidade e limpeza da transcrição.
- `src/main/resources/templates/rapida.html`: valores iniciais coerentes para
  estoque por unidade.
- `src/main/java/br/com/estoque/repository/ProdutoRepository.java`: consulta
  tolerante a acentos e pontuação de frase, sem instalar extensão PostgreSQL.
- `src/test/js/movement-voice.test.mjs`: regressões de unidade, peso, ajuste,
  troca de produto e reconhecimento de voz.
- `src/test/java/br/com/estoque/AplicacaoTest.java`: busca com banco real e baixa
  por unidade/peso, incluindo rejeição de fração de unidade sem alterar saldo.

## Validação

70 testes Java aprovados com PostgreSQL e MQTT, sem testes ignorados; 13 testes
JavaScript aprovados. Build Docker concluído e aplicação atualizada.

No Edge headless, através do NGINX em localhost:8080:

- `un`: 1 e 2 aceitos; 0, 0.125 e 1.001 rejeitados pelo input.
- `kg`: 1, 2, 0.125 e 1.001 aceitos; zero rejeitado para saída.
- Ajuste: zero aceito em ambas as unidades.
- Busca `tenis horizon.`: retorna Tênis Horizon.

O reconhecimento foi testado com eventos simulados; não houve captura de fala
por microfone físico nesta verificação.
