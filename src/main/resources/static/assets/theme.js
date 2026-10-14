'use strict';

(() => {
  const key = 'estoque.theme';
  const valid = value => ['light', 'dark', 'system'].includes(value);
  const system = window.matchMedia('(prefers-color-scheme: dark)');
  let preference = 'light';
  try {
    const saved = localStorage.getItem(key);
    if (valid(saved)) preference = saved;
  } catch { /* The theme still works when browser storage is unavailable. */ }
  const apply = () => {
    document.documentElement.dataset.theme = preference === 'system'
      ? (system.matches ? 'dark' : 'light') : preference;
  };
  apply();
  system.addEventListener('change', apply);
  document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-theme-picker]').forEach(picker => { picker.hidden = false; });
    const selects = document.querySelectorAll('[data-theme-select]');
    selects.forEach(select => {
      select.value = preference;
      select.addEventListener('change', () => {
        if (!valid(select.value)) return;
        preference = select.value;
        apply();
        selects.forEach(other => { other.value = preference; });
        try { localStorage.setItem(key, preference); } catch { /* Session-only theme. */ }
      });
    });
  });
})();
