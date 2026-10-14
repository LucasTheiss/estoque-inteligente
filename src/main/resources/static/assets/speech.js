'use strict';

const speechButtons = document.querySelectorAll('[data-speak]');
const speechStatus = document.querySelector('[data-speech-status]');
const synthesis = window.speechSynthesis;
let activeSpeech = null;

function stopSpeech() {
  if (!activeSpeech) return;
  const {button, label} = activeSpeech;
  activeSpeech = null;
  button.textContent = label;
  button.setAttribute('aria-pressed', 'false');
  synthesis.cancel();
  if (speechStatus) speechStatus.textContent = 'Leitura interrompida.';
}
for (const button of speechButtons) {
  if (!synthesis || !window.SpeechSynthesisUtterance) {
    button.disabled = true;
    if (speechStatus) speechStatus.textContent = 'Áudio indisponível neste navegador. As posições continuam disponíveis em texto.';
    continue;
  }
  button.setAttribute('aria-pressed', 'false');
  button.addEventListener('click', () => {
    const sameButton = activeSpeech?.button === button;
    stopSpeech();
    if (sameButton) return;
    const utterance = new window.SpeechSynthesisUtterance(button.dataset.speak);
    utterance.lang = 'pt-BR';
    const speech = {button, label: button.textContent, utterance};
    activeSpeech = speech;
    button.textContent = 'Parar áudio';
    button.setAttribute('aria-pressed', 'true');
    const finish = (message) => {
      if (activeSpeech !== speech) return;
      activeSpeech = null;
      button.textContent = speech.label;
      button.setAttribute('aria-pressed', 'false');
      if (speechStatus) speechStatus.textContent = message;
    };
    utterance.onend = () => finish('Leitura concluída.');
    utterance.onerror = () => {
      if (activeSpeech !== speech) return;
      finish('Não foi possível reproduzir o áudio. Consulte a posição na tabela.');
    };
    try {
      if (speechStatus) speechStatus.textContent = 'Lendo a localização selecionada. Use Parar áudio para interromper.';
      synthesis.speak(utterance);
    } catch {
      utterance.onerror();
    }
  });
}
window.addEventListener('pagehide', stopSpeech);
