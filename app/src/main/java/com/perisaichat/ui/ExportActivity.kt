package com.perisaichat.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.perisaichat.R
import com.perisaichat.util.FileUtils
import kotlinx.coroutines.launch
import java.io.File

class ExportActivity : AppCompatActivity() {
    
    private val viewModel: ExportViewModel by viewModels()
    private lateinit var statusText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export)
        
        statusText = findViewById(R.id.tvExportStatus)
        
        findViewById<Button>(R.id.btnExportCSV).setOnClickListener {
            exportCSV()
        }
        
        findViewById<Button>(R.id.btnExportZip).setOnClickListener {
            exportZip()
        }
        
        findViewById<Button>(R.id.btnCreateTemplate).setOnClickListener {
            createTemplate()
        }
        
        updateStatus()
    }
    
    private fun updateStatus() {
        lifecycleScope.launch {
            val logs = viewModel.getAllLogs()
            
            val evidenceDir = FileUtils.getEvidenceDirectory(this@ExportActivity)
            
            statusText.text = """
                📦 Export Bukti
                
                Total Ancaman Tercatat: ${logs.size}
                Siap untuk diekspor
                
                File akan disimpan di:
                ${evidenceDir.absolutePath}
            """.trimIndent()
        }
    }
    
    private fun exportCSV() {
        statusText.text = "Mengekspor CSV..."
        
        lifecycleScope.launch {
            try {
                val logs = viewModel.getAllLogs()
                
                if (logs.isEmpty()) {
                    Toast.makeText(this@ExportActivity, "Tidak ada log untuk diekspor", Toast.LENGTH_SHORT).show()
                    updateStatus()
                    return@launch
                }
                
                val csvFile = FileUtils.exportLogsToCSV(this@ExportActivity, logs)
                
                statusText.text = "✅ CSV berhasil diekspor!\n\n${csvFile.absolutePath}"
                
                shareFile(csvFile)
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}"
            }
        }
    }
    
    private fun exportZip() {
        statusText.text = "Membuat ZIP archive..."
        
        lifecycleScope.launch {
            try {
                val logs = viewModel.getAllLogs()
                
                if (logs.isEmpty()) {
                    Toast.makeText(this@ExportActivity, "Tidak ada log untuk diekspor", Toast.LENGTH_SHORT).show()
                    updateStatus()
                    return@launch
                }
                
                val zipFile = FileUtils.createEvidenceZip(this@ExportActivity, logs)
                
                statusText.text = "✅ ZIP berhasil dibuat!\n\n${zipFile.absolutePath}\n\nUkuran: ${zipFile.length() / 1024} KB"
                
                shareFile(zipFile)
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}"
            }
        }
    }
    
    private fun createTemplate() {
        lifecycleScope.launch {
            try {
                val templateFile = FileUtils.createAppealTemplate(this@ExportActivity)
                
                statusText.text = "✅ Template surat banding berhasil dibuat!\n\n${templateFile.absolutePath}"
                
                shareFile(templateFile)
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}"
            }
        }
    }
    
    private fun shareFile(file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = when {
                    file.extension == "csv" -> "text/csv"
                    file.extension == "zip" -> "application/zip"
                    else -> "text/plain"
                }
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(intent, "Bagikan File"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error sharing file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
