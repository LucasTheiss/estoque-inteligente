"""Aplica identidade visual aos quatro Excel sem alterar nenhum dado ou fÃ³rmula.

Gera novos arquivos em outputs/apf-formatados. Usa openpyxl para estilo e layout,
reposiciona exclusivamente imagens existentes e restaura os caches de fÃ³rmulas do
XLSX original no XML da cÃ³pia formatada.
"""
from __future__ import annotations
import copy, hashlib, posixpath, re, zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

import openpyxl
from openpyxl.drawing.spreadsheet_drawing import AnchorMarker, OneCellAnchor
from openpyxl.drawing.xdr import XDRPositiveSize2D
from openpyxl.styles import Alignment, Border, Font, PatternFill, Side
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.page import PageMargins
from openpyxl.worksheet.pagebreak import Break, RowBreak

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'outputs/apf-formatados'
SOURCES=[
 ROOT/'docs/entregas-individuais/catalogo_produtos.xlsx',
 ROOT/'docs/entregas-individuais/movimentacoes_usuarios.xlsx',
 ROOT/'docs/entregas-individuais/posicoes_ocupacao.xlsx',
 ROOT/'docs/apf/coletiva/entrega-coletiva-apf.xlsx',
]
NS={'x':'http://schemas.openxmlformats.org/spreadsheetml/2006/main',
    'r':'http://schemas.openxmlformats.org/officeDocument/2006/relationships',
    'p':'http://schemas.openxmlformats.org/package/2006/relationships'}
NAVY='17324D'; BLUE='285577'; TEAL='117C78'; GOLD='C28A2C'
INK='27384A'; MUTED='5C6F82'; WHITE='FFFFFF'; PAPER='F4F7FA'
PALE_BLUE='EAF1F7'; PALE_TEAL='E4F3F1'; PALE_GOLD='FFF4DF'; LINE='D6E0E8'
SIDE=Side(style='thin',color=LINE)
THIN_BOTTOM=Border(bottom=SIDE)
FILL_NAVY=PatternFill('solid',fgColor=NAVY)
FILL_BLUE=PatternFill('solid',fgColor=BLUE)
FILL_TEAL=PatternFill('solid',fgColor=PALE_TEAL)
FILL_GOLD=PatternFill('solid',fgColor=PALE_GOLD)
FILL_PALE=PatternFill('solid',fgColor=PALE_BLUE)

def package_sheet_paths(z):
    book=ET.fromstring(z.read('xl/workbook.xml'))
    relroot=ET.fromstring(z.read('xl/_rels/workbook.xml.rels'))
    targets={q.attrib['Id']:q.attrib['Target'] for q in relroot}
    result={}
    for sh in book.find('x:sheets',NS):
        rel=sh.attrib['{'+NS['r']+'}id'];target=targets[rel]
        result[sh.attrib['name']]=target.lstrip('/') if target.startswith('/') else posixpath.normpath(posixpath.join('xl',target))
    return result

def formula_snapshot(path):
    wb=openpyxl.load_workbook(path,data_only=False)
    result={}
    for ws in wb:
        cells={c.coordinate:c.value for row in ws for c in row if c.value is not None}
        formulas={coord:value for coord,value in cells.items() if isinstance(value,str) and value.startswith('=')}
        result[ws.title]=dict(cells=cells,formulas=formulas,merges={str(x) for x in ws.merged_cells.ranges},
          validations=[(dv.type,dv.operator,dv.formula1,dv.formula2,str(dv.sqref)) for dv in ws.data_validations.dataValidation],
          images=len(ws._images),conditional=len(ws.conditional_formatting))
    return result

