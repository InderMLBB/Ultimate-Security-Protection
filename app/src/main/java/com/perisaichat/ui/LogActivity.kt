package com.perisaichat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.perisaichat.R
import com.perisaichat.data.ThreatLog
import com.perisaichat.util.FileUtils

class LogActivity : AppCompatActivity() {
    
    private val viewModel: LogViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        
        recyclerView = findViewById(R.id.recyclerViewLogs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = LogAdapter { log ->
            showLogDetailDialog(log)
        }
        recyclerView.adapter = adapter
        
        findViewById<Button>(R.id.btnClearLogs).setOnClickListener {
            confirmClearLogs()
        }
        
        observeLogs()
    }
    
    private fun observeLogs() {
        viewModel.allLogs.observe(this) { logs ->
            adapter.submitList(logs)
            
            findViewById<TextView>(R.id.tvLogCount).text = 
                "Total Logs: ${logs.size}"
        }
    }
    
    private fun showLogDetailDialog(log: ThreatLog) {
        val view = layoutInflater.inflate(R.layout.dialog_log_detail, null)
        
        view.findViewById<TextView>(R.id.tvDetailTimestamp).text = 
            FileUtils.formatTimestamp(log.timestamp)
        view.findViewById<TextView>(R.id.tvDetailApp).text = log.appSource
        view.findViewById<TextView>(R.id.tvDetailSender).text = log.senderId ?: "Unknown"
        view.findViewById<TextView>(R.id.tvDetailMessage).text = log.messageSnippet
        view.findViewById<TextView>(R.id.tvDetailReason).text = log.reason
        view.findViewById<TextView>(R.id.tvDetailScore).text = "${log.score}/100"
        view.findViewById<TextView>(R.id.tvDetailHash).text = log.hash
        
        val checkBox = view.findViewById<CheckBox>(R.id.cbFalsePositive)
        checkBox.isChecked = log.isFalsePositive
        
        AlertDialog.Builder(this)
            .setTitle("Detail Ancaman")
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                if (checkBox.isChecked != log.isFalsePositive) {
                    viewModel.updateFalsePositive(log, checkBox.isChecked)
                }
            }
            .setNegativeButton("Tutup", null)
            .setNeutralButton("Hapus") { _, _ ->
                viewModel.deleteLog(log)
            }
            .show()
    }
    
    private fun confirmClearLogs() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Semua Log")
            .setMessage("Apakah Anda yakin ingin menghapus semua log ancaman? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ ->
                viewModel.deleteAllLogs()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    
    class LogAdapter(
        private val onItemClick: (ThreatLog) -> Unit
    ) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
        
        private var logs = listOf<ThreatLog>()
        
        fun submitList(newLogs: List<ThreatLog>) {
            logs = newLogs
            notifyDataSetChanged()
        }
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_log, parent, false)
            return LogViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            holder.bind(logs[position], onItemClick)
        }
        
        override fun getItemCount() = logs.size
        
        class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvTimestamp: TextView = itemView.findViewById(R.id.tvLogTimestamp)
            private val tvApp: TextView = itemView.findViewById(R.id.tvLogApp)
            private val tvReason: TextView = itemView.findViewById(R.id.tvLogReason)
            private val tvScore: TextView = itemView.findViewById(R.id.tvLogScore)
            private val tvMessage: TextView = itemView.findViewById(R.id.tvLogMessage)
            
            fun bind(log: ThreatLog, onItemClick: (ThreatLog) -> Unit) {
                tvTimestamp.text = FileUtils.formatTimestamp(log.timestamp)
                tvApp.text = log.appSource.split(".").lastOrNull() ?: log.appSource
                tvReason.text = log.reason.uppercase()
                tvScore.text = "${log.score}"
                tvMessage.text = log.messageSnippet.take(100) + 
                    if (log.messageSnippet.length > 100) "..." else ""
                
                itemView.setOnClickListener { onItemClick(log) }
                
                if (log.isFalsePositive) {
                    itemView.alpha = 0.5f
                }
            }
        }
    }
}
