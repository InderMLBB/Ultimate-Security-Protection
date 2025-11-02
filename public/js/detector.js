export const VirusDetector = {
  suspiciousUrls: [
    'bit.ly', 'tinyurl', 'freegift', 'claim-now', 'bonus-ff', 
    'get-prize', 'win-prize', 'free-money', 'claim.now',
    'shopeepay', 'dana-gratis', 'ovo-gratis', 'pulsa-gratis'
  ],
  
  phishingKeywords: [
    'menang', 'hadiah', 'gratis', 'klaim', 'bonus', 'prize',
    'congratulation', 'winner', 'claim', 'verify account',
    'suspended', 'blocked', 'urgent', 'immediately'
  ],
  
  invisibleChars: [
    '\u200B', '\u200C', '\u200D', '\u200E', '\u200F',
    '\uFEFF', '\u2060', '\u2061', '\u2062', '\u2063'
  ],
  
  analyze(text) {
    if (!text || text.length === 0) {
      return {
        score: 0,
        isThreat: false,
        reason: 'Empty message',
        matchedPattern: 'CLEAN',
        details: []
      };
    }
    
    let score = 0;
    const details = [];
    let matchedPattern = '';
    
    const length = text.length;
    const nonAsciiCount = [...text].filter(char => char.charCodeAt(0) > 127).length;
    const nonAsciiRatio = length > 0 ? (nonAsciiCount / length) * 100 : 0;
    
    const invisibleCount = [...text].filter(char => this.invisibleChars.includes(char)).length;
    
    const emojiRegex = /[\p{Emoji_Presentation}\p{Extended_Pictographic}]/gu;
    const emojiCount = (text.match(emojiRegex) || []).length;
    
    if (length > 300) {
      score += 25;
      details.push(`Pesan sangat panjang (${length} karakter)`);
      matchedPattern = 'LENGTH_EXCESSIVE';
    }
    
    if (nonAsciiRatio > 40) {
      score += 30;
      details.push(`Rasio non-ASCII tinggi (${nonAsciiRatio.toFixed(1)}%)`);
      matchedPattern = 'UNICODE_HEAVY';
    }
    
    if (invisibleCount > 5) {
      score += 35;
      details.push(`Mengandung ${invisibleCount} karakter tersembunyi`);
      matchedPattern = 'INVISIBLE_CHARS';
    }
    
    if (emojiCount > 20) {
      score += 20;
      details.push(`Terlalu banyak emoji/simbol (${emojiCount})`);
      matchedPattern = 'EMOJI_SPAM';
    }
    
    const repeatingPattern = /(.)\1{10,}/;
    if (repeatingPattern.test(text)) {
      score += 30;
      details.push('Pola karakter berulang terdeteksi');
      matchedPattern = 'CHAR_REPETITION';
    }
    
    const urlPattern = /(https?:\/\/[\w.-]+|www\.[\w.-]+)/gi;
    const urls = text.match(urlPattern) || [];
    
    const hasSuspiciousUrl = urls.some(url => 
      this.suspiciousUrls.some(suspicious => url.toLowerCase().includes(suspicious))
    );
    
    if (hasSuspiciousUrl) {
      score += 40;
      details.push('URL mencurigakan terdeteksi');
      matchedPattern = 'PHISHING_URL';
    }
    
    const lowerText = text.toLowerCase();
    const matchedKeywords = this.phishingKeywords.filter(keyword =>
      lowerText.includes(keyword.toLowerCase())
    );
    
    if (matchedKeywords.length > 0 && urls.length > 0) {
      score += 25;
      details.push(`Kata kunci phishing dengan URL: ${matchedKeywords.slice(0, 3).join(', ')}`);
      if (!matchedPattern) matchedPattern = 'PHISHING_COMBO';
    } else if (matchedKeywords.length >= 3) {
      score += 15;
      details.push('Beberapa kata kunci phishing terdeteksi');
      if (!matchedPattern) matchedPattern = 'PHISHING_KEYWORDS';
    }
    
    const uniqueChars = new Set([...text]).size;
    const uniqueRatio = length > 0 ? (uniqueChars / length) * 100 : 0;
    
    if (length > 100 && uniqueRatio < 10) {
      score += 25;
      details.push(`Keragaman karakter rendah (${uniqueRatio.toFixed(1)}%)`);
      if (!matchedPattern) matchedPattern = 'LOW_DIVERSITY';
    }
    
    score = Math.min(score, 100);
    
    const isThreat = score >= 50;
    const reason = details.length > 0 ? details.join('; ') : 'Tidak ada ancaman terdeteksi';
    
    return {
      score,
      isThreat,
      reason,
      matchedPattern: matchedPattern || 'CLEAN',
      details
    };
  },
  
  categorizeReason(analyzeResult) {
    const pattern = analyzeResult.matchedPattern;
    
    if (pattern.includes('PHISHING')) {
      return 'phishing';
    } else if (
      pattern.includes('LENGTH') ||
      pattern.includes('UNICODE') ||
      pattern.includes('INVISIBLE') ||
      pattern.includes('REPETITION') ||
      pattern.includes('DIVERSITY')
    ) {
      return 'virtex';
    } else if (pattern.includes('EMOJI')) {
      return 'spam';
    } else {
      return 'suspicious';
    }
  }
};