def restore_formula_caches(original,destination):
    """Transplanta os <f>/<v> originais, pareados por aba e endereÃ§o de cÃ©lula."""
    tmp=destination.with_suffix('.cache-preserved.xlsx')
    with zipfile.ZipFile(original,'r') as zin,zipfile.ZipFile(destination,'r') as zout:
        oldpaths=package_sheet_paths(zin);newpaths=package_sheet_paths(zout)
        assert oldpaths.keys()==newpaths.keys()
        replacements={}
        for name,oldpath in oldpaths.items():
            oldroot=ET.fromstring(zin.read(oldpath));newroot=ET.fromstring(zout.read(newpaths[name]))
            oldcells={c.attrib['r']:c for c in oldroot.findall('.//x:c[x:f]',NS)}
            newcells={c.attrib['r']:c for c in newroot.findall('.//x:c[x:f]',NS)}
            assert oldcells.keys()==newcells.keys(),f'{name}: cÃ©lulas de fÃ³rmula alteradas'
            for ref,oldcell in oldcells.items():
                oldf=oldcell.find('x:f',NS);newf=newcells[ref].find('x:f',NS)
                assert oldf.text==newf.text,f'{name}!{ref}: fÃ³rmula alterada'
                newf.attrib.clear();newf.attrib.update(oldf.attrib)
                oldv=oldcell.find('x:v',NS);newcell=newcells[ref]
                if oldv is None:
                    if newcell.find('x:v',NS) is not None:newcell.remove(newcell.find('x:v',NS))
                else:
                    newv=newcell.find('x:v',NS)
                    if newv is None:newv=ET.SubElement(newcell,'{'+NS['x']+'}v')
                    newv.text=oldv.text
                if oldcell.get('t') is None:newcell.attrib.pop('t',None)
                else:newcell.set('t',oldcell.get('t'))
            replacements[newpaths[name]]=ET.tostring(newroot,encoding='utf-8',xml_declaration=True)
        with zipfile.ZipFile(tmp,'w',zipfile.ZIP_DEFLATED,compresslevel=6) as zout2:
            for item in zout.infolist():
                zout2.writestr(item,replacements.get(item.filename,zout.read(item.filename)))
    tmp.replace(destination)

def new_anchor(row,width_px,height_px,col=0):
    marker=AnchorMarker(col=col,colOff=0,row=row,rowOff=0)
    size=XDRPositiveSize2D(cx=round(width_px*9525),cy=round(height_px*9525))
    return OneCellAnchor(_from=marker,ext=size)

def fit_image(img,width_px=1400):
    ratio=width_px/img.width
    img.width=width_px;img.height=round(img.height*ratio)

def individual_image_layout(ws):
    """PÃ¡ginas A3 horizontais: ficha antes dos protÃ³tipos, sem ampliar cÃ©lulas."""
    imgs=list(ws._images)
    if not imgs:return
    if ws.title=='Entradas externas':
        start=max(1,ws.max_row+2);step=55
        ws.row_breaks=RowBreak()
        for i,img in enumerate(imgs):
            r=start+i*step
            img.anchor=new_anchor(r-1,1400,round(1400*img.height/img.width))
            fit_image(img,1400)
            for row in range(r,min(r+step,ws.max_row+step)):
                ws.row_dimensions[row].height=14.5
            ws.row_breaks.append(Break(id=r-2))
        last=start+len(imgs)*step-2
    else:
        start=ws.max_row+2;step=46
        ws.row_breaks=RowBreak()
        for i,img in enumerate(imgs):
            r=start+i*step
            img.anchor=new_anchor(r-1,1400,round(1400*img.height/img.width))
            fit_image(img,1400)
            if i==0:ws.row_breaks.append(Break(id=start-2))
            else:ws.row_breaks.append(Break(id=r-2))
            for row in range(r,min(r+step,ws.max_row+len(imgs)*step)):
                ws.row_dimensions[row].height=14.5
        last=start+len(imgs)*step-2
    ws.print_area=f"'"+ws.title.replace("'","''")+f"'!$A$1:$O${last}"
    ws.page_setup.orientation='landscape';ws.page_setup.paperSize=ws.PAPERSIZE_A3
    ws.page_setup.fitToWidth=1;ws.page_setup.fitToHeight=0
    ws.sheet_properties.pageSetUpPr.fitToPage=True

