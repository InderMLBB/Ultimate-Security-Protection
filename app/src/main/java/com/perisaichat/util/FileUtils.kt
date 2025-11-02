package com.perisaichat.util

import android.content.Context
import com.perisaichat.data.ThreatLog
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileUtils {
    
    fun calculateSHA256(text: String): String {
        val bytes = text.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    
    fun getEvidenceDirectory(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), "Evidence")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    fun exportLogsToCSV(context: Context, logs: List<ThreatLog>): File {
        val csvFile = File(getEvidenceDirectory(context), "logs.csv")
        
        csvFile.bufferedWriter().use { writer ->
            writer.write("ID,Timestamp,App Source,Sender ID,Message Snippet,Reason,Score,Hash,Is False Positive,Screenshot Path\n")
            
            logs.forEach { log ->
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                val timestampStr = dateFormat.format(Date(log.timestamp))
                
                val line = "${log.id}," +
                        "\"${timestampStr}\"," +
                        "\"${log.appSource}\"," +
                        "\"${log.senderId ?: "Unknown"}\"," +
                        "\"${escapeCsv(log.messageSnippet)}\"," +
                        "\"${log.reason}\"," +
                        "${log.score}," +
                        "\"${log.hash}\"," +
                        "${log.isFalsePositive}," +
                        "\"${log.screenshotPath ?: ""}\"\n"
                writer.write(line)
            }
        }
        
        return csvFile
    }
    
    fun createAppealTemplate(context: Context): File {
        val templateFile = File(getEvidenceDirectory(context), "readme_for_support.txt")
        
        val template = """
            Kepada Tim WhatsApp Support,
            
            Saya mengalami pemblokiran akun secara tiba-tiba.
            Melalui aplikasi PerisaiChat, saya memiliki bukti adanya pesan berbahaya/kenon yang dikirim ke akun saya.
            Terlampir file ZIP berisi bukti pesan dan tangkapan layar.
            
            Mohon tinjau kembali pemblokiran akun saya.
            Terima kasih atas perhatian dan bantuannya.
            
            Hormat saya,
            (Nama Pengguna)
            (Nomor WhatsApp)
            
            ---
            
            Evidence Details:
            - Export Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}
            - App Version: PerisaiChat v1.0.0
            - Total Threats Detected: [See logs.csv]
            
            Files Included:
            1. logs.csv - Complete threat log with metadata
            2. readme_for_support.txt - This file
            3. evidence/ - Screenshots (if available)
            
            Instructions:
            1. Please review the logs.csv file for complete details
            2. Each entry includes SHA256 hash for verification
            3. Screenshots provide visual proof of harmful messages
            4. This evidence proves the account received unsolicited harmful content
            
            Legal Disclaimer:
            This evidence is provided solely for account appeal purposes.
            All data was collected locally on the device using PerisaiChat defensive application.
            No offensive actions were taken by this account.
        """.trimIndent()
        
        templateFile.writeText(template)
        return templateFile
    }
    
    fun createEvidenceZip(context: Context, logs: List<ThreatLog>): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val zipFile = File(getEvidenceDirectory(context), "PerisaiChat_Evidence_$timestamp.zip")
        
        ZipOutputStream(FileOutputStream(zipFile)).use { zipOut ->
            val csvFile = exportLogsToCSV(context, logs)
            addFileToZip(zipOut, csvFile, csvFile.name)
            
            val templateFile = createAppealTemplate(context)
            addFileToZip(zipOut, templateFile, templateFile.name)
            
            logs.forEach { log ->
                log.screenshotPath?.let { path ->
                    val screenshotFile = File(path)
                    if (screenshotFile.exists()) {
                        addFileToZip(zipOut, screenshotFile, "evidence/${screenshotFile.name}")
                    }
                }
            }
        }
        
        return zipFile
    }
    
    private fun addFileToZip(zipOut: ZipOutputStream, file: File, entryName: String) {
        file.inputStream().use { input ->
            zipOut.putNextEntry(ZipEntry(entryName))
            input.copyTo(zipOut)
            zipOut.closeEntry()
        }
    }
    
    private fun escapeCsv(text: String): String {
        return text.replace("\"", "\"\"").take(250)
    }
    
    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        return dateFormat.format(Date(timestamp))
    }
}
