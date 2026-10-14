import http from 'node:http';
import {readFile} from 'node:fs/promises';
import path from 'node:path';

const root = process.cwd();
const rendered = path.join(root, '.impeccable/review/rendered');
const assets = path.join(root, 'src/main/resources/static/assets');
http.createServer(async (req, res) => {
  if (req.method !== 'GET') {
    res.writeHead(405, {'Content-Type': 'text/plain; charset=utf-8'});
    return res.end('Prévia visual: operações não são executadas.');
  }
  const url = new URL(req.url, 'http://localhost');
  try {
    if (/^\/assets\/[a-z-]+\.(css|js)$/.test(url.pathname)) {
      res.setHeader('Content-Type', url.pathname.endsWith('.css') ? 'text/css' : 'text/javascript');
      return res.end(await readFile(path.join(assets, path.basename(url.pathname))));
    }
    if (url.pathname === '/__preview.css') {
      res.setHeader('Content-Type', 'text/css');
      return res.end('.preview-notice{padding:5px 18px;background:#fff4d8;color:#594313;font:12px/1.5 sans-serif;margin:0;max-width:none}');
    }
    const name = url.pathname === '/erro' ? 'erro' : (url.pathname + url.search).replace(/[^a-zA-Z0-9]/g, '_');
    let html;
    try { html = await readFile(path.join(rendered, name + '.html'), 'utf8'); }
    catch { html = await readFile(path.join(rendered, url.pathname.replace(/[^a-zA-Z0-9]/g, '_') + '.html'), 'utf8'); }
    html = html.replace('</head>', '<link rel="stylesheet" href="/__preview.css"></head>')
      .replace(/(<body[^>]*>)/, '$1<p class="preview-notice">Prévia visual · dados de teste · operações indisponíveis</p>');
    res.setHeader('Content-Type', 'text/html; charset=utf-8');
    res.setHeader('Content-Security-Policy', "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:");
    res.end(html);
  } catch {
    res.writeHead(404, {'Content-Type': 'text/plain; charset=utf-8'});
    res.end('Prévia não disponível para esta rota.');
  }
}).listen(8787, '127.0.0.1', () => console.log('UI preview: http://127.0.0.1:8787'));