def is_table_header(values):
    words=('registro lÃ³gico','item de dado referenciado','arquivo / origem','caracterÃ­stica',
           'dado que cruza a fronteira','funÃ§Ã£o / grupo lÃ³gico','tipo e finalidade','arquivo / origem')
    joined=' | '.join(str(v).lower() for v in values if v is not None)
    return any(word in joined for word in words) and ('item' in joined or 'registro' in joined or 'funÃ§Ã£o' in joined or 'caracterÃ­stica' in joined or 'arquivo' in joined or 'tipo e finalidade' in joined)

def restyle_sheet(ws,collective=False):
    ws.sheet_view.showGridLines=False;ws.sheet_view.zoomScale=90
    ws.sheet_properties.tabColor=TEAL if collective else BLUE
    ws.sheet_properties.pageSetUpPr.fitToPage=True
    ws.page_margins=PageMargins(left=.28,right=.28,top=.38,bottom=.38,header=.16,footer=.16)
    ws.oddFooter.left.text='ESTOQUE INTELIGENTE  â€¢  APF'
    ws.oddFooter.left.size=8;ws.oddFooter.left.font='Arial,Regular'
    ws.oddFooter.right.text='&A   â€¢   &P / &N';ws.oddFooter.right.size=8
    # Limpa layouts exagerados sem alterar cÃ©lulas, dados ou fÃ³rmulas.
    if not collective:
        widths={'A':26,'B':44,'C':25,'D':47}
        for col,width in widths.items():ws.column_dimensions[col].width=width
        for col in range(5,17):ws.column_dimensions[get_column_letter(col)].width=9.71
        for row in range(1,ws.max_row+1):ws.row_dimensions[row].height=18
        ws.page_setup.orientation='landscape';ws.page_setup.paperSize=ws.PAPERSIZE_A3
        ws.page_setup.fitToWidth=1;ws.page_setup.fitToHeight=0
        ws.freeze_panes='A4' if ws.title=='ContribuiÃ§Ã£o' else ('A9' if ws.title.startswith(('A','X')) and 'ALI' in ws.title or ws.title.endswith('AIE') else None)
    else:
        for col,width in {'A':27,'B':45,'C':35,'D':49}.items():ws.column_dimensions[col].width=width
        for col,width in {'E':38,'F':23,'G':14,'H':24}.items():ws.column_dimensions[col].width=width
        ws.freeze_panes='A4' if ws.title=='CatÃ¡logo' else ('A11' if ws.title in ['Resumo'] else None)

    # Texto padrÃ£o, respiro e alinhamento mantÃªm as tabelas legÃ­veis.
    for row in ws.iter_rows():
        for cell in row:
            font=copy.copy(cell.font);font.name='Arial'
            if font.sz is None:font.sz=10.5
            if font.color is None or (font.color.type=='rgb' and font.color.rgb in ['00000000','FF000000']):font.color=INK
            cell.font=font
            cell.alignment=copy.copy(cell.alignment)
            cell.alignment=Alignment(horizontal=cell.alignment.horizontal or 'left',
              vertical='center',wrap_text=True,text_rotation=cell.alignment.text_rotation,
              shrink_to_fit=cell.alignment.shrink_to_fit,indent=cell.alignment.indent)
            if cell.value is not None:
                cell.border=Border(left=cell.border.left,right=cell.border.right,top=cell.border.top,bottom=SIDE,
                  diagonal=cell.border.diagonal,diagonal_direction=cell.border.diagonal_direction,
                  diagonalUp=cell.border.diagonalUp,diagonalDown=cell.border.diagonalDown,
                  outline=cell.border.outline,vertical=cell.border.vertical,horizontal=cell.border.horizontal,
                  start=cell.border.start,end=cell.border.end)

    # TÃ­tulos e rÃ³tulos de seÃ§Ã£o: mesma paleta e hierarquia nos trÃªs arquivos individuais.
    if ws.max_row and ws['A1'].value is not None:
        for c in range(1,min(max(ws.max_column,4),8)+1):
            cell=ws.cell(1,c);cell.fill=FILL_NAVY
            cell.font=Font(name='Arial',size=17,bold=True,color=WHITE)
            cell.alignment=Alignment(vertical='center',horizontal='left',wrap_text=True)
        ws.row_dimensions[1].height=34
    for r in range(2,ws.max_row+1):
        vals=[ws.cell(r,c).value for c in range(1,min(max(ws.max_column,4),8)+1)]
        text=' '.join(str(v) for v in vals if v is not None).strip()
        if not text:continue
        # Faixas de tÃ­tulo jÃ¡ mescladas no material, sem alterar nem criar mesclagens.
        if ws.cell(r,1).value is not None and isinstance(ws.cell(r,1).value,str) and ws.cell(r,1).value.startswith(('EE','ALI','AIE','CE','SE','X0','A0')) and any(ch.isdigit() for ch in ws.cell(r,1).value[:5]):
            if any(rng.min_row==r and rng.min_col==1 and rng.max_col>=4 for rng in ws.merged_cells.ranges):
                for c in range(1,min(ws.max_column,8)+1):
                    ws.cell(r,c).fill=FILL_BLUE;ws.cell(r,c).font=Font(name='Arial',size=14,bold=True,color=WHITE)
                ws.row_dimensions[r].height=max(ws.row_dimensions[r].height or 18,28)
        if is_table_header(vals):
            for c in range(1,min(ws.max_column,8)+1):
                q=ws.cell(r,c);q.fill=FILL_BLUE
                if q.value is not None:q.font=Font(name='Arial',size=10,bold=True,color=WHITE)
            ws.row_dimensions[r].height=max(ws.row_dimensions[r].height or 18,28)

    # SeÃ§Ãµes de contagem, PF e resultados ganham Ãªnfase coerente sem tocar em fÃ³rmulas.
    for row in ws.iter_rows():
        for cell in row:
            if not isinstance(cell.value,str):continue
            label=cell.value.strip().casefold()
            if label in {'complexidade','tamanho estimado (pf)','pf adicionais','total individual estimado','pfa (pf ajustados)','pfa individual','esforÃ§o com reserva (h)','prazo planejado (dias Ãºteis)','custo com reserva (r$)','esforÃ§o total','prazo arredondado','custo','pfa','pf ajustados','pfb','ni total','fator de ajuste (fa)','fa','pontos de funÃ§Ã£o ajustados'}:
                for c in range(cell.column,min(ws.max_column,cell.column+3)+1):
                    out=ws.cell(cell.row,c);out.fill=FILL_TEAL
                    if out.value is not None:
                        ft=copy.copy(out.font);ft.bold=True;ft.color=TEAL;out.font=ft
                ws.row_dimensions[cell.row].height=max(ws.row_dimensions[cell.row].height or 18,23)
    # Constantes editÃ¡veis de NI/premissas sÃ£o visualmente distintas das fÃ³rmulas.
    if ws.title=='NI e estimativas':
        note_range=range(9,23) if not collective else range(4,18)
        for r in note_range:
            q=ws.cell(r,2)
            if isinstance(q.value,(int,float)):
                q.fill=FILL_GOLD;q.font=Font(name='Arial',size=11,bold=True,color='8A5A12')
        for row in ws.iter_rows():
            for q in row:
                if q.data_type=='f':q.font=Font(name='Arial',size=10.5,color='245C87')
    # Colunas de fÃ³rmula calculada: cor azul; nÃºmeros sem mudanÃ§a de formato.
    for row in ws.iter_rows():
        for q in row:
            if q.data_type=='f' and q.fill.fill_type is None:
                q.font=Font(name='Arial',size=10.5,color='245C87')

