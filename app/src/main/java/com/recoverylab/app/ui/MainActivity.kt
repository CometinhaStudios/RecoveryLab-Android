package com.recoverylab.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recoverylab.app.shizuku.ShizukuManager
import rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {
    private val permissionListener = Shizuku.OnRequestPermissionResultListener { _, _ ->
        refreshTrigger++
    }
    private var refreshTrigger by mutableStateOf(0)

    override fun onResume() {
        super.onResume()
        // Recheck after returning from Shizuku settings/notification so stale state is not kept.
        refreshTrigger++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shizuku.addRequestPermissionResultListener(permissionListener)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        refreshKey = refreshTrigger,
                        onRequestShizuku = { ShizukuManager.requestPermission() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        Shizuku.removeRequestPermissionResultListener(permissionListener)
        super.onDestroy()
    }
}

@Composable
fun HomeScreen(refreshKey: Int, onRequestShizuku: () -> Unit) {
    val shizukuState = remember(refreshKey) { ShizukuManager.currentState() }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Recovery Lab", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Documento de planejamento — não é garantia de recuperação. Resultados só aparecem como válidos após validação real do arquivo de saída.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Status do Shizuku", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(ShizukuManager.describeState(shizukuState))
                Spacer(Modifier.height(8.dp))
                if (shizukuState is ShizukuManager.ShizukuState.AvailableNoPermission) {
                    Button(onClick = onRequestShizuku) { Text("Autorizar Shizuku") }
                }
                OutlinedButton(onClick = { /* state is refreshed by Activity lifecycle */ }) {
                    Text("Status atualizado ao voltar para o app")
                }
            }
        }
        HomeButton("Memória acessível", "Analisar pastas e arquivos que o app já pode acessar")
        HomeButton("USB/OTG e SD", "Detectar unidades externas conectadas")
        HomeButton("Sessões anteriores", "Ver varreduras e relatórios salvos")
        HomeButton("Diagnóstico de permissões", "Ver exatamente o que o app pode e não pode fazer neste aparelho")
    }
}

@Composable
private fun HomeButton(title: String, subtitle: String) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: navegação — implementado na Fase 1 */ }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
