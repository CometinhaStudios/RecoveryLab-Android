package com.recoverylab.app

import android.app.Application
import com.recoverylab.app.core.SessionLogger

/** Inicializa logs locais; não escreve nas mídias examinadas. */
class RecoveryLabApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionLogger.init(this)
        SessionLogger.log("App iniciado — versão 0.1-base")
    }
}
