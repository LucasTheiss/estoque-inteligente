const $ = (selector, root = document) => root.querySelector(selector);
const csrf = $('meta[name="csrf-token"]').content;
const status = (text) => { $('#live-status').textContent = text; };
for (const link of document.querySelectorAll('nav a')) {
  if (new URL(link.href).pathname === location.pathname) link.setAttribute('aria-current', 'page');
}
for (const form of document.forms) form.addEventListener('submit', (event) => {
  const question = event.submitter?.dataset.confirmButton || form.dataset.confirm;
  if (question && !confirm(question)) { event.preventDefault(); return; }
  if (form.dataset.submitted) { event.preventDefault(); return; }
  if (form.method.toLowerCase() === 'post') { form.dataset.submitted = 'true'; form.setAttribute('aria-busy', 'true'); }
});
window.addEventListener('pageshow', () => { for (const form of document.forms) { delete form.dataset.submitted; form.removeAttribute('aria-busy'); } });
async function jsonRequest(url, options = {}) {
  const response = await fetch(url, { ...options, headers: { 'X-CSRF-Token': csrf, ...options.headers } });
  if (!response.ok) {
    const html = new DOMParser().parseFromString(await response.text(), 'text/html');
    throw new Error($('.notice.error', html)?.textContent || 'Não foi possível concluir. Verifique a conexão e tente novamente.');
  }
  return response.json();
}
for (const button of document.querySelectorAll('[data-signal]')) button.addEventListener('click', async () => {
  button.disabled = true;
  try { const result = await jsonRequest(`/iot/sinalizar/${button.dataset.signal}`, { method: 'POST' }); status(result.status); if ($('#mqtt-result')) $('#mqtt-result').textContent = `${result.topic} · ${JSON.stringify(result.payload)}`; }
  catch (error) { status(error.message); } finally { button.disabled = false; }
});
if ($('[data-mqtt-status]')) jsonRequest('/iot/status').then(result => {
  for (const label of document.querySelectorAll('[data-mqtt-status]')) { label.textContent = result.connected ? 'MQTT conectado' : 'MQTT desconectado'; label.classList.toggle('connected', result.connected); }
}).catch(() => { $('[data-mqtt-status]').textContent = 'MQTT indisponível'; });
const pair = $('[data-pair]');
function syncPair() {
  const [product, position] = pair.value.split(':');
  $('[name=product_id]').value = product || ''; $('[name=position_id]').value = position || '';
  $('[name=quantity]').step = pair.selectedOptions[0]?.dataset.unit === 'kg' ? '0.001' : '1';
}
if (pair) { pair.addEventListener('change', syncPair); syncPair(); }
$('#read-scale')?.addEventListener('click', async () => {
  const device = pair.selectedOptions[0]?.dataset.device;
  if (!device || !pair.value) { status('Selecione um produto e uma posição com balança configurada.'); return; }
  try { const result = await jsonRequest(`/iot/balanca/${$('[name=product_id]').value}/${encodeURIComponent(device)}`); $('[name=quantity]').value = result.quantity; $('#scale-result').textContent = `Peso líquido: ${result.quantity} kg · Subtotal: R$ ${result.subtotal}`; }
  catch (error) { status(error.message); }
});
const grade = $('[data-grade]');
if (grade) {
  for (const radio of document.querySelectorAll('[name=structure]')) radio.addEventListener('change', () => { grade.hidden = $('[name=structure]:checked').value !== 'grade'; $('[name=sizes]').required = !grade.hidden; });
  $('[name=sizes]').addEventListener('change', (event) => {
    $('#grade-quantities').replaceChildren();
    for (const size of new Set(event.target.value.split(',').map(value => value.trim()).filter(value => /^\d{1,3}$/.test(value)).slice(0, 30))) {
      const label = document.createElement('label'); label.textContent = `Estoque tamanho ${size}`;
      const input = document.createElement('input'); Object.assign(input, { type: 'number', name: `quantity_${size}`, min: '0', step: '1', value: '0' }); label.append(input); $('#grade-quantities').append(label);
    }
  });
}
$('#sector-filter')?.addEventListener('change', (event) => { for (const sector of document.querySelectorAll('[data-sector]')) sector.hidden = !!event.target.value && event.target.value !== sector.dataset.sector; });
const voiceButton = $('#voice-start');
if (voiceButton) {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition) { voiceButton.disabled = true; $('#voice-status').textContent = 'Este navegador não oferece reconhecimento de voz. Use a consulta por texto.'; }
  else voiceButton.addEventListener('click', () => {
    const recognition = new SpeechRecognition(); recognition.lang = 'pt-BR'; recognition.interimResults = false;
    recognition.onstart = () => { voiceButton.disabled = true; $('#voice-status').textContent = 'Ouvindo… diga modelo, cor e tamanho.'; };
    recognition.onend = () => { voiceButton.disabled = false; };
    recognition.onerror = () => { $('#voice-status').textContent = 'Não foi possível usar o microfone. Verifique a permissão ou digite sua busca.'; };
    recognition.onresult = (event) => { $('#voice-query').value = event.results[0][0].transcript; $('#voice-form').requestSubmit(); };
    recognition.start();
  });
}
$('#speak-answer')?.addEventListener('click', () => {
  if (!window.speechSynthesis) { status('Retorno de áudio indisponível neste navegador. Leia a resposta na tela.'); return; }
  const speech = new SpeechSynthesisUtterance($('#voice-answer').textContent); speech.lang = 'pt-BR'; speechSynthesis.cancel(); speechSynthesis.speak(speech);
});