def style_individual(ws):
    restyle_sheet(ws,False)
    if ws.title=='ContribuiÃ§Ã£o':
        ws.freeze_panes='A5';ws.print_area="'ContribuiÃ§Ã£o'!$A$1:$D$25"
        for r in [4,19]:
            for c in range(1,5):ws.cell(r,c).fill=FILL_BLUE;ws.cell(r,c).font=Font(name='Arial',size=10,bold=True,color=WHITE)
        for r in range(20,26):
            ws.row_dimensions[r].height=max(ws.row_dimensions[r].height or 18,28)
            ws.cell(r,1).fill=FILL_PALE;ws.cell(r,1).font=Font(name='Arial',size=10.5,bold=True,color=NAVY)
            ws.cell(r,2).fill=FILL_TEAL;ws.cell(r,2).font=Font(name='Arial',size=12,bold=True,color=TEAL)
            ws.cell(r,3).fill=FILL_PALE
        # Destaque visual Ã  contribuiÃ§Ã£o declarada, mantendo o texto original.
        for r in [20,21,22,23,24,25]:
            if ws.cell(r,3).value is not None:ws.cell(r,3).fill=FILL_GOLD
    if ws.title in ['A01 ALI','A02 ALI','X01 AIE','X02 AIE']:
        for r in [8,9,10,16]:
            if any(ws.cell(r,c).value is not None for c in range(1,5)) and is_table_header([ws.cell(r,c).value for c in range(1,5)]):
                for c in range(1,5):ws.cell(r,c).fill=FILL_BLUE;ws.cell(r,c).font=Font(name='Arial',size=10,bold=True,color=WHITE)
        for r in range(9,ws.max_row+1):
            if ws.cell(r,2).value is not None and isinstance(ws.cell(r,2).value,str):
                ws.row_dimensions[r].height=max(ws.row_dimensions[r].height or 18,23)
        if ws.title.endswith('AIE'):ws.freeze_panes='A10'
    if ws.title=='Entradas externas':
        for r in [9,50,91,116]:
            if r<=ws.max_row:
                for c in range(1,5):ws.cell(r,c).fill=FILL_BLUE;ws.cell(r,c).font=Font(name='Arial',size=10,bold=True,color=WHITE)
        for r in [1,42,84,109]:
            for c in range(1,5):ws.cell(r,c).fill=FILL_TEAL;ws.cell(r,c).font=Font(name='Arial',size=14,bold=True,color=WHITE)
        # Imagens existentes (ordem: duas EEs e suas telas de detalhe) depois dos dados,
        # cada uma em pÃ¡gina prÃ³pria, para evitar que se escondam atrÃ¡s das fichas.
        start=ws.max_row+2;step=55;ws.row_breaks=RowBreak()
        for i,img in enumerate(ws._images):
            r=start+i*step;fit_image(img,1400);img.anchor=new_anchor(r-1,1400,img.height)
            ws.row_breaks.append(Break(id=r-2))
            for rr in range(r,r+step):ws.row_dimensions[rr].height=14.5
        ws.print_area=f"'Entradas externas'!$A$1:$O${start+len(ws._images)*step-2}"
        ws.freeze_panes=None
    if ws.title in ['SE06 SE','SE02 SE','SE04 SE','SE07 SE','SE13 SE']:
        for r in range(1,ws.max_row+1):
            if ws.cell(r,1).value and str(ws.cell(r,1).value).startswith(('Entrada','Dados','SaÃ­da','CÃ¡lculo')):
                for c in range(1,5):
                    if ws.cell(r,c).value is not None:ws.cell(r,c).fill=FILL_PALE
    individual_image_layout(ws)

