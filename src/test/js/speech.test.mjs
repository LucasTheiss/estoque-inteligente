import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';

const code = readFileSync(new URL('../../main/resources/static/assets/speech.js', import.meta.url), 'utf8');
function fixture(supported = true) {
  const buttons = ['Tênis areia, tamanho 38. Posição A1: 6 unidades.', 'Café. Posição B2: 1,5 quilogramas.']
    .map(text => ({dataset: {speak: text}, textContent: 'Ouvir localização', attributes: {},
      setAttribute(name, value) { this.attributes[name] = value; },
      addEventListener(name, handler) { this[name] = handler; }}));
  const status = {textContent: ''};
  const spoken = [];
  let cancellations = 0;
  const window = {addEventListener(name, handler) { this[name] = handler; }};
  if (supported) {
    window.SpeechSynthesisUtterance = class { constructor(text) { this.text = text; } };
    window.speechSynthesis = {speak(utterance) { spoken.push(utterance); }, cancel() { cancellations++; }};
  }
  vm.runInNewContext(code, {window, document: {
    querySelectorAll() { return buttons; }, querySelector() { return status; }
  }});
  return {buttons, status, spoken, window, cancellations: () => cancellations};
}
test('lê apenas após clique e permite interromper', () => {
  const f = fixture();
  assert.equal(f.spoken.length, 0);
  f.buttons[0].click();
  assert.equal(f.spoken[0].text, f.buttons[0].dataset.speak);
  assert.equal(f.spoken[0].lang, 'pt-BR');
  assert.equal(f.buttons[0].attributes['aria-pressed'], 'true');
  f.buttons[0].click();
  assert.equal(f.spoken.length, 1);
  assert.equal(f.cancellations(), 1);
  assert.equal(f.buttons[0].textContent, 'Ouvir localização');
  assert.equal(f.status.textContent, 'Leitura interrompida.');
});
test('trocar resultado cancela leitura anterior sem corromper a atual', () => {
  const f = fixture();
  f.buttons[0].click();
  f.buttons[1].click();
  f.spoken[0].onerror();
  assert.equal(f.buttons[1].textContent, 'Parar áudio');
  assert.equal(f.cancellations(), 1);
  f.spoken[1].onend();
  assert.equal(f.buttons[1].textContent, 'Ouvir localização');
  assert.equal(f.status.textContent, 'Leitura concluída.');
});
test('navegador sem síntese oferece alternativa textual', () => {
  const f = fixture(false);
  assert.ok(f.buttons.every(button => button.disabled));
  assert.match(f.status.textContent, /disponíveis em texto/);
});
test('falha de áudio restaura botão e informa recuperação', () => {
  const f = fixture();
  f.buttons[0].click();
  f.spoken[0].onerror();
  assert.equal(f.buttons[0].attributes['aria-pressed'], 'false');
  assert.match(f.status.textContent, /Consulte a posição/);
});
test('sair da página interrompe a leitura', () => {
  const f = fixture();
  f.buttons[0].click();
  f.window.pagehide();
  assert.equal(f.cancellations(), 1);
  assert.equal(f.buttons[0].textContent, 'Ouvir localização');
});
