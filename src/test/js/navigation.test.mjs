import test from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import vm from 'node:vm';

const code = readFileSync(new URL('../../main/resources/static/assets/app.js', import.meta.url), 'utf8');
test('menu compacto pode abrir e volta aberto ao layout desktop', () => {
  const navigation = {open: true};
  const media = {matches: true, addEventListener(name, handler) { this[name] = handler; }};
  vm.runInNewContext(code, {
    document: {querySelector: selector => selector === '[data-navigation]' ? navigation : null,
      querySelectorAll: () => []},
    window: {matchMedia: () => media, addEventListener() {}}
  });
  assert.equal(navigation.open, false);
  navigation.open = true;
  media.matches = false;
  media.change();
  assert.equal(navigation.open, true);
  media.matches = true;
  media.change();
  assert.equal(navigation.open, false);
});
