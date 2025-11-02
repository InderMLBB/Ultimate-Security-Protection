package com.perisaichat.ui

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.perisaichat.R

class SettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        findViewById<Button>(R.id.btnAntiBanTips).setOnClickListener {
            showAntiBanTips()
        }
        
        findViewById<Button>(R.id.btnCheckModdedApps).setOnClickListener {
            checkForModdedApps()
        }
        
        findViewById<Button>(R.id.btnAbout).setOnClickListener {
            showAboutDialog()
        }
        
        displaySecurityStatus()
    }
    
    private fun displaySecurityStatus() {
        val statusText = findViewById<TextView>(R.id.tvSecurityStatus)
        
        val prefs = getSharedPreferences("perisai_prefs", Context.MODE_PRIVATE)
        val isShieldEnabled = prefs.getBoolean("shield_enabled", true)
        
        val moddedApps = detectModdedApps()
        
        val status = buildString {
            appendLine("🔒 Status Keamanan")
            appendLine()
            appendLine("Shield Status: ${if (isShieldEnabled) "✅ Aktif" else "⚠️ Nonaktif"}")
            appendLine()
            
            if (moddedApps.isNotEmpty()) {
                appendLine("⚠️ PERINGATAN:")
                appendLine("Aplikasi modifikasi terdeteksi:")
                moddedApps.forEach { app ->
                    appendLine("  - $app")
                }
                appendLine()
                appendLine("Penggunaan aplikasi modifikasi dapat menyebabkan banned!")
            } else {
                appendLine("✅ Tidak ada aplikasi modifikasi terdeteksi")
            }
        }
        
        statusText.text = status
    }
    
    private fun detectModdedApps(): List<String> {
        val moddedAppPackages = listOf(
            "com.gbwhatsapp" to "GB WhatsApp",
            "com.whatsapp.w4b" to "WhatsApp Business (Mod)",
            "com.yowhatsapp" to "YoWhatsApp",
            "com.fmwhatsapp" to "FM WhatsApp",
            "com.ogwhatsapp" to "OG WhatsApp"
        )
        
        val detected = mutableListOf<String>()
        
        moddedAppPackages.forEach { (packageName, appName) ->
            if (isAppInstalled(packageName)) {
                detected.add(appName)
            }
        }
        
        return detected
    }
    
    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    private fun showAntiBanTips() {
        val tips = """
            🛡️ Tips Mencegah Banned WhatsApp
            
            1. JANGAN Gunakan Aplikasi Modifikasi
               - GB WhatsApp, YoWhatsApp, FM WhatsApp, dll
               - Gunakan WhatsApp resmi dari Play Store
            
            2. Aktifkan Two-Step Verification
               - WhatsApp > Settings > Account > Two-step verification
               - Tambahkan PIN 6 digit
            
            3. Hindari Spam dan Auto Broadcast
               - Jangan kirim pesan massal otomatis
               - Batasi jumlah pesan per hari
               - Jangan gunakan bot atau auto-reply
            
            4. Backup Data Secara Berkala
               - Settings > Chats > Chat backup
               - Gunakan Google Drive
            
            5. Gunakan SIM Card yang Terdaftar
               - Pastikan nomor HP terverifikasi dengan benar
               - Jangan gunakan nomor virtual/temporary
            
            6. Jangan Klik Link Mencurigakan
               - Hati-hati dengan pesan phishing
               - Verifikasi pengirim sebelum klik link
            
            7. Laporkan Pesan Berbahaya
               - Gunakan fitur "Report" di WhatsApp
               - Block sender yang mencurigakan
            
            8. Update WhatsApp Secara Teratur
               - Gunakan versi terbaru dari Play Store
               - Aktifkan auto-update
            
            ⚠️ JIKA TERKENA BAN:
            - Gunakan fitur Export Bukti di PerisaiChat
            - Lampirkan ZIP file saat mengajukan banding
            - Hubungi WhatsApp Support: support@whatsapp.com
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("Tips Anti-Ban WhatsApp")
            .setMessage(tips)
            .setPositiveButton("Mengerti", null)
            .show()
    }
    
    private fun checkForModdedApps() {
        val moddedApps = detectModdedApps()
        
        val message = if (moddedApps.isEmpty()) {
            "✅ Bagus! Tidak ada aplikasi modifikasi terdeteksi.\n\nAnda menggunakan aplikasi resmi yang aman."
        } else {
            "⚠️ PERINGATAN!\n\nAplikasi modifikasi terdeteksi:\n\n${moddedApps.joinToString("\n") { "• $it" }}\n\nPenggunaan aplikasi modifikasi dapat menyebabkan:\n• Banned permanen\n• Kehilangan data\n• Risiko keamanan\n\nSegera hapus dan gunakan aplikasi resmi!"
        }
        
        AlertDialog.Builder(this)
            .setTitle("Deteksi Aplikasi Modifikasi")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showAboutDialog() {
        val about = """
            🛡️ PerisaiChat v1.0.0
            
            Ultimate Anti-Virtex & Anti-Ban Shield
            
            PerisaiChat adalah aplikasi defensif yang melindungi Anda dari:
            • Virus teks (virtex/kenon)
            • Pesan phishing
            • Spam berbahaya
            
            Fitur Utama:
            ✓ Real-time notification monitoring
            ✓ AI heuristic threat detection
            ✓ Evidence collection & export
            ✓ Auto-generated appeal templates
            ✓ Anti-ban advisor
            
            DISCLAIMER:
            PerisaiChat adalah aplikasi defensif. Tidak menjamin 100% keamanan, namun memberikan perlindungan maksimal terhadap pesan berbahaya. Pengguna bertanggung jawab atas data dan tindakan sendiri.
            
            PRIVASI:
            • Semua data disimpan lokal
            • Tidak ada upload ke server
            • Tidak ada tracking
            • Open source
            
            LEGAL:
            Aplikasi ini HANYA untuk perlindungan diri. TIDAK BOLEH digunakan untuk:
            • Hacking atau pelanggaran privasi
            • Pembalasan atau serangan balik
            • Eksploitasi atau penyalahgunaan data
            
            © 2025 PerisaiChat
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("Tentang PerisaiChat")
            .setMessage(about)
            .setPositiveButton("OK", null)
            .show()
    }
}
