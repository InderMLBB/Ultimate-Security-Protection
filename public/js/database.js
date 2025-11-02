let allLogs = [];
let filteredLogs = [];
let currentFilter = 'all';
let currentLog = null;

async function loadLogs() {
    try {
        const response = await fetch('/api/logs');
        allLogs = await response.json();
        
        applyFilters();
        updateStats();
    } catch (error) {
        console.error('Error loading logs:', error);
    }
}

function applyFilters() {
    let logs = [...allLogs];
    
    const searchQuery = document.getElementById('searchInput').value.toLowerCase();
    if (searchQuery) {
        logs = logs.filter(log => 
            log.messageSnippet.toLowerCase().includes(searchQuery) ||
            log.appSource.toLowerCase().includes(searchQuery) ||
            (log.senderId && log.senderId.toLowerCase().includes(searchQuery))
        );
    }
    
    if (currentFilter !== 'all') {
        if (currentFilter === 'false-positive') {
            logs = logs.filter(log => log.isFalsePositive === 1);
        } else {
            logs = logs.filter(log => log.reason === currentFilter && log.isFalsePositive === 0);
        }
    }
    
    filteredLogs = logs;
    renderTable();
}

function renderTable() {
    const container = document.getElementById('databaseTable');
    
    if (filteredLogs.length === 0) {
        container.innerHTML = `
            <div class="card" style="text-align: center; padding: 3rem; color: var(--text-secondary);">
                <span class="material-icons" style="font-size: 4rem; margin-bottom: 1rem;">inbox</span>
                <p style="font-size: 1.2rem;">Tidak ada data yang ditemukan</p>
            </div>
        `;
        return;
    }
    
    container.innerHTML = filteredLogs.map(log => `
        <div class="threat-item ${log.reason}" onclick="showDetail(${log.id})" style="margin-bottom: 1rem;">
            <div class="threat-header">
                <div>
                    <span class="threat-badge ${log.reason}">${log.reason.toUpperCase()}</span>
                    ${log.isFalsePositive ? '<span class="threat-badge" style="background: #fff3e0; color: #ff9800;">FALSE POSITIVE</span>' : ''}
                </div>
                <span class="threat-score">${log.score}</span>
            </div>
            <div class="threat-info">
                <span class="material-icons" style="font-size: 1rem;">access_time</span>
                ${new Date(log.timestamp).toLocaleString('id-ID', { timeZone: 'Asia/Jakarta' })}
                <span class="material-icons" style="font-size: 1rem; margin-left: 1rem;">apps</span>
                ${log.appSource}
                ${log.senderId ? `<span class="material-icons" style="font-size: 1rem; margin-left: 1rem;">person</span> ${log.senderId}` : ''}
            </div>
            <div class="threat-message">
                ${log.messageSnippet}
            </div>
            <div style="margin-top: 0.5rem; color: var(--text-secondary); font-size: 0.85rem;">
                <span class="material-icons" style="font-size: 0.9rem;">tag</span>
                Hash: ${log.hash.substring(0, 16)}...
            </div>
        </div>
    `).join('');
}

function updateStats() {
    document.getElementById('totalLogs').textContent = allLogs.length;
    document.getElementById('displayedLogs').textContent = filteredLogs.length;
}

function setFilter(filter) {
    currentFilter = filter;
    
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
    
    applyFilters();
}

function showDetail(id) {
    currentLog = allLogs.find(log => log.id === id);
    if (!currentLog) return;
    
    const modal = document.getElementById('detailModal');
    const body = document.getElementById('modalBody');
    
    body.innerHTML = `
        <div style="display: grid; gap: 1rem;">
            <div>
                <strong>ID:</strong> #${currentLog.id}
            </div>
            <div>
                <strong>Timestamp:</strong><br>
                ${new Date(currentLog.timestamp).toLocaleString('id-ID', { timeZone: 'Asia/Jakarta' })}
            </div>
            <div>
                <strong>App Source:</strong><br>
                ${currentLog.appSource}
            </div>
            <div>
                <strong>Sender:</strong><br>
                ${currentLog.senderId || 'Unknown'}
            </div>
            <div>
                <strong>Reason:</strong><br>
                <span class="threat-badge ${currentLog.reason}">${currentLog.reason.toUpperCase()}</span>
            </div>
            <div>
                <strong>Threat Score:</strong><br>
                <span style="font-size: 2rem; font-weight: 700; color: var(--danger);">${currentLog.score}/100</span>
            </div>
            <div>
                <strong>Message:</strong><br>
                <div style="padding: 1rem; background: var(--background); border-radius: 8px; margin-top: 0.5rem;">
                    ${currentLog.messageSnippet}
                </div>
            </div>
            <div>
                <strong>SHA256 Hash:</strong><br>
                <code style="font-size: 0.8rem; word-break: break-all; background: var(--background); padding: 0.5rem; display: block; border-radius: 4px; margin-top: 0.5rem;">
                    ${currentLog.hash}
                </code>
            </div>
            <div>
                <label style="display: flex; align-items: center; gap: 0.5rem; cursor: pointer;">
                    <input type="checkbox" id="falsePositiveCheck" ${currentLog.isFalsePositive ? 'checked' : ''} onchange="toggleFalsePositive()">
                    <span>Tandai sebagai False Positive</span>
                </label>
            </div>
        </div>
    `;
    
    modal.classList.add('active');
}

function closeModal() {
    document.getElementById('detailModal').classList.remove('active');
    currentLog = null;
}

async function toggleFalsePositive() {
    const checked = document.getElementById('falsePositiveCheck').checked;
    
    try {
        await fetch(`/api/logs/${currentLog.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ isFalsePositive: checked })
        });
        
        loadLogs();
    } catch (error) {
        console.error('Error updating false positive:', error);
    }
}

async function deleteCurrentThreat() {
    if (!currentLog) return;
    
    if (!confirm('Apakah Anda yakin ingin menghapus log ini?')) return;
    
    try {
        await fetch(`/api/logs/${currentLog.id}`, {
            method: 'DELETE'
        });
        
        closeModal();
        loadLogs();
    } catch (error) {
        console.error('Error deleting threat:', error);
    }
}

async function confirmDeleteAll() {
    if (!confirm('Apakah Anda yakin ingin menghapus SEMUA log? Tindakan ini tidak dapat dibatalkan!')) return;
    
    try {
        await fetch('/api/logs', {
            method: 'DELETE'
        });
        
        loadLogs();
    } catch (error) {
        console.error('Error deleting all logs:', error);
    }
}

window.showDetail = showDetail;
window.closeModal = closeModal;
window.toggleFalsePositive = toggleFalsePositive;
window.deleteCurrentThreat = deleteCurrentThreat;
window.confirmDeleteAll = confirmDeleteAll;

document.getElementById('searchInput').addEventListener('input', applyFilters);

document.querySelectorAll('.filter-btn').forEach(btn => {
    btn.addEventListener('click', () => setFilter(btn.dataset.filter));
});

window.addEventListener('click', (e) => {
    const modal = document.getElementById('detailModal');
    if (e.target === modal) {
        closeModal();
    }
});

loadLogs();
