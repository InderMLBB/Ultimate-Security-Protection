async function loadStats() {
    try {
        const response = await fetch('/api/stats');
        const stats = await response.json();
        
        document.getElementById('totalThreats').textContent = stats.total;
        document.getElementById('virtexCount').textContent = stats.virtex;
        document.getElementById('phishingCount').textContent = stats.phishing;
        document.getElementById('spamCount').textContent = stats.spam;
        
        animateNumber('totalThreats', 0, stats.total, 1000);
        animateNumber('virtexCount', 0, stats.virtex, 1000);
        animateNumber('phishingCount', 0, stats.phishing, 1000);
        animateNumber('spamCount', 0, stats.spam, 1000);
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

async function loadRecentThreats() {
    try {
        const response = await fetch('/api/logs');
        const logs = await response.json();
        
        const recentLogs = logs.slice(0, 5);
        const container = document.getElementById('recentThreatsList');
        
        if (recentLogs.length === 0) {
            container.innerHTML = '<div class="card" style="text-align: center; padding: 2rem; color: var(--text-secondary);">Belum ada ancaman terdeteksi</div>';
            return;
        }
        
        container.innerHTML = recentLogs.map(log => `
            <div class="threat-item ${log.reason}" onclick="window.location.href='/database.html'">
                <div class="threat-header">
                    <span class="threat-badge ${log.reason}">${log.reason.toUpperCase()}</span>
                    <span class="threat-score">${log.score}</span>
                </div>
                <div class="threat-info">
                    ${new Date(log.timestamp).toLocaleString('id-ID', { timeZone: 'Asia/Jakarta' })} • ${log.appSource}
                </div>
                <div class="threat-message">
                    ${log.messageSnippet.substring(0, 100)}${log.messageSnippet.length > 100 ? '...' : ''}
                </div>
            </div>
        `).join('');
    } catch (error) {
        console.error('Error loading recent threats:', error);
    }
}

async function loadShieldStatus() {
    try {
        const response = await fetch('/api/settings/shield_enabled');
        const data = await response.json();
        
        const enabled = data.value === 'true';
        const toggle = document.getElementById('shieldToggle');
        const statusIcon = document.getElementById('statusIcon');
        const statusTitle = document.getElementById('statusTitle');
        const statusDesc = document.getElementById('statusDesc');
        
        toggle.checked = enabled;
        
        if (enabled) {
            statusIcon.classList.add('active');
            statusTitle.textContent = '🛡️ Perisai Aktif';
            statusDesc.textContent = 'Anda terlindungi dari ancaman virtex, phishing, dan spam';
        } else {
            statusIcon.classList.remove('active');
            statusTitle.textContent = '⏸️ Perisai Nonaktif';
            statusDesc.textContent = 'Aktifkan perisai untuk melindungi diri Anda';
        }
    } catch (error) {
        console.error('Error loading shield status:', error);
    }
}

async function toggleShield() {
    const toggle = document.getElementById('shieldToggle');
    const enabled = toggle.checked;
    
    try {
        await fetch('/api/settings/shield_enabled', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ value: enabled ? 'true' : 'false' })
        });
        
        loadShieldStatus();
    } catch (error) {
        console.error('Error toggling shield:', error);
        toggle.checked = !enabled;
    }
}

function animateNumber(id, start, end, duration) {
    const element = document.getElementById(id);
    const range = end - start;
    const increment = range / (duration / 16);
    let current = start;
    
    const timer = setInterval(() => {
        current += increment;
        if ((increment > 0 && current >= end) || (increment < 0 && current <= end)) {
            element.textContent = end;
            clearInterval(timer);
        } else {
            element.textContent = Math.round(current);
        }
    }, 16);
}

document.getElementById('shieldToggle').addEventListener('change', toggleShield);

loadStats();
loadRecentThreats();
loadShieldStatus();

setInterval(() => {
    loadStats();
    loadRecentThreats();
}, 5000);
