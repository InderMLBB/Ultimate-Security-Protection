package com.perisaichat

import org.junit.Test
import org.junit.Assert.*

class VirusDetectorTest {
    
    @Test
    fun testVirtexDetection_highScore() {
        val virtexText = "🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥🔥" +
                "⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡" +
                "💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥💥" +
                "😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈😈" +
                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
                "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB" +
                "CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC"
        
        val result = VirusDetector.analyze(virtexText)
        
        assertTrue("Virtex should be detected as threat", result.isThreat)
        assertTrue("Virtex score should be > 70", result.score > 70)
    }
    
    @Test
    fun testPhishingDetection_withUrl() {
        val phishingText = "Selamat! Anda menang hadiah 10 juta rupiah! " +
                "Klaim sekarang di https://bit.ly/claim-prize-now " +
                "Segera klik link sebelum expired!"
        
        val result = VirusDetector.analyze(phishingText)
        
        assertTrue("Phishing should be detected as threat", result.isThreat)
        assertTrue("Phishing score should be > 60", result.score > 60)
        assertTrue("Should match phishing pattern", 
            result.matchedPattern.contains("PHISHING"))
    }
    
    @Test
    fun testNormalMessage_lowScore() {
        val normalText = "Hai, apa kabar? Mau ketemu nanti sore? " +
                "Kita bisa ngobrol santai di kafe."
        
        val result = VirusDetector.analyze(normalText)
        
        assertFalse("Normal message should not be threat", result.isThreat)
        assertTrue("Normal message score should be < 40", result.score < 40)
    }
    
    @Test
    fun testLongMessage_withNormalContent() {
        val longNormalText = "Halo, saya mau menjelaskan sesuatu yang penting. " +
                "Kemarin kita sudah diskusi tentang project, dan saya pikir " +
                "kita perlu lebih banyak waktu untuk menyelesaikannya. " +
                "Apakah kamu setuju kalau kita extend deadline sampai minggu depan?"
        
        val result = VirusDetector.analyze(longNormalText)
        
        assertTrue("Long normal message score should be reasonable", result.score < 50)
    }
    
    @Test
    fun testInvisibleCharacters() {
        val invisibleText = "Hello\u200B\u200C\u200D\u200E\u200F\uFEFF\u2060World"
        
        val result = VirusDetector.analyze(invisibleText)
        
        assertTrue("Invisible chars should increase score", result.score > 0)
    }
    
    @Test
    fun testRepeatingPattern() {
        val repeatingText = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        
        val result = VirusDetector.analyze(repeatingText)
        
        assertTrue("Repeating pattern should be detected", result.score > 20)
    }
    
    @Test
    fun testEmptyMessage() {
        val emptyText = ""
        
        val result = VirusDetector.analyze(emptyText)
        
        assertFalse("Empty message should not be threat", result.isThreat)
        assertEquals("Empty message should have 0 score", 0, result.score)
    }
    
    @Test
    fun testCategorization_virtex() {
        val virtexText = "A".repeat(400)
        val result = VirusDetector.analyze(virtexText)
        val category = VirusDetector.categorizeReason(result)
        
        assertEquals("Should categorize as virtex", "virtex", category)
    }
    
    @Test
    fun testCategorization_phishing() {
        val phishingText = "Congratulations! Click here: https://bit.ly/free-money"
        val result = VirusDetector.analyze(phishingText)
        val category = VirusDetector.categorizeReason(result)
        
        assertEquals("Should categorize as phishing", "phishing", category)
    }
    
    @Test
    fun testMultiplePhishingKeywords() {
        val text = "URGENT! Your account has been suspended. " +
                "Click immediately to verify and claim your prize!"
        
        val result = VirusDetector.analyze(text)
        
        assertTrue("Multiple phishing keywords should increase score", result.score > 30)
    }
}
