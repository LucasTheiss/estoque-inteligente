import csv
import io
import re
from datetime import datetime
from flask import Blueprint, current_app, g, jsonify, request, Response
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4, landscape
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet
from xml.sax.saxutils import escape
from .db import get_db, audit
from .security import require
from .domain import BalancaAdapter, ValidationError
from .iot import adapter
from . import catalog
from .web import render, done, history_rows

bp=Blueprint('integrations',__name__)


@bp.route('/busca-assistida',methods=['GET','POST'])
@require('consultar','iot')
def assisted():
    query=request.values.get('q','').strip()
    query=re.sub(r'^(alexa[, ]+)?(onde (está|esta|estão|estao)|localizar|buscar)\s+(o |a |os |as )?', '',query,flags=re.I)
    items=catalog.products(get_db(),g.tenant['id'],query) if query else []
    rows=catalog.locations(get_db(),g.tenant['id'],items[0]['id']) if len(items)==1 else []
    speech=''
    if len(items)==1:
        speech=f"{items[0]['name']}: " + ('; '.join(f"posição {r['code']}, {r['quantity']:g} {r['unit']}" for r in rows) or 'sem posição cadastrada.')
    elif query:
        speech='Nenhum produto encontrado.' if not items else 'Encontrei vários produtos. Informe também tamanho, cor ou SKU.'
    return render('assisted.html',products=items,rows=rows,speech=speech,query=query)


@bp.post('/iot/sinalizar/<int:id>')
@require('consultar','iot')
def signal(id):
    position=get_db().execute('SELECT * FROM positions WHERE tenant_id=%s AND id=%s AND active',(g.tenant['id'],id)).fetchone()
    if not position: raise ValidationError('Posição não encontrada.')
    result=adapter(current_app,g.tenant).signal(position)
    audit(get_db(),g.tenant['id'],g.user['id'],'iot.sinalizar',position=id)
    get_db().commit()
    return jsonify(result)


@bp.get('/iot/status')
@require('consultar','iot')
def status():
    mqtt=adapter(current_app,g.tenant)
    return {'connected':mqtt.connected.is_set()}


@bp.get('/iot/balanca/<int:product_id>/<device>')
@require('saida','fractional')
def scale(product_id,device):
    if not g.tenant['iot']: raise ValidationError('IoT não habilitado.')
    item=catalog.product(get_db(),g.tenant['id'],product_id)
    if item['unit']!='kg': raise ValidationError('Produto não vendido por peso.')
    result=BalancaAdapter().converter(adapter(current_app,g.tenant).reading(device),item['price'])
    return {key:str(value) for key,value in result.items()}


@bp.route('/iot/configuracao',methods=['GET','POST'])
@require('iot','iot')
def config():
    if request.method=='POST':
        host=catalog.text(request.form,'mqtt_host',True,253)
        if not re.fullmatch(r'[a-zA-Z0-9.:-]+',host): raise ValidationError('Host MQTT inválido.')
        get_db().execute('UPDATE tenants SET mqtt_host=%s,mqtt_port=%s,mqtt_qos=%s,led_seconds=%s WHERE id=%s',
            (host,catalog.integer(request.form.get('mqtt_port'),1,65535),catalog.integer(request.form.get('mqtt_qos'),0,1),
             catalog.integer(request.form.get('led_seconds'),5,60),g.tenant['id']))
        audit(get_db(),g.tenant['id'],g.user['id'],'iot.configurar',host=host)
        return done('Configuração salva.','integrations.config')
    return render('iot.html')


@bp.get('/historico/exportar/<format>')
@require('relatorios','enterprise')
def export(format):
    rows=history_rows()
    headings=['ID','Data','Produto / SKU','Tipo','Posição','Quantidade','Responsável','Motivo']
    data=[[str(r['id']),r['created_at'].strftime('%d/%m/%Y %H:%M'),r['sku'],r['kind'],r['code'],str(r['delta']),r['username'],r['reason']] for r in rows]
    if format=='csv':
        output=io.StringIO(); writer=csv.writer(output,delimiter=';'); writer.writerow(headings)
        for row in data:
            writer.writerow(["'"+v if v.lstrip().startswith(('=','+','-','@')) else v for v in row])
        content='\ufeff'+output.getvalue(); mime='text/csv; charset=utf-8'
    elif format=='pdf':
        output=io.BytesIO(); styles=getSampleStyleSheet()
        cells=[[Paragraph(escape(v),styles['BodyText']) for v in row] for row in [headings]+data]
        table=Table(cells,repeatRows=1,colWidths=[30,78,110,55,52,62,95,275])
        table.setStyle(TableStyle([('BACKGROUND',(0,0),(-1,0),colors.HexColor('#e8e5ff')),
            ('VALIGN',(0,0),(-1,-1),'TOP'),('LINEBELOW',(0,0),(-1,-1),.3,colors.lightgrey),
            ('BOTTOMPADDING',(0,0),(-1,-1),8)]))
        SimpleDocTemplate(output,pagesize=landscape(A4),leftMargin=20,rightMargin=20).build([
            Paragraph(escape(g.tenant['name'])+' — Histórico',styles['Title']),Spacer(1,12),table])
        content=output.getvalue(); mime='application/pdf'
    else: raise ValidationError('Formato inválido.')
    audit(get_db(),g.tenant['id'],g.user['id'],'historico.exportar',format=format,rows=len(rows))
    get_db().commit()
    return Response(content,mimetype=mime,headers={'Content-Disposition':f'attachment; filename=historico.{format}'})
