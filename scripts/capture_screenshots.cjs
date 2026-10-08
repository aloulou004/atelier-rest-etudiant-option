// Capture the live responses in Chrome. Requires Playwright and Chrome.
const { chromium } = require('playwright');
const fs = require('fs');
const path = require('path');

const base = process.argv[2] || 'http://localhost:8080/Gestion_Options_Etudiants/rest';
const output = path.resolve(__dirname, '..', 'screenshots');
fs.mkdirSync(output, { recursive: true });
const escapeHtml = text => text.replace(/[&<>"']/g, char =>
  ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[char]);

(async () => {
  const browser = await chromium.launch({ channel: 'chrome', headless: true });
  try {
    const page = await browser.newPage({ viewport: { width: 1280, height: 800 }, deviceScaleFactor: 1 });
    for (const [route, filename, title] of [
      ['/options', 'options.png', 'Liste des options'],
      ['/etudiants', 'etudiants.png', 'Liste des étudiants'],
      ['/etudiants/option?codeOption=1', 'etudiants-option-xml.png', 'Étudiants de l’option 1'],
    ]) {
      const response = await page.goto(base + route, { waitUntil: 'networkidle' });
      if (!response || response.status() !== 200) throw new Error(`${route}: HTTP ${response && response.status()}`);
      const contentType = response.headers()['content-type'];
      const raw = await response.text();
      const formatted = contentType.includes('json') ? JSON.stringify(JSON.parse(raw), null, 2) :
        raw.replace(/></g, '>\n<');
      await page.goto('about:blank');
      await page.setContent(`<!doctype html><html lang="fr"><meta charset="utf-8"><title>${escapeHtml(title)}</title>
        <style>
          body { margin: 0; padding: 48px 64px; background: #f3f6fb; color: #17243a; font: 16px Arial, sans-serif; }
          main { max-width: 1080px; margin: auto; }
          h1 { font-size: 30px; margin: 0 0 8px; }
          p { color: #607086; margin: 0 0 24px; }
          .request, .response { background: white; border: 1px solid #d9e1eb; border-radius: 12px; margin: 18px 0; padding: 22px 26px; box-shadow: 0 3px 12px #17243a0a; }
          .label { color: #607086; font-size: 13px; font-weight: bold; letter-spacing: .08em; text-transform: uppercase; margin-bottom: 12px; }
          .method { display: inline-block; background: #dbeafe; color: #1751a4; border-radius: 5px; padding: 6px 10px; font-weight: bold; margin-right: 10px; }
          code, pre { font: 14px Consolas, 'Courier New', monospace; }
          .url { overflow-wrap: anywhere; }
          .status { color: #16713d; font-weight: bold; }
          pre { white-space: pre-wrap; line-height: 1.45; margin: 16px 0 0; }
        </style><main><h1>${escapeHtml(title)}</h1><p>Service REST de gestion des options et des étudiants</p>
        <section class="request"><div class="label">Requête</div><span class="method">GET</span><code class="url">${escapeHtml(base + route)}</code></section>
        <section class="response"><div class="label">Réponse réelle du serveur</div><span class="status">HTTP ${response.status()} OK</span>
        &nbsp; · &nbsp; <code>${escapeHtml(contentType)}</code><pre>${escapeHtml(formatted)}</pre></section></main></html>`);
      await page.screenshot({ path: path.join(output, filename), fullPage: true });
      console.log(`${filename}: ${base + route} (${response.status()})`);
    }
  } finally {
    await browser.close();
  }
})().catch(error => { console.error(error); process.exitCode = 1; });
