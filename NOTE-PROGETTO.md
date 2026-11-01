# Note di progetto: HeadUnit Revived Custom

Fork personale di [Open Headunit](https://github.com/andreknieriem/open-headunit) (ex HeadUnit Revived) per un'autoradio ACAVICA su Renault Clio 4. Chi riprende il lavoro, anche un altro Claude, parta da qui.

Il proprietario non è uno sviluppatore esperto: spiegazioni in italiano e semplici, piano mostrato e approvato prima di ogni modifica, modifiche piccole e mirate.

## La radio
Dati letti dalla schermata di sistema:
- Piattaforma 8581, firmware `QF005.20241010.165656`, MCU `QF30.V03.03`, CanMcu `RN V1.05.100BYPS`
- Android 14 dichiarato, da verificare sulla radio
- Schermo: il sistema indica 1280x720 (il proprietario ricordava 1280x800), da verificare
- Bluetooth 5.0: non è noto se il modulo è interno o separato
- App di proiezione preinstallata: Zlink (`com.zjinnova.zlink`), da lasciare intatta come riserva finché la nostra non funziona bene. Dopo un arresto forzato torna a collegarsi.
- Il menu impostazioni è del produttore e non mostra quelle di Android. Nessun root né Self-ADB rilevato.
- Test video dell'app superato: H.264 a 480p e 720p

## Cosa cambia rispetto all'originale
| Cosa | Dove | Perché |
|---|---|---|
| `applicationId` = `io.github.alezarpellon.headunitcustom` | `app/build.gradle.kts`, `app/src/main/res/xml/shortcuts.xml` | Non entrare in conflitto con altre app. Il `namespace` del codice resta `com.andrerinas.openheadunit`. |
| Nome app "HeadUnit Revived Custom" (stringhe `title` e `app_name`; l'icona usa `title`) | `values/strings.xml`, `values-it/strings.xml` | Nome scelto dal proprietario. Le altre lingue e i testi interni dell'app dicono ancora "Open Headunit". |
| Versione 1.0 (`versionCode` 1) | `app/build.gradle.kts` | Numerazione nostra, 1.0, 1.1... |
| "Controlla aggiornamenti" punta a questo repository | `utils/UpdateChecker.kt` | Altrimenti proporrebbe l'APK ufficiale, con altro ID e altra firma. |
| Avviso di versione modificata e link ai sorgenti | `main/AboutFragment.kt` | Richiesto dalla licenza AGPL-3.0. |
| Pulsanti per aprire pagina di Zlink, impostazioni Android, info dispositivo e opzioni sviluppatore | `main/SettingsFragment.kt`, `layout/dialog_oem_app_manager.xml`, stringhe `oem_app_open_*` | La radio nasconde le impostazioni di Android: servono per disattivare Zlink e per ADB. |
| Action `Release APK` | `.github/workflows/release-apk.yml` | Compila, firma e pubblica. |

Quando si fa il merge dall'originale, in `app/build.gradle.kts` vanno tenuti i nostri `applicationId`, `versionCode` e `versionName`.

## Base del codice
Commit `2049df15` dell'originale, successivo alla 4.0.0-beta1 e non ancora una versione ufficiale. Scelto al posto della 3.5.0 perché solo il codice recente ha il collegamento al modulo Bluetooth delle radio Unisoc "ZJ", che potrebbe servire su questa radio. Se si rivela instabile e il collegamento non serve, si torna alla 3.5.0.

## Come si compila e si pubblica
Si compila solo su GitHub, non serve nulla sul PC.
- **Push sul branch `riconnessione-stabile`:** compila e verifica l'APK firmato, che si trova tra gli artifact dell'Action.
- **Release:**
  1. Alza `versionCode` e `versionName` in `app/build.gradle.kts`.
  2. Scrivi le note in italiano in `note-release/vX.Y.md`.
  3. Crea e invia il tag `vX.Y`. L'Action rifiuta il tag se non corrisponde a `versionName`.

## Chiave di firma
- Sta nei GitHub Secrets: `HEADUNIT_KEYSTORE_BASE64`, `HEADUNIT_KEYSTORE_PASSWORD`, `HEADUNIT_KEY_PASSWORD`. Alias: `headunit-revived`.
- La copia di backup la conserva il proprietario, fuori dal repository. Non va mai caricata qui.
- Se si perde, le nuove versioni non si installano più sopra le vecchie.
- `keystore.jkc`, presente nel repository originale, è pubblico: non va usato.

## Come funziona la riconnessione (modalità Native)
È il telefono a chiamare la radio: la radio crea la rete Wi-Fi Direct, ascolta via Bluetooth e sulla porta 5288, e "bussa" al telefono via Bluetooth. Punti in cui oggi si arrende o rallenta:
- `SessionEndGroupPolicy.wakesPhoneAfterSessionEnd`: se il telefono chiude con un ByeBye non bussa più, ascolta soltanto.
- `AapService.scheduleReconnectIfNeeded`: `holdsRecovery`, la schermata di proiezione non si è aperta due volte.
- `NativeAaHandshakeManager`: il ciclo dei tentativi si interrompe in alcuni casi. La riapertura dell'ascolto Bluetooth ha un numero limitato di tentativi.
- `NativeAaWakeDamagePolicy`: se bussare fa perdere il vivavoce smette di bussare. Protegge le chiamate, va rispettato.
- `AapService.acquireBootWakeLock`: il blocco che tiene sveglia la radio dura solo 10 minuti dall'avvio.

## Stato del lavoro
- [x] Fork, Actions abilitate, branch `riconnessione-stabile`
- [x] Identità, firma e Action di Release (v1.0)
- [x] v1.0.1: pulsanti per aprire le schermate di sistema nascoste
- [x] Prova della modalità Native sulla radio con Zlink disattivato e Wi-Fi acceso a mano: Android Auto, vivavoce e musica Bluetooth funzionano. Non parte da solo.
- [ ] Avvio automatico: prova con "Avvia all'accensione" e con Zlink di nuovo attivo. La radio riattiva Zlink a ogni riavvio, anche se disattivato dalla sua pagina. Da verificare se il Wi-Fi resta acceso dopo il riavvio.
- [ ] v1.1: guardiano di riconnessione e registro delle disconnessioni
- [ ] Controllo aggiornamenti all'avvio e Pull Request settimanale dall'originale (solo versioni stabili)
- [ ] Disattivare Zlink, solo dopo che la nostra app funziona bene
- [ ] Fase 2: mappa di tutte le modifiche possibili
- [ ] Fase 3: personalizzazioni su branch `personalizzazioni`, con Release separata