def style_group(ws):
    restyle_sheet(ws,True)
    if ws.title in ['Capa','Resumo','CatÃ¡logo','Matrizes','NI e estimativas','Exemplos SE','CritÃ©rios e fontes','ConferÃªncia']:
        for r in range(1,ws.max_row+1):
            vals=[ws.cell(r,c).value for c in range(1,min(ws.max_column,8)+1)]
            if is_table_header(vals):
                for c in range(1,ws.max_column+1):
                    ws.cell(r,c).fill=FILL_BLUE
                    if ws.cell(r,c).value is not None:ws.cell(r,c).font=Font(name='Arial',size=10,bold=True,color=WHITE)
            if ws.title=='CatÃ¡logo' and r>=4 and r%2==0:
                for c in range(1,ws.max_column+1):
                    if ws.cell(r,c).value is not None and ws.cell(r,c).fill.fill_type is None:ws.cell(r,c).fill=FILL_PALE
        if ws.title=='Resumo':
            for r in [10,13]:
                for c in range(1,5):
                    ws.cell(r,c).fill=FILL_TEAL
                    if ws.cell(r,c).value is not None:ws.cell(r,c).font=Font(name='Arial',size=12,bold=True,color=TEAL)
        if ws.title=='Exemplos SE':
            for r in [4]:
                for c in range(1,5):ws.cell(r,c).fill=FILL_BLUE;ws.cell(r,c).font=Font(name='Arial',size=10,bold=True,color=WHITE)
        if ws.title=='NI e estimativas':
            for r in range(27,35):
                if ws.cell(r,2).value is not None and not (isinstance(ws.cell(r,2).value,str) and ws.cell(r,2).value.startswith('=')):
                    ws.cell(r,2).fill=FILL_GOLD;ws.cell(r,2).font=Font(name='Arial',size=10.5,bold=True,color='8A5A12')
    else:
        # Fichas individuais da equipe: tabelas de TD/AR, complexidade e protÃ³tipos.
        if ws.max_row>=74:
            for r in [10,45]:
                if r<=ws.max_row:
                    for c in range(1,5):
                        if ws.cell(r,c).value is not None:ws.cell(r,c).fill=FILL_BLUE;ws.cell(r,c).font=Font(name='Arial',size=10,bold=True,color=WHITE)
            for r in [44,59,69]:
                if r<=ws.max_row:
                    for c in range(1,5):
                        if ws.cell(r,c).value is not None:ws.cell(r,c).fill=FILL_TEAL;ws.cell(r,c).font=Font(name='Arial',size=12,bold=True,color=WHITE if c==1 else TEAL)
            # Entradas NI do grupo sÃ£o as catorze notas numÃ©ricas de B4:B17.
            if ws.title in ['A01','A02','A03','A04','A05','A06','A07','A08','A09','X01','X02','CE04','CE05','CE06','CE07','CE08','CE09','CE11','CE12','EE01','EE02','EE03','EE04','EE05','EE06','EE07','EE08','EE09','EE10','EE11','EE12','EE13','EE14','EE15','EE16','EE17','EE18','EE19','EE20','EE21','EE22','EE23','EE24','EE25','EE26','EE27','EE28','SE02','SE04','SE06','SE07','SE08','SE09','SE10','SE13']:
                for row in ws.iter_rows():
                    for q in row:
                        if q.data_type=='f' and q.fill.fill_type is None:q.font=Font(name='Arial',size=10.5,color='245C87')
    # ImpressÃ£o das fichas da equipe em A3 retrato, com a imagem inteira.
    if ws.title not in ['Capa','Resumo','CatÃ¡logo','Matrizes','NI e estimativas','Exemplos SE','CritÃ©rios e fontes','ConferÃªncia']:
        ws.page_setup.orientation='portrait';ws.page_setup.paperSize=ws.PAPERSIZE_A3
        ws.page_setup.fitToWidth=1;ws.page_setup.fitToHeight=0
        ws.sheet_properties.pageSetUpPr.fitToPage=True

