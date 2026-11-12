"""Gera um DOCX simples em OOXML, sem dependências externas."""
from pathlib import Path
from xml.sax.saxutils import escape
from zipfile import ZipFile, ZIP_DEFLATED
import xml.etree.ElementTree as ET

sections = [
    ('1. Ferramenta utilizada',
     'Foi utilizado o jOOQ 3.19.26, na edição gratuita, com PostgreSQL 17, Java 21 e Spring Boot 3.5.6. O plugin oficial do Maven consulta o banco existente e gera classes Java e código de acesso aos dados. A demonstração fica na pasta DatabaseFirst, separada do sistema original.'),
    ('2. Implementação',
     'O banco exclusivo estoque_databasefirst recebe as três migrations Flyway originais do projeto. O gerador lê o esquema public e suas tabelas, colunas, tipos e chaves. São gerados Records, POJOs, DAOs e referências aos relacionamentos. A apresentação utiliza principalmente models, products, positions e tenants. Os arquivos ficam em DatabaseFirst/target/generated-sources/jooq. A geração é acionada pelo comando demo.ps1 generate, sem alterar o build normal do sistema principal.'),
    ('3. Demonstração do CRUD',
     'A tabela models foi escolhida por permitir um cadastro simples e exclusão física quando não existem produtos vinculados. A página local permite cadastrar, listar, editar e excluir modelos da loja exclusiva de teste. O serviço usa ModelsRecord, a referência MODELS e o POJO gerados pelo jOOQ. As operações são transacionais e registradas na auditoria. O teste também utiliza ModelsDao e confere os dados diretamente no PostgreSQL. A página Thymeleaf, o Controller e o Service foram escritos manualmente; não são telas geradas automaticamente. O acesso exige login e proteção CSRF.'),
    ('4. Regeneração das classes',
     'A demonstração adiciona a coluna demo_note à tabela models somente no banco de teste. Após executar novamente demo.ps1 generate, surgem DEMO_NOTE, getDemoNote() e setDemoNote(...) nos arquivos gerados. A compilação e os testes são repetidos com a nova coluna. O comando demo.ps1 restore remove a coluna e os registros da demonstração, regenera as classes e testa o estado inicial. O passo a passo completo está em DatabaseFirst/ROTEIRO_DEMONSTRACAO.md.'),
]
paragraphs = []
for title, text in sections:
    paragraphs.append('<w:p><w:pPr><w:spacing w:before="240" w:after="120"/></w:pPr><w:r><w:rPr><w:b/><w:sz w:val="26"/></w:rPr><w:t>' + escape(title) + '</w:t></w:r></w:p>')
    paragraphs.append('<w:p><w:pPr><w:spacing w:after="160" w:line="276" w:lineRule="auto"/></w:pPr><w:r><w:rPr><w:sz w:val="22"/></w:rPr><w:t>' + escape(text) + '</w:t></w:r></w:p>')
document = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>' + ''.join(paragraphs) + '<w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1134" w:right="1134" w:bottom="1134" w:left="1134"/></w:sectPr></w:body></w:document>'
output = Path(__file__).resolve().parents[1] / 'DatabaseFirst_ParteIII.docx'
with ZipFile(output, 'w', ZIP_DEFLATED) as archive:
    archive.writestr('[Content_Types].xml', '<?xml version="1.0"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/></Types>')
    archive.writestr('_rels/.rels', '<?xml version="1.0"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>')
    archive.writestr('word/document.xml', document)
with ZipFile(output) as archive:
    assert archive.testzip() is None
    for name in archive.namelist():
        ET.fromstring(archive.read(name))
    assert len(ET.fromstring(archive.read('word/document.xml')).findall('.//{http://schemas.openxmlformats.org/wordprocessingml/2006/main}p')) == 8
print(output)
