"""Adapter MQTT: mantém conexão e telemetria por loja no processo Flask."""
import json
import threading
import time
import paho.mqtt.client as mqtt
from .domain import ValidationError


class MqttAdapter:
    def __init__(self, tenant, config):
        self.tenant = tenant
        self.connected = threading.Event()
        self.scales, self.leds = {}, {}
        self.client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
        if config.get('MQTT_USER'):
            self.client.username_pw_set(config['MQTT_USER'], config.get('MQTT_PASSWORD'))
        self.client.on_connect = self.on_connect
        self.client.on_disconnect = lambda *args: self.connected.clear()
        self.client.on_message = self.on_message
        self.client.connect_async(tenant['mqtt_host'], tenant['mqtt_port'], 30)
        self.client.loop_start()

    @property
    def prefix(self):
        return f"lojas/{self.tenant['slug']}"

    def on_connect(self, client, userdata, flags, reason, properties):
        if reason == 0:
            self.connected.set()
            client.subscribe([(self.prefix+'/balancas/+/peso',1),(self.prefix+'/posicoes/+/estado',1)])

    def on_message(self, client, userdata, message):
        try:
            if len(message.payload)>2048 or message.retain:
                return
            data=json.loads(message.payload)
            if not isinstance(data,dict): return
            parts=message.topic.split('/')
            if len(parts)!=5 or '/'.join(parts[:2])!=self.prefix: return
            collection=self.scales if parts[2]=='balancas' else self.leds
            # Dispositivos expiram e o cache fica limitado; não acumula telemetria sem uso.
            if len(collection)>=1000:
                collection.clear()
            collection[parts[3]]=(time.monotonic(),data)
        except (ValueError,UnicodeError):
            return

    def signal(self, position):
        if not position['device'] or position['pin'] is None:
            raise ValidationError('Configure o controlador e o pino da posição antes de sinalizar.')
        if not self.connected.wait(1):
            raise ValidationError('Broker MQTT indisponível. Consulte a localização na tela.')
        topic=f"{self.prefix}/controladores/{position['device']}/led"
        payload={'action':'ON','timeout':self.tenant['led_seconds'],'pin':position['pin'],'position':position['id']}
        sent=self.client.publish(topic,json.dumps(payload),qos=self.tenant['mqtt_qos'],retain=False)
        try: sent.wait_for_publish(timeout=.7)
        except RuntimeError: raise ValidationError('Não foi possível enviar o comando MQTT.') from None
        if not sent.is_published(): raise ValidationError('O broker não confirmou o comando no prazo.')
        return {'topic':topic,'payload':payload,'status':'Comando entregue ao broker; aguardando dispositivo.'}

    def reading(self, device):
        reading=self.scales.get(device)
        if not self.connected.is_set() or not reading or time.monotonic()-reading[0]>5:
            raise ValidationError('Sem leitura recente da balança. Verifique o dispositivo.')
        return reading[1]

    def close(self):
        self.client.disconnect()
        self.client.loop_stop()


_lock=threading.Lock()


def adapter(app,tenant):
    registry=app.extensions.setdefault('mqtt',{})
    signature=(tenant['mqtt_host'],tenant['mqtt_port'],tenant['mqtt_qos'],tenant['led_seconds'])
    with _lock:
        current=registry.get(tenant['id'])
        if current and current[0]!=signature:
            current[1].close()
            current=None
        if not current:
            current=(signature,MqttAdapter(tenant,app.config))
            registry[tenant['id']]=current
        return current[1]
