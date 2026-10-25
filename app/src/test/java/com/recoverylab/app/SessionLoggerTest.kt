package com.recoverylab.app

import com.recoverylab.app.core.SessionLogger
import org.junit.Assert.assertTrue
import org.junit.Test

/** Teste mínimo da Fase 0: garante que o logger não derruba o app sem Context. */
class SessionLoggerTest {
    @Test
    fun logWithoutInit_doesNotThrow() {
        var threw = false
        try {
            SessionLogger.log("teste sem init")
        } catch (t: Throwable) {
            threw = true
        }
        assertTrue("log() não deve lançar exceção mesmo sem init()", !threw)
    }
}
