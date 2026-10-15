import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';

const code = readFileSync(new URL('../../main/resources/static/assets/app.js', import.meta.url), 'utf8');

function movement(unit = 'un') {
  const option = {value: '1', dataset: {unit}};
  const product = {value: '1', options: [option], selectedOptions: [option], addEventListener(name, handler) { this[name] = handler; }};
  const position = {options: [], replaceChildren(option) { this.options = [option]; }, add(option) { this.options.push(option); }};
  const quantity = {value: '1', min: '0.001', step: '0.001'};
  let kind = 'saida';
  const radio = {addEventListener(name, handler) { this[name] = handler; }};
  const form = {
    querySelector: selector => ({'[data-product]': product, '[data-position]': position, 'input[data-quantity]': quantity,
      '[name="kind"]:checked': {value: kind}}[selector] ?? null),
    querySelectorAll: () => [radio], addEventListener() {}
  };
  vm.runInNewContext(code, {
    document: {querySelector: () => null, querySelectorAll: selector => selector === '[data-movement]' ? [form] : []},
    window: {addEventListener() {}}, location: {search: ''}, URLSearchParams,
    Option: function(text, value) { this.text = text; this.value = value; }
  });
  return {quantity, changeProduct(unit) { option.dataset.unit = unit; product.change(); },
    changeKind(value) { kind = value; radio.change(); }};
}

test('baixa por unidade aceita inteiros com mínimo alinhado ao passo', () => {
  const {quantity} = movement();
  assert.equal(quantity.min, '1');
  assert.equal(quantity.step, '1');
  assert.equal(quantity.inputMode, 'numeric');
});

test('quilogramas aceitam milésimos e trocar para unidade não arredonda o valor informado', () => {
  const form = movement('kg');
  assert.equal(form.quantity.min, '0.001');
  assert.equal(form.quantity.step, '0.001');
  assert.equal(form.quantity.inputMode, 'decimal');
  form.quantity.value = '0.125';
  form.changeProduct('un');
  assert.equal(form.quantity.value, '0.125');
  assert.equal(form.quantity.min, '1');
  assert.equal(form.quantity.step, '1');
});

test('ajuste permite zerar; entrada e saída exigem quantidade positiva na unidade escolhida', () => {
  const form = movement();
  form.changeKind('ajuste');
  assert.equal(form.quantity.min, '0');
  form.changeProduct('kg');
  assert.equal(form.quantity.min, '0');
  assert.equal(form.quantity.step, '0.001');
  form.changeKind('entrada');
  assert.equal(form.quantity.min, '0.001');
  form.changeProduct('un');
  form.changeKind('saida');
  assert.equal(form.quantity.min, '1');
});

test('reconhecimento remove pontuação final preservando acentos, SKU e ponto decimal interno', () => {
  let recognition;
  const voice = {addEventListener(name, handler) { this[name] = handler; }};
  const status = {}, query = {};
  function Recognition() { recognition = this; this.start = () => {}; }
  vm.runInNewContext(code, {
    document: {querySelector: selector => ({'[data-voice]': voice, '[data-voice-status]': status, '#voice-query': query}[selector] ?? null), querySelectorAll: () => []},
    window: {SpeechRecognition: Recognition, addEventListener() {}}
  });
  voice.click();
  for (const [spoken, expected] of [
    [' Tênis Horizon. ', 'Tênis Horizon'], ['Café, açúcar!', 'Café, açúcar'],
    ['SKU-38 12.50...', 'SKU-38 12.50'], ['Tênis azul?!', 'Tênis azul'], ['Café…', 'Café']
  ]) {
    recognition.onresult({results: [[{transcript: spoken}]]});
    assert.equal(query.value, expected);
  }
  recognition.onend();
  assert.equal(voice.disabled, false);
});
