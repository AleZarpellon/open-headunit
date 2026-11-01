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
- Modello indicato da Android: K706. Le impostazioni di Android si aprono anche da Sistema → Di più nel menu del produttore. Il loro aspetto ("App e notifiche") fa pensare a un Android più vecchio del 14 dichiarato.
- Menu Generale → Tempo di sonno: Non dormire, 2 ore, 1, 3 o 7 giorni (impostato: 7 giorni). Col quadro spento per 2 minuti la radio dorme e non si riavvia: Zlink resta disattivato. Zlink torna dopo un riavvio vero.
- Nessuna voce "Batteria" nella pagina dell'app e nessuna "Ottimizzazione della batteria" in Accesso speciale alle app. Le impostazioni giuste si trovano cercando "batteria" nella ricerca di Android.
- Impostazioni della radio per l'app, in Batteria: Gestione avvio automatico → Avvio automatico app e Avvio secondario app su Consenti; Chiudi app dopo blocco schermo su Non chiudere; Ottimizzatore standby app → App ottimizzata spenta.
- Prima di queste impostazioni l'app non si apriva da sola al risveglio e il log portava l'ora dell'apertura a mano. Dopo, la Home della radio mostra "Android Auto – Connesso" senza aprire nulla e, toccando l'icona, il collegamento è quasi immediato. La schermata però non viene ancora in primo piano da sola.
- Prova del 2026-11-01: quadro spento alle 04:52:56 circa, riacceso alle 04:55:58 circa, icona toccata poco dopo le 04:56:58. File di log: uno creato alle 04:52:24 e fermo alle 04:52:35, uno nuovo creato alle 04:57:01. Un file nuovo vuol dire un processo nuovo: nonostante le impostazioni, l'app probabilmente è morta in standby ed è ripartita solo col tocco. "Android Auto – Connesso" sulla Home quindi non prova che l'app sia viva. Non si è letto il contenuto dei file.
- Prova con "Usa come launcher auto": al risveglio resta la Home della radio e, secondo il proprietario, nemmeno il tasto Home apre la nostra app. Strada scartata. La soluzione scelta è "Non dormire" (vedi sotto).
- **Configurazione che funziona (prima prova, 2026-11-01):** Generale → Tempo di sonno su "Non dormire", con Avvia all'accensione acceso e le impostazioni di Batteria qui sopra. Dopo 2 minuti a quadro spento la radio riparte col logo, l'app si apre da sola e Android Auto parte dalla nostra app, in circa 20-30 secondi. Zlink è rimasto disattivato, anche se dopo i riavvii precedenti era tornato attivo: il motivo non è chiaro. Confermata alla prima accensione del giorno dopo: Android Auto parte da solo, Zlink disattivato, Wi-Fi acceso, vivavoce e musica funzionano.
- I log con posizione "Cartella Download" stanno in `Android/data/io.github.alezarpellon.headunitcustom/files/Download/OpenHeadunitLogs/`, visibili col gestore file della radio. "Esporta log" può dire che non sono disponibili anche se i file ci sono.
- L'avviso giallo sul Wi-Fi spento può restare visibile anche a Wi-Fi acceso.

