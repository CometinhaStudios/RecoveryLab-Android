package com.recoverylab.app.shizuku

import android.content.pm.PackageManager
import com.recoverylab.app.core.SessionLogger
import rikka.shizuku.Shizuku

/** Integração inicial com Shizuku; não promete acesso root nem a setores físicos. */
object ShizukuManager {
    const val REQUEST_CODE_PERMISSION = 7421

    sealed class ShizukuState {
        data object Unavailable : ShizukuState()
        data object AvailableNoPermission : ShizukuState()
        data class AvailableGranted(val runningAsRoot: Boolean) : ShizukuState()
    }

    fun currentState(): ShizukuState {
        val binderAlive = try {
            Shizuku.pingBinder()
        } catch (t: Throwable) {
            SessionLogger.log("Shizuku indisponível: ${t.message}", SessionLogger.Level.WARN)
            false
        }
        if (!binderAlive) return ShizukuState.Unavailable

        val granted = try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (t: Throwable) {
            false
        }
        if (!granted) return ShizukuState.AvailableNoPermission

        val uid = try { Shizuku.getUid() } catch (t: Throwable) { -1 }
        val isRoot = uid == 0
        SessionLogger.log("Shizuku ativo. UID=$uid (${if (isRoot) "root" else "shell/ADB"})")
        return ShizukuState.AvailableGranted(runningAsRoot = isRoot)
    }

    fun requestPermission() {
        if (Shizuku.isPreV11()) {
            SessionLogger.log("Versão do Shizuku anterior à v11 — fluxo legado não implementado.", SessionLogger.Level.WARN)
            return
        }
        Shizuku.requestPermission(REQUEST_CODE_PERMISSION)
    }

    fun describeState(state: ShizukuState): String = when (state) {
        is ShizukuState.Unavailable ->
            "Shizuku não está em execução neste aparelho. Os recursos adicionais ficarão desativados."
        is ShizukuState.AvailableNoPermission ->
            "Shizuku detectado, mas o Recovery Lab ainda não tem autorização. Toque para autorizar."
        is ShizukuState.AvailableGranted ->
            if (state.runningAsRoot)
                "Shizuku autorizado, rodando com privilégio de root real. Ainda assim, nenhuma operação de escrita será feita na mídia examinada."
            else
                "Shizuku autorizado via ADB (shell). Isso NÃO é root — algumas operações continuarão indisponíveis."
    }
}
