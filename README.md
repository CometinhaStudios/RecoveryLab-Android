# Recovery Lab — Fase 0 (Base)

App Android de recuperação de arquivos. **Documento/código de planejamento — não é garantia de recuperação.** Nenhum recurso é considerado pronto até ter código, build e testes verificáveis.

## Status atual
Fase 0 concluída nesta entrega:
- Projeto Kotlin + Jetpack Compose compila.
- Tela inicial com os 4 atalhos do plano (Memória acessível, USB/OTG e SD, Sessões anteriores, Diagnóstico de permissões) — navegação real entra na Fase 1.
- Logger de sessão (`SessionLogger`) que nunca marca um arquivo como recuperado sem validação.
- Integração inicial com Shizuku: detecta se o serviço está rodando, se a permissão foi concedida e se o processo roda como root real (UID 0) ou como shell ADB (UID 2000) — nunca afirma "root" quando é ADB.
- Workflow de CI no GitHub Actions: compila o APK debug na nuvem e publica como artifact a cada push em `dev`/`main`, ou manualmente.

## Como instalar o APK gerado
1. No GitHub, abra a aba **Actions** do repositório.
2. Abra a execução mais recente do workflow **Android CI - Build APK**.
3. Baixe o artifact `recovery-lab-debug-apk`.
4. Transfira o `.apk` para o aparelho e instale (é necessário permitir "instalar de fontes desconhecidas" para o app usado na transferência).

## Permissões solicitadas e por quê
| Permissão | Uso |
|---|---|
| `READ_MEDIA_IMAGES/VIDEO/AUDIO` (Android 13+) | Ler mídias existentes que o usuário autorizar |
| `READ_EXTERNAL_STORAGE` (até Android 12) | Equivalente nas versões antigas |
| `android.hardware.usb.host` (opcional) | Detectar e abrir ao conectar unidade USB/OTG |
| Shizuku (sob autorização explícita do usuário) | Operações adicionais permitidas pela API oficial do Shizuku — nunca aciona nada sem essa autorização |

O app **não** solicita permissão de administrador de dispositivo, não usa APIs privadas e não tenta ignorar permissões do sistema.

## Limitações conhecidas (Fase 0)
- Ainda não há motor de varredura (Fases 1–3) nem leitura de setores apagados.
- Os botões da tela inicial ainda não navegam — isso é a Fase 1.
- Sem Shizuku, o app funciona só com o que o Android concede via SAF/MediaStore.
- Com Shizuku via ADB, o app tem privilégio de shell, **não** acesso irrestrito a setores físicos.
- Dados protegidos por criptografia, sobrescritos, afetados por TRIM ou pelo controle interno do flash podem ser irrecuperáveis — o app nunca promete o contrário.

## Enviar logs de falha
Os logs de sessão ficam em `filesDir/logs/session.log` dentro do armazenamento do app. Em builds futuras, a tela de Diagnóstico terá um botão para exportar esse arquivo diretamente.

## Próximos passos (Fase 1 — Scanner seguro)
- Implementar navegação real dos 4 atalhos.
- Seleção de pasta/unidade via Storage Access Framework.
- Listagem de arquivos/metadados acessíveis, sem inventar arquivos apagados.
