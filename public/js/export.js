async function loadExportInfo() {
    try {
        const response = await fetch('/api/logs');
        const logs = await response.json();
        
        const info = document.getElementById('exportInfo');
        info.innerHTML = `
            <div style="display: grid; gap: 0.5rem;">
                <div>📊 Total Ancaman Tercatat: <strong>${logs.length}</strong></div>
                <div>🦠 Virtex: <strong>${logs.filter(l => l.reason === 'virtex' && !l.isFalsePositive).length}</strong></div>
                <div>🎣 Phishing: <strong>${logs.filter(l => l.reason === 'phishing' && !l.isFalsePositive).length}</strong></div>
                <div>📧 Spam: <strong>${logs.filter(l => l.reason === 'spam' && !l.isFalsePositive).length}</strong></div>
                <div>⚠️ False Positives: <strong>${logs.filter(l => l.isFalsePositive).length}</strong></div>
            </div>
        `;
    } catch (error) {
        console.error('Error loading export info:', error);
    }
}

function exportCSV() {
    window.location.href = '/api/export/csv';
}

window.exportCSV = exportCSV;

loadExportInfo();