## Cosa cambia rispetto all'originale
| Cosa | Dove | Perché |
|---|---|---|
| `applicationId` = `io.github.alezarpellon.headunitcustom` | `app/build.gradle.kts`, `app/src/main/res/xml/shortcuts.xml` | Non entrare in conflitto con altre app. Il `namespace` del codice resta `com.andrerinas.openheadunit`. |
| Nome app "HeadUnit Revived Custom" (stringhe `title` e `app_name`; l'icona usa `title`) | `values/strings.xml`, `values-it/strings.xml` | Nome scelto dal proprietario. Le altre lingue e i testi interni dell'app dicono ancora "Open Headunit". |
| Versione 1.2 (`versionCode` 4) | `app/build.gradle.kts` | Numerazione nostra, 1.0, 1.1... |
| Controllo aggiornamenti all'avvio con Installa, Salta e Più tardi; permesso `REQUEST_INSTALL_PACKAGES` | `utils/UpdateInstaller.kt`, `main/MainActivity.kt`, `AndroidManifest.xml`, stringhe `update_*` | Richiesto dal proprietario. Non si è ancora visto all'opera: serve una versione successiva alla 1.2. |
| Action settimanale `Controlla originale` | `.github/workflows/upstream-sync.yml` | Propone le nuove versioni stabili dell'originale, ignorando le beta. |
| Guardiano di riconnessione: attese 2, 5, 10, 30 s e poi 30 s, controllo ogni 30 s che qualcosa stia ancora cercando il telefono | `nativeaa/ReconnectBackoffPolicy.kt`, `NativeAaHandshakeManager.kt` | Il proprietario vuole che la radio non si arrenda mai, salvo disconnessione manuale. |
| Si bussa anche dopo un ByeBye del telefono | `nativeaa/SessionEndGroupPolicy.kt` | Era il principale punto di resa. Conseguenza: chiudere Android Auto dal telefono lo fa richiamare. |
| Schermata non aperta due volte: si riprova dopo 30 s invece di fermarsi | `aap/AapService.kt` (`END_AND_HOLD`), `holdNextWake` | Stesso motivo. |
| Blocco anti-sonno durante la ricerca: massimo 15 minuti e mai a schermo spento | `aap/AapService.kt` (`acquireReconnectWakeLock`) | Non deve tenere sveglia la radio a quadro spento e scaricare la batteria dell'auto. |
| Registro disconnessioni, sempre attivo, con Salva e Condividi | `utils/DisconnectLog.kt`, `main/SettingsFragment.kt`, stringhe `disconnect_log*` | Richiesto dal proprietario, e il Bluetooth dalla radio non invia file: la schermata si può fotografare. |
| "Esporta log" cerca l'ultimo file su disco | `utils/LogExporter.kt`, `utils/LogFilesHelper.kt` | Dopo un riavvio dell'app diceva "non disponibili" anche con i file presenti. |
| La Action esegue anche i test | `.github/workflows/release-apk.yml` | Per accorgersi degli errori prima di installare. |
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

La Action esegue i test prima di compilare. `HctRetainedChannelTest` ("actual FSM bridge keeps its bound...") è fallito una volta e passato alla ripetizione, senza modifiche: sembra instabile. In quel caso si rilancia con `gh run rerun <id> --failed`.

## Come si aggiorna dall'originale
Ogni lunedì la Action `Controlla originale` cerca l'ultima versione stabile dell'originale. Si può lanciare anche a mano da GitHub, scheda Actions. Dato che il nostro codice è già oltre la 4.0.0-beta1, la prima proposta arriverà con la 4.0.0 stabile. Il branch predefinito del repository è `riconnessione-stabile`, perché le Action programmate partono solo da quello: se si cambia branch di lavoro, va aggiornato anche `BASE_BRANCH` nella Action.

- **Dal telefono, app GitHub:** Pull requests → apri la proposta → leggi il testo. Se dice che si unisce senza conflitti e i controlli sono passati, tocca **Merge**.
- **Dopo l'unione:** alza la versione e pubblica una Release, come sopra.
- **Se ci sono conflitti:** la proposta elenca i file. Non si risolvono dal telefono: apri una sessione con Claude e fagli leggere questo file.
- **Se arriva un'e-mail di Action fallita:** spesso l'originale ha cambiato un file in `.github/workflows`, che il token automatico non può toccare. La proposta va creata a mano o con Claude.
- GitHub sospende le Action programmate dopo 60 giorni senza attività nel repository: se succede, riattivala dalla scheda Actions.

## Chiave di firma
- Sta nei GitHub Secrets: `HEADUNIT_KEYSTORE_BASE64`, `HEADUNIT_KEYSTORE_PASSWORD`, `HEADUNIT_KEY_PASSWORD`. Alias: `headunit-revived`.
- La copia di backup la conserva il proprietario, fuori dal repository. Non va mai caricata qui.
- Se si perde, le nuove versioni non si installano più sopra le vecchie.
- `keystore.jkc`, presente nel repository originale, è pubblico: non va usato.

## Come funziona la riconnessione (modalità Native)
È il telefono a chiamare la radio: la radio crea la rete Wi-Fi Direct, ascolta via Bluetooth e sulla porta 5288, e "bussa" al telefono via Bluetooth. Dalla v1.1 ci pensa il guardiano (vedi tabella sopra). Cosa resta volutamente:
- La disconnessione manuale dalla radio ferma tutto.
- `NativeAaWakeDamagePolicy` e `HandsFreeWakeEscalationPolicy`: se bussare fa perdere il vivavoce la radio lascia stare il telefono. Protegge le chiamate, va rispettato.
- `JoinRefusalPolicy`: se il telefono rifiuta più volte la rete, l'attesa arriva fino a 2 minuti, oltre i 30 secondi chiesti.
- Primo tentativo dopo 5 secondi (`WAKE_SETTLE_MS`): prima il telefono non vede ancora la rete.
- La riapertura dell'ascolto Bluetooth ha un numero limitato di tentativi: non è stata toccata.

Sulla strada verso casa del 2026-11-01 il proprietario ha notato piccole perdite di connessione con la v1.0.1. Causa non ancora nota: controllare il registro disconnessioni dopo la v1.1.

## Stato del lavoro
- [x] Fork, Actions abilitate, branch `riconnessione-stabile`
- [x] Identità, firma e Action di Release (v1.0)
- [x] v1.0.1: pulsanti per aprire le schermate di sistema nascoste
- [x] Prova della modalità Native sulla radio con Zlink disattivato e Wi-Fi acceso a mano: Android Auto, vivavoce e musica Bluetooth funzionano. Non parte da solo.
- [x] Avvio automatico: funziona con "Non dormire" alla prima prova (vedi sezione La radio)
- [x] Prova alla prima accensione del giorno dopo, senza toccare nulla: Android Auto parte da solo, Zlink resta disattivato, Wi-Fi acceso, vivavoce e musica funzionano
- [ ] v1.1: guardiano di riconnessione e registro delle disconnessioni. Prove da fermo del 2026-11-01 con il registro: Wi-Fi spento 10 s e arresto forzato di Android Auto tornano da soli in 6-12 s con 0 tentativi (il telefono torna senza che la radio bussi, quindi il guardiano non è ancora stato messo alla prova); "Interrompi connessione" registrata come manuale, nessun ritorno da solo secondo il proprietario; chiamata sulle casse senza cadute, ma probabilmente dopo il ritorno. Salva funziona. Il proprietario dice che la prova con Wi-Fi spento per 3 minuti e chiamata in mezzo funziona, ma la foto del registro manca: tentativi maggiori di 0 e vivavoce mentre la radio bussa restano da confermare. I blocchi tornando a casa erano leggeri e solo sulla musica, senza disconnessioni: al prossimo viaggio confrontare musica scaricata e in streaming e guardare se si ferma anche la mappa.
- [ ] Controllo aggiornamenti all'avvio e Pull Request settimanale dall'originale (solo versioni stabili): scritti nella v1.2, l'avviso è da vedere con la versione successiva. Audio: moltiplicatore di latenza a 16x sulla radio, quindi già alto; prossima prova Audio AAC.
- [ ] Disattivare Zlink, solo dopo che la nostra app funziona bene
- [x] Fase 2: mappa di tutte le modifiche possibili, in `MAPPA-MODIFICHE.md`. Il proprietario deve ancora scegliere le voci.
- [ ] Fase 3: personalizzazioni su branch `personalizzazioni`, con Release separata

## Idea da provare: mappa grande e barra di Spotify
Il proprietario vuole mappa grande e Spotify come barra stretta in basso. Toccando l'icona di Waze Android Auto apre già la mappa a tutta larghezza, senza schede, quindi non serve ritagliare il video. Il testo delle notifiche non arriva alla radio: `GenericNotificationService` nel protocollo è vuoto e il codice non lo usa, quindi restano dentro il video di Android Auto.

Sul branch `personalizzazioni`, versione `v1.2-prova1`, non proposta come aggiornamento perché viene confrontata come uguale alla 1.2 ed esce come pre-release. Interruttori in Impostazioni → Audio, spenti di serie:
- **Barra della musica** (`aap/MusicBarView.kt`, `aap/NowPlayingState.kt`): sta nel contenuto della finestra, nello spazio liberato dal padding del `container`.
- **Solo sopra una mappa** (`aap/MusicBarPolicy.kt`): ogni 1,5 s legge l'immagine in piccolo e cerca una colonna scura nella metà destra. La trova nella fessura dei widget e nelle app scure, non sulle mappe. Il margine passa da 0 a 80 tramite `SystemUI.apply(extraBottom)`, che poi riannuncia i margini al telefono. Da provare: se Android Auto si adatta senza scollegarsi, se i tocchi restano giusti, se la lettura rallenta il video. Funziona solo con TextureView.

Le versioni stabili successive dovranno avere `versionCode` maggiore di 5, altrimenti non si installano sopra la prova.

La Action `Controlla originale` è stata provata solo nel caso "niente da fare". Il ramo che unisce e apre la Pull Request non è ancora stato eseguito.