def main():
    OUT.mkdir(parents=True,exist_ok=True);report=[]
    for src in SOURCES:
        original=formula_snapshot(src)
        wb=openpyxl.load_workbook(src,data_only=False,keep_links=True)
        collective=src.name=='entrega-coletiva-apf.xlsx'
        for ws in wb:
            style_group(ws) if collective else style_individual(ws)
        output=OUT/(src.stem+'_formatado.xlsx')
        wb.save(output)
        restore_formula_caches(src,output)
        after=formula_snapshot(output)
        assert original==after,f'{src.name}: fÃ³rmulas, dados, tabelas ou imagens alterados'
        report.append(dict(origem=str(src.relative_to(ROOT)),arquivo=str(output.relative_to(ROOT)),
           abas=len(wb.sheetnames),celulasDados=sum(len(x['cells']) for x in original.values()),
           formulas=sum(len(x['formulas']) for x in original.values()),imagens=sum(x['images'] for x in original.values()),
           sha256=hashlib.sha256(output.read_bytes()).hexdigest()))
        print(src.name,'->',output.name,'formula/dados intactos')
    import json
    (OUT/'manifesto-formatacao.json').write_text(json.dumps(report,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
    print(json.dumps(report,ensure_ascii=False,indent=2))

if __name__=='__main__':main()

