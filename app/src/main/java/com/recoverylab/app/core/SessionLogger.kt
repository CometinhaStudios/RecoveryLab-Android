package com.recoverylab.app.core

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Logger central da sessão de varredura/recuperação.
 * Nunca registrar um arquivo como recuperado sem validar o arquivo de saída.
 */
object SessionLogger {
    private const val TAG = "RecoveryLab"
    private lateinit var logFile: File
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    fun init(context: Context) {
        val dir = File(context.filesDir, "logs").apply { mkdirs() }
        logFile = File(dir, "session.log")
    }

    fun log(message: String, level: Level = Level.INFO) {
        val line = "${timeFormat.format(Date())} [${level.name}] $message"
        // android.util.Log é um stub nos testes JVM locais; o logger em arquivo deve continuar seguro.
        runCatching {
            when (level) {
                Level.INFO -> Log.i(TAG, message)
                Level.WARN -> Log.w(TAG, message)
                Level.ERROR -> Log.e(TAG, message)
            }
        }
        runCatching { if (::logFile.isInitialized) logFile.appendText(line + "\n") }
    }

    fun logValidation(fileName: String, validated: Boolean, reason: String) {
        val status = if (validated) "VALIDADO" else "NÃO VALIDADO"
        log("Arquivo '$fileName': $status — $reason", if (validated) Level.INFO else Level.WARN)
    }

    fun readLog(): String = runCatching { if (::logFile.isInitialized) logFile.readText() else "" }.getOrDefault("")
    enum class Level { INFO, WARN, ERROR }
}
