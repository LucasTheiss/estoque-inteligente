import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';

const code = readFileSync(new URL('../../main/resources/static/assets/theme.js', import.meta.url), 'utf8');
function fixture(saved, dark = false, storageBlocked = false) {
  const root = {dataset: {}};
  const picker = {hidden: true};
  const select = {value: '', addEventListener(name, handler) { this[name] = handler; }};
  const media = {matches: dark, addEventListener(name, handler) { this[name] = handler; }};
  const storage = {getItem() { if (storageBlocked) throw Error('blocked'); return saved; },
    setItem(key, value) { if (storageBlocked) throw Error('blocked'); saved = value; }};
  const document = {documentElement: root,
    addEventListener(name, handler) { this[name] = handler; },
    querySelectorAll(selector) { return selector === '[data-theme-picker]' ? [picker] : [select]; }};
  vm.runInNewContext(code, {document, localStorage: storage, window: {matchMedia: () => media}});
  document.DOMContentLoaded();
  return {root, picker, select, media, saved: () => saved};
}

test('aplica a preferência antes da montagem dos controles e restaura a seleção', () => {
  const f = fixture('dark');
  assert.equal(f.root.dataset.theme, 'dark');
  assert.equal(f.select.value, 'dark');
  assert.equal(f.picker.hidden, false);
  f.select.value = 'light';
  f.select.change();
  assert.equal(f.root.dataset.theme, 'light');
  assert.equal(f.saved(), 'light');
});

test('tema do sistema acompanha mudanças sem sobrescrever escolha explícita', () => {
  const f = fixture('system');
  assert.equal(f.root.dataset.theme, 'light');
  f.media.matches = true;
  f.media.change();
  assert.equal(f.root.dataset.theme, 'dark');
  f.select.value = 'light';
  f.select.change();
  f.media.change();
  assert.equal(f.root.dataset.theme, 'light');
});

test('storage bloqueado e preferência inválida mantêm o controle funcional', () => {
  for (const f of [fixture('invalid'), fixture('dark', false, true)]) {
    assert.equal(f.root.dataset.theme, 'light');
    f.select.value = 'dark';
    assert.doesNotThrow(() => f.select.change());
    assert.equal(f.root.dataset.theme, 'dark');
  }
});
