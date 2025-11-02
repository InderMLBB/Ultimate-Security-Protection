import express from 'express';
import Database from 'better-sqlite3';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';
import crypto from 'crypto';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const app = express();
const PORT = 5000;

const db = new Database('perisaichat.db');

db.exec(`
  CREATE TABLE IF NOT EXISTS threat_logs (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp INTEGER NOT NULL,
    appSource TEXT NOT NULL,
    senderId TEXT,
    messageSnippet TEXT NOT NULL,
    reason TEXT NOT NULL,
    score INTEGER NOT NULL,
    hash TEXT NOT NULL,
    isFalsePositive INTEGER DEFAULT 0,
    screenshotPath TEXT
  )
`);

db.exec(`
  CREATE TABLE IF NOT EXISTS settings (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
  )
`);

const getOrCreateSetting = db.prepare(`
  INSERT OR IGNORE INTO settings (key, value) VALUES (?, ?)
`);
getOrCreateSetting.run('shield_enabled', 'true');

app.use(express.json());
app.use(express.static('public'));

app.get('/api/stats', (req, res) => {
  const total = db.prepare('SELECT COUNT(*) as count FROM threat_logs WHERE isFalsePositive = 0').get();
  const virtex = db.prepare('SELECT COUNT(*) as count FROM threat_logs WHERE reason = ? AND isFalsePositive = 0').get('virtex');
  const phishing = db.prepare('SELECT COUNT(*) as count FROM threat_logs WHERE reason = ? AND isFalsePositive = 0').get('phishing');
  const spam = db.prepare('SELECT COUNT(*) as count FROM threat_logs WHERE reason = ? AND isFalsePositive = 0').get('spam');
  const falsePositives = db.prepare('SELECT COUNT(*) as count FROM threat_logs WHERE isFalsePositive = 1').get();
  
  res.json({
    total: total.count,
    virtex: virtex.count,
    phishing: phishing.count,
    spam: spam.count,
    falsePositives: falsePositives.count
  });
});

app.get('/api/logs', (req, res) => {
  const logs = db.prepare('SELECT * FROM threat_logs ORDER BY timestamp DESC').all();
  res.json(logs);
});

app.post('/api/logs', (req, res) => {
  const { appSource, senderId, message, reason, score } = req.body;
  
  const messageSnippet = message.substring(0, 250);
  const hash = crypto.createHash('sha256').update(messageSnippet).digest('hex');
  const timestamp = Date.now();
  
  const insert = db.prepare(`
    INSERT INTO threat_logs (timestamp, appSource, senderId, messageSnippet, reason, score, hash)
    VALUES (?, ?, ?, ?, ?, ?, ?)
  `);
  
  const result = insert.run(timestamp, appSource, senderId || 'Unknown', messageSnippet, reason, score, hash);
  
  res.json({ id: result.lastInsertRowid, success: true });
});

app.put('/api/logs/:id', (req, res) => {
  const { id } = req.params;
  const { isFalsePositive } = req.body;
  
  const update = db.prepare('UPDATE threat_logs SET isFalsePositive = ? WHERE id = ?');
  update.run(isFalsePositive ? 1 : 0, id);
  
  res.json({ success: true });
});

app.delete('/api/logs/:id', (req, res) => {
  const { id } = req.params;
  const del = db.prepare('DELETE FROM threat_logs WHERE id = ?');
  del.run(id);
  
  res.json({ success: true });
});

app.delete('/api/logs', (req, res) => {
  const del = db.prepare('DELETE FROM threat_logs');
  del.run();
  
  res.json({ success: true });
});

app.get('/api/settings/:key', (req, res) => {
  const { key } = req.params;
  const setting = db.prepare('SELECT value FROM settings WHERE key = ?').get(key);
  
  res.json({ value: setting ? setting.value : null });
});

app.put('/api/settings/:key', (req, res) => {
  const { key } = req.params;
  const { value } = req.body;
  
  const update = db.prepare('INSERT OR REPLACE INTO settings (key, value) VALUES (?, ?)');
  update.run(key, value);
  
  res.json({ success: true });
});

app.get('/api/export/csv', (req, res) => {
  const logs = db.prepare('SELECT * FROM threat_logs ORDER BY timestamp DESC').all();
  
  let csv = 'ID,Timestamp,App Source,Sender ID,Message Snippet,Reason,Score,Hash,Is False Positive\n';
  
  logs.forEach(log => {
    const date = new Date(log.timestamp).toLocaleString('id-ID', { timeZone: 'Asia/Jakarta' });
    csv += `${log.id},"${date}","${log.appSource}","${log.senderId}","${log.messageSnippet.replace(/"/g, '""')}","${log.reason}",${log.score},"${log.hash}",${log.isFalsePositive}\n`;
  });
  
  res.setHeader('Content-Type', 'text/csv');
  res.setHeader('Content-Disposition', 'attachment; filename="perisaichat_logs.csv"');
  res.send(csv);
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`🛡️  PerisaiChat Web Demo running on http://0.0.0.0:${PORT}`);
  console.log(`📊 Dashboard: http://0.0.0.0:${PORT}`);
  console.log(`💾 Database: http://0.0.0.0:${PORT}/database.html`);
  console.log(`🧪 Test Detection: http://0.0.0.0:${PORT}/test.html`);
});
