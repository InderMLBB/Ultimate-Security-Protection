import { VirusDetector } from './detector.js';

let currentResult = null;

const samples = {
    virtex: `🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥
⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡
💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥
😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈
AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB`,
    
    phishing: `🎉 SELAMAT! 🎉

Anda adalah PEMENANG UNDIAN BERHADIAH!

💰 Hadiah Utama: Rp 50.000.000
🎁 Bonus: iPhone 15 Pro Max

KLAIM SEKARANG sebelum expired!
Klik link: https://bit.ly/claim-hadiah-gratis

BURUAN! Hanya berlaku 24 jam!`,
    
    normal: `Halo, apa kabar?

Gimana kabarmu hari ini? Sudah makan siang belum?

Aku mau ngajak kamu ngobrol santai nanti sore. Kita bisa ketemu di kafe dekat kantor, sekitar jam 4 sore.

Oh iya, jangan lupa bawa dokumen yang kemarin ya.`
};

function updateCharCount() {
    const message = document.getElementById('testMessage').value;
    document.getElementById('charCount').textContent = message.length;
}

function loadSample(type) {
    document.getElementById('testMessage').value = samples[type];
    updateCharCount();
}

function analyzeMessage() {
    const message = document.getElementById('testMessage').value.trim();
    
    if (!message) {
        alert('Mohon masukkan pesan untuk dianalisis');
        return;
    }
    
    currentResult = VirusDetector.analyze(message);
    const category = VirusDetector.categorizeReason(currentResult);
    
    currentResult.category = category;
    
    displayResult(currentResult);
}

function displayResult(result) {
    document.getElementById('resultPlaceholder').style.display = 'none';
    document.getElementById('resultContent').style.display = 'block';
    
    const scoreNumber = document.getElementById('scoreNumber');
    const scoreCircle = document.getElementById('scoreCircle');
    const threatStatus = document.getElementById('threatStatus');
    const scoreCard = document.getElementById('threatScoreCard');
    const saveBtn = document.getElementById('saveBtn');
    
    animateScore(result.score);
    
    const circumference = 565.48;
    const offset = circumference - (result.score / 100) * circumference;
    
    let color;
    if (result.score < 30) {
        color = '#4CAF50';
        threatStatus.className = 'threat-status safe';
        threatStatus.innerHTML = '<span class="material-icons">check_circle</span><span>Aman - Tidak Berbahaya</span>';
    } else if (result.score < 50) {
        color = '#FF9800';
        threatStatus.className = 'threat-status';
        threatStatus.style.background = '#fff3e0';
        threatStatus.style.color = '#FF9800';
        threatStatus.innerHTML = '<span class="material-icons">warning</span><span>Mencurigakan</span>';
    } else {
        color = '#D32F2F';
        threatStatus.className = 'threat-status dangerous';
        threatStatus.innerHTML = '<span class="material-icons">dangerous</span><span>Berbahaya - Akan Diblokir!</span>';
    }
    
    scoreCircle.style.stroke = color;
    scoreCircle.style.strokeDashoffset = offset;
    scoreCircle.style.transition = 'stroke-dashoffset 1s ease';
    
    const details = document.getElementById('analysisDetails');
    details.innerHTML = `
        <div class="detail-item">
            <strong>Pattern Terdeteksi:</strong><br>
            ${result.matchedPattern}
        </div>
        <div class="detail-item">
            <strong>Kategori:</strong><br>
            <span class="threat-badge ${result.category}">${result.category.toUpperCase()}</span>
        </div>
        <div class="detail-item">
            <strong>Alasan:</strong><br>
            ${result.reason}
        </div>
        ${result.details.length > 0 ? `
            <div class="detail-item">
                <strong>Detail Temuan:</strong>
                <ul style="margin: 0.5rem 0 0 1.5rem;">
                    ${result.details.map(d => `<li>${d}</li>`).join('')}
                </ul>
            </div>
        ` : ''}
        <div class="detail-item">
            <strong>Panjang Pesan:</strong> ${document.getElementById('testMessage').value.length} karakter
        </div>
    `;
    
    if (result.isThreat) {
        saveBtn.style.display = 'inline-flex';
    } else {
        saveBtn.style.display = 'none';
    }
}

function animateScore(target) {
    const element = document.getElementById('scoreNumber');
    let current = 0;
    const duration = 1000;
    const increment = target / (duration / 16);
    
    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            element.textContent = target;
            clearInterval(timer);
        } else {
            element.textContent = Math.round(current);
        }
    }, 16);
}

async function saveThreat() {
    if (!currentResult) return;
    
    const message = document.getElementById('testMessage').value;
    const appSource = document.getElementById('appSource').value;
    const senderId = document.getElementById('senderId').value;
    
    try {
        const response = await fetch('/api/logs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                appSource,
                senderId,
                message,
                reason: currentResult.category,
                score: currentResult.score
            })
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('✅ Threat berhasil disimpan ke database!');
            document.getElementById('saveBtn').style.display = 'none';
        }
    } catch (error) {
        console.error('Error saving threat:', error);
        alert('❌ Error menyimpan threat');
    }
}

window.loadSample = loadSample;
window.analyzeMessage = analyzeMessage;
window.saveThreat = saveThreat;

document.getElementById('testMessage').addEventListener('input', updateCharCount);

updateCharCount();
