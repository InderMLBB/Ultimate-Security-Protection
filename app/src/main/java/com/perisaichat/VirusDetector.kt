package com.perisaichat

import java.util.regex.Pattern

data class AnalyzeResult(
    val score: Int,
    val isThreat: Boolean,
    val reason: String,
    val matchedPattern: String
)

object VirusDetector {
    
    private val suspiciousUrls = listOf(
        "bit.ly", "tinyurl", "freegift", "claim-now", "bonus-ff", 
        "get-prize", "win-prize", "free-money", "claim.now",
        "shopeepay", "dana-gratis", "ovo-gratis", "pulsa-gratis"
    )
    
    private val phishingKeywords = listOf(
        "menang", "hadiah", "gratis", "klaim", "bonus", "prize",
        "congratulation", "winner", "claim", "verify account",
        "suspended", "blocked", "urgent", "immediately"
    )
    
    private val invisibleChars = listOf(
        '\u200B', '\u200C', '\u200D', '\u200E', '\u200F',
        '\uFEFF', '\u2060', '\u2061', '\u2062', '\u2063'
    )
    
    fun analyze(text: String): AnalyzeResult {
        if (text.isEmpty()) {
            return AnalyzeResult(0, false, "Empty message", "")
        }
        
        var score = 0
        val reasons = mutableListOf<String>()
        var matchedPattern = ""
        
        val length = text.length
        val nonAsciiCount = text.count { it.code > 127 }
        val nonAsciiRatio = if (length > 0) (nonAsciiCount.toFloat() / length) * 100 else 0f
        
        val invisibleCount = text.count { it in invisibleChars }
        
        val emojiPattern = Pattern.compile("[\\p{So}\\p{Sc}\\p{Sk}\\p{Sm}]")
        val emojiMatcher = emojiPattern.matcher(text)
        var emojiCount = 0
        while (emojiMatcher.find()) emojiCount++
        
        if (length > 300) {
            score += 25
            reasons.add("Very long message (${length} chars)")
            matchedPattern = "LENGTH_EXCESSIVE"
        }
        
        if (nonAsciiRatio > 40) {
            score += 30
            reasons.add("High non-ASCII ratio (${String.format("%.1f", nonAsciiRatio)}%)")
            matchedPattern = "UNICODE_HEAVY"
        }
        
        if (invisibleCount > 5) {
            score += 35
            reasons.add("Contains invisible characters (${invisibleCount})")
            matchedPattern = "INVISIBLE_CHARS"
        }
        
        if (emojiCount > 20) {
            score += 20
            reasons.add("Excessive emojis/symbols (${emojiCount})")
            matchedPattern = "EMOJI_SPAM"
        }
        
        val repeatingPattern = "(.)\\1{10,}".toRegex()
        if (repeatingPattern.containsMatchIn(text)) {
            score += 30
            reasons.add("Repeating character pattern detected")
            matchedPattern = "CHAR_REPETITION"
        }
        
        val urlPattern = Pattern.compile("(https?://[\\w.-]+|www\\.[\\w.-]+)", Pattern.CASE_INSENSITIVE)
        val urlMatcher = urlPattern.matcher(text)
        val urls = mutableListOf<String>()
        while (urlMatcher.find()) {
            urls.add(urlMatcher.group())
        }
        
        val hasSuspiciousUrl = urls.any { url ->
            suspiciousUrls.any { suspicious -> url.contains(suspicious, ignoreCase = true) }
        }
        
        if (hasSuspiciousUrl) {
            score += 40
            reasons.add("Suspicious URL detected")
            matchedPattern = "PHISHING_URL"
        }
        
        val lowerText = text.lowercase()
        val matchedKeywords = phishingKeywords.filter { keyword ->
            lowerText.contains(keyword.lowercase())
        }
        
        if (matchedKeywords.isNotEmpty() && urls.isNotEmpty()) {
            score += 25
            reasons.add("Phishing keywords with URL: ${matchedKeywords.take(3).joinToString(", ")}")
            if (matchedPattern.isEmpty()) matchedPattern = "PHISHING_COMBO"
        } else if (matchedKeywords.size >= 3) {
            score += 15
            reasons.add("Multiple phishing keywords detected")
            if (matchedPattern.isEmpty()) matchedPattern = "PHISHING_KEYWORDS"
        }
        
        val uniqueChars = text.toSet().size
        val uniqueRatio = if (length > 0) (uniqueChars.toFloat() / length) * 100 else 0f
        
        if (length > 100 && uniqueRatio < 10) {
            score += 25
            reasons.add("Low character diversity (${String.format("%.1f", uniqueRatio)}%)")
            if (matchedPattern.isEmpty()) matchedPattern = "LOW_DIVERSITY"
        }
        
        score = score.coerceIn(0, 100)
        
        val isThreat = score >= 50
        val reason = if (reasons.isEmpty()) "No threat detected" else reasons.joinToString("; ")
        
        return AnalyzeResult(
            score = score,
            isThreat = isThreat,
            reason = reason,
            matchedPattern = if (matchedPattern.isEmpty()) "CLEAN" else matchedPattern
        )
    }
    
    fun categorizeReason(analyzeResult: AnalyzeResult): String {
        return when {
            analyzeResult.matchedPattern.contains("PHISHING") -> "phishing"
            analyzeResult.matchedPattern.contains("LENGTH") ||
            analyzeResult.matchedPattern.contains("UNICODE") ||
            analyzeResult.matchedPattern.contains("INVISIBLE") ||
            analyzeResult.matchedPattern.contains("REPETITION") ||
            analyzeResult.matchedPattern.contains("DIVERSITY") -> "virtex"
            analyzeResult.matchedPattern.contains("EMOJI") -> "spam"
            else -> "suspicious"
        }
    }
}
