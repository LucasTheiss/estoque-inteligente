'use strict';

const navigation = document.querySelector('[data-navigation]');
if (navigation) {
  const compact = window.matchMedia('(max-width: 900px)');
  const adaptNavigation = () => { navigation.open = !compact.matches; };
  adaptNavigation();
  compact.addEventListener('change', adaptNavigation);
}

const feedback = (message) => {
  const target = document.querySelector('#feedback');
  if (target) target.textContent = message;
};
async function request(url, method = 'GET') {
  const headers = {Accept: 'application/json'};
  if (method !== 'GET') {
    headers[document.querySelector('meta[name="csrf-header"]').content] =
      document.querySelector('meta[name="csrf-token"]').content;
  }
  const response = await fetch(url, {method, headers, credentials: 'same-origin', signal: AbortSignal.timeout(10000)});
  if (response.redirected || !response.headers.get('content-type')?.includes('json')) {
    throw new Error('Sua sessão terminou. Entre novamente para continuar.');
  }
  const data = await response.json();
  if (!response.ok) throw new Error(data.detail || 'Não foi possível concluir a solicitação.');
  return data;
}
document.querySelectorAll('[data-confirm]').forEach(form => {
  form.addEventListener('submit', event => {
    if (!window.confirm(form.dataset.confirm)) event.preventDefault();
  });
});
document.querySelector('[data-back]')?.addEventListener('click', () => history.length > 1 ? history.back() : location.assign('/'));
document.querySelectorAll('[data-signal]').forEach(button => {
  button.addEventListener('click', async () => {
    button.disabled = true;
    button.setAttribute('aria-busy', 'true');
    try {
      const id = button.dataset.signal;
      await request('/api/iot/sinalizar/' + id, 'POST');
      feedback('Comando enviado. Aguardando confirmação do dispositivo…');
      let confirmed = false;
      for (let attempt = 0; attempt < 8; attempt++) {
        await new Promise(resolve => setTimeout(resolve, 600));
        const ack = await request('/api/iot/confirmacao/' + id);
        if (ack.confirmed) {
          feedback(ack.simulated ? 'Demonstração: sinalização confirmada pelo simulador.' : 'Sinalização confirmada pelo dispositivo.');
          confirmed = true;
          break;
        }
      }
      if (!confirmed) feedback('Comando enviado, mas o dispositivo não confirmou. Confira a conexão.');
    } catch (error) {
      feedback(error.name === 'TimeoutError' ? 'O dispositivo demorou a responder. Tente novamente.' : error.message);
    } finally {
      button.disabled = false;
      button.removeAttribute('aria-busy');
    }
  });
});
document.querySelectorAll('[data-movement]').forEach(form => {
  const product = form.querySelector('[data-product]');
  const position = form.querySelector('[data-position]');
  const quantity = form.querySelector('[data-quantity]');
  const seen = new Set();
  Array.from(product.options).forEach(option => {
    if (option.value && seen.has(option.value)) option.remove();
    else seen.add(option.value);
  });
  const positions = Array.from(position.options).filter(option => option.value).map(option => option.cloneNode(true));
  const update = () => {
    position.replaceChildren(new Option('Selecione a posição', ''));
    positions.filter(option => option.dataset.product === product.value).forEach(option => position.add(option.cloneNode(true)));
    if (position.options.length === 2) position.selectedIndex = 1;
    const unit = product.selectedOptions[0]?.dataset.unit;
    quantity.step = unit === 'kg' ? '0.001' : '1';
  };
  product.addEventListener('change', update);
  const preselected = new URLSearchParams(location.search).get('productId');
  if (preselected && Array.from(product.options).some(option => option.value === preselected)) product.value = preselected;
  update();
  form.addEventListener('submit', () => {
    const submit = form.querySelector('button[type="submit"], button:not([type])');
    if (submit) { submit.disabled = true; submit.textContent = 'Registrando…'; }
  });
});
window.addEventListener('pageshow', () => {
  document.querySelectorAll('[data-movement] button').forEach(button => {
    if (button.textContent === 'Registrando…') { button.disabled = false; button.textContent = 'Registrar movimentação'; }
  });
});
document.querySelector('[data-scan-form]')?.addEventListener('submit', event => {
  event.preventDefault();
  const code = document.querySelector('[data-scan]').value.trim();
  const product = document.querySelector('[data-product]');
  const match = Array.from(product.options).find(option => option.value && (option.dataset.barcode === code || option.dataset.sku === code));
  const status = document.querySelector('[data-scan-message]');
  if (!match) { status.textContent = 'Código não encontrado. Confira o cadastro ou selecione o produto abaixo.'; return; }
  product.value = match.value;
  product.dispatchEvent(new Event('change'));
  status.textContent = 'Produto localizado: ' + match.textContent + '. Confira a posição e confirme a saída.';
  document.querySelector('[data-quantity]').focus();
});
document.querySelector('[data-scale]')?.addEventListener('click', async event => {
  const button = event.currentTarget;
  const output = document.querySelector('[data-scale-output]');
  const product = document.querySelector('[data-product]');
  if (!product.value || product.selectedOptions[0].dataset.unit !== 'kg') {
    output.textContent = 'Selecione um produto em quilogramas.';
    return;
  }
  const device = document.querySelector('[data-scale-device]').value.trim();
  if (!/^[a-zA-Z0-9_-]+$/.test(device)) { output.textContent = 'Informe um dispositivo válido.'; return; }
  button.disabled = true;
  try {
    const data = await request('/api/iot/balanca/' + product.value + '/' + encodeURIComponent(device));
    document.querySelector('[data-quantity]').value = data.quantity;
    output.textContent = (data.simulated ? 'Leitura simulada. ' : '') + 'Peso líquido: ' + data.quantity + ' kg. Subtotal: R$ ' + Number(data.subtotal).toLocaleString('pt-BR', {minimumFractionDigits: 2, maximumFractionDigits: 2}) + '. Confira o peso antes de registrar.';
  } catch (error) { output.textContent = error.message; }
  finally { button.disabled = false; }
});
document.querySelector('[data-iot-status]')?.addEventListener('click', async event => {
  const button = event.currentTarget;
  button.disabled = true;
  const output = document.querySelector('[data-iot-output]');
  try {
    const data = await request('/api/iot/status');
    output.textContent = data.connected ? 'Conectado ao servidor MQTT.' : 'Sem conexão. Confira o servidor e os dispositivos.';
  } catch (error) { output.textContent = error.message; }
  finally { button.disabled = false; }
});
const voice = document.querySelector('[data-voice]');
if (voice) {
  const Recognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  const status = document.querySelector('[data-voice-status]');
  if (!Recognition) {
    voice.disabled = true;
    status.textContent = 'Este navegador não oferece reconhecimento de voz. Use a busca por texto.';
  } else {
    voice.addEventListener('click', () => {
      const recognition = new Recognition();
      recognition.lang = 'pt-BR';
      recognition.interimResults = false;
      voice.disabled = true;
      status.textContent = 'Ouvindo… diga o nome, a cor ou o tamanho do produto.';
      recognition.onresult = event => {
        const text = event.results[0][0].transcript;
        document.querySelector('#voice-query').value = text.slice(0, 200);
        status.textContent = 'Reconhecido: ' + text + '. Confira o texto e clique em Buscar.';
      };
      recognition.onerror = () => { status.textContent = 'Não foi possível ouvir. Confira a permissão do microfone ou digite a busca.'; };
      recognition.onend = () => { voice.disabled = false; };
      try { recognition.start(); } catch { voice.disabled = false; status.textContent = 'Não foi possível iniciar o microfone. Use a busca por texto.'; }
    });
  }
}
