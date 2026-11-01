# Mappa delle modifiche possibili

Per l'autoradio ACAVICA K706 (schermo 1280x720) sulla Clio 4. Fonti: wiki dell'originale e codice. La wiki descrive la 3.5.0, mentre la nostra base è più recente: qualche voce può avere un nome o un posto diverso.

**Legenda**
- **Dove:** "Impostazione" vuol dire che c'è già e basta attivarla, senza nuove versioni. "Codice" vuol dire che serve una nuova versione dell'app.
- **Difficoltà e rischio:** B bassa, M media, A alta.
- **Priorità:** 1 da fare, 2 utile, 3 solo se ti interessa, **No** sconsigliata o inutile sulla tua radio.

Le impostazioni si trovano nell'app, in Impostazioni. Molte sono nella scheda **Avanzate**, e la ricerca in alto cerca in entrambe le schede.

## Le tue idee

| Idea | Cosa si può fare | Dove | Diff. | Rischio | Priorità |
|---|---|---|---|---|---|
| Valori già pronti per il 10,1" | Fare in modo che resolution, DPI e dimensioni partano già giuste anche dopo un ripristino | Codice | B | B | 3: basta il backup delle impostazioni, qui sotto |
| Pulsanti e menu più grandi | Scala interfaccia 100-150% e Dimensione pulsanti 60-120% | Impostazione: Tema → Personalizzazione | B | B | 1 |
| Android Auto più grande o più piccolo | Densità (DPI): più alta, icone più grandi | Impostazione: Grafica → DPI | B | B | 1 |
| Splash personalizzato | Immagine o video (fino a 10 MB) mentre Android Auto si avvia | Impostazione: Tema → Schermata di caricamento | B | B | 2 |
| Avvio all'accensione e collegamento all'ultimo telefono | Già funzionante con "Non dormire" | Fatto | – | – | Fatto |
| Modalità notte automatica | Alba e tramonto, oppure orario. Con il segnale dei fari solo se il cablaggio lo porta alla radio | Impostazione: Tema → Modalità scura | B | B | 1 |
| Tornare al menu radio senza chiudere il collegamento | "Sposta in background" dal menu con due dita, oppure pulsante flottante sempre visibile | Impostazione: Altre funzionalità → pulsante flottante e Azione di uscita | B | B | 1 |
| Tema e colori | Sfondo, colore di ogni pulsante, grigio di notte | Impostazione: Tema → Personalizzazione | B | B | 2 |
| Tutto in italiano | Tradurre le voci rimaste in inglese e togliere "Open Headunit" dai testi | Codice | M | B | 2 |

## Connessione

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Banda WiFi Direct | Sceglie 5 GHz o 2,4 GHz | Impostazione | B | M | Da provare per i blocchi alla musica, solo dopo la prova in viaggio | 2 |
| Canale 5 GHz | Fissa il canale | Impostazione | B | M | Solo se la banda non basta | 3 |
| Stessa rete WiFi Direct | Il telefono ritrova sempre la stessa rete | Impostazione, già attiva | – | – | Lasciala attiva | Fatto |
| Nuova identità rete | Rigenera nome e password della rete | Impostazione | B | M | Solo se il telefono non si collega più | Di riserva |
| Telefono preferito | Se in auto ci sono più telefoni, sceglie il tuo | Impostazione | B | B | Utile con un passeggero Android | 2 |
| Registro disconnessioni con notifica a schermo | Avvisa quando il collegamento cade | Codice | B | B | Non serve finché il registro basta | 3 |
| Modalità Hotspot, Helper o Server | Altri modi di collegarsi | Impostazione | M | A | La Native funziona già | No |
| Disconnessione automatica via Bluetooth | Chiude la sessione se il Bluetooth sparisce | Impostazione | B | A | Va contro il guardiano | No |

## Prestazioni

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Overlay prestazioni | Mostra FPS, CPU, temperatura e ritardo video | Impostazione: Debug | B | B | Serve a capire i blocchi | 2 |
| Limite FPS 30 | Video più leggero, più stabile su collegamenti deboli | Impostazione: Video | B | B | Da provare se si blocca anche la mappa | 2 |
| Riduci video sui 2,4 GHz | Limita a 720p e 30 FPS | Impostazione, già attiva | – | – | Lasciala attiva | Fatto |
| Bassa latenza del decoder | Tocco più pronto, se la radio lo regge | Impostazione | B | M | Da provare per ultima | 3 |
| Modalità di visualizzazione | TextureView, SurfaceView o GLES20 | Impostazione | B | M | Solo con problemi video | 3 |
| Decodifica software | Video calcolato dal processore | Impostazione | B | A | Più lenta, la tua H.264 funziona | No |

## Audio

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Riprendi la musica alla riconnessione | Se cade, riparte da sola se il telefono torna entro 60 s | Impostazione | B | B | Si abbina bene al guardiano | 1 |
| Regolazione volumi | Musica, navigatore e sistema separati, da 0 a 200% | Impostazione | B | B | Utile se il navigatore è troppo basso | 2 |
| Audio AAC | Audio compresso, usa meno banda | Impostazione | B | M | Da provare per i blocchi | 2 |
| Coda audio | Quanti pezzi di audio tenere in attesa | Impostazione | B | M | Solo dopo la prova in viaggio | 3 |
| Moltiplicatore di latenza | Memoria audio | Impostazione, predefinito 8x (il massimo) | – | – | Controlla solo che sia a 8x | Controllo |
| Flussi audio separati | Musica e navigatore su uscite diverse | Impostazione | B | M | Non serve con un solo impianto | 3 |
| Uscita AAudio | Audio a bassa latenza, sperimentale | Impostazione | B | M | Può gracchiare | 3 |
| Microfono | Sorgente e riduzione rumore per l'Assistente | Impostazione | B | B | Se l'Assistente non ti capisce | 2 |
| Sincronizza brano col sistema | Titolo e copertina nella radio | Impostazione | B | B | Da provare se la Home della radio li mostra | 3 |

## Schermo e grafica

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Margini personalizzati | Evita che le barre della radio coprano l'app | Impostazione | B | B | Solo se qualcosa è coperto | 2 |
| Adattamento video | Riempi, con bande o ritaglia | Impostazione | B | B | Se l'immagine sembra schiacciata | 2 |
| Modalità schermo | Quali barre di sistema si vedono | Impostazione | B | B | Se vuoi tutto a schermo intero | 2 |
| Android Auto in bianco e nero di notte | Meno abbagliamento | Impostazione, richiede GLES20 | B | M | Solo se ti dà fastidio | 3 |
| Nascondi orologio, segnale, batteria | Pulisce la barra di Android Auto | Impostazione | B | B | Gusto personale | 3 |
| Icona dell'app personalizzata | Cambia l'icona | Codice | B | B | Gusto personale | 3 |
| Risoluzione oltre 720p | Più dettaglio | Impostazione | B | A | Lo schermo è 720p, non guadagni nulla | No |
| HUD, correzione tocco, ultrawide | Per schermi o usi particolari | Impostazione | B | B | Non servono | No |

## Comandi e tasti

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Comandi al volante | Assegna i tasti alle azioni di Android Auto | Impostazione: Mappa tasti, e Studio del volante nel menu della radio | B | B | Da provare sulla Clio | 1 |
| Tasti media verso Android Auto | Sempre, automatico o mai | Impostazione | B | B | Se i tasti comandano la radio invece di Android Auto | 2 |
| Pulsante flottante | Pulsante sempre visibile per tornare alla Home della radio | Impostazione | B | B | Con dimensione e trasparenza regolabili | 1 |
| Picture-in-Picture | Android Auto in una finestrella | Menu con due dita | B | M | Da provare, non tutte le radio lo reggono | 3 |
| Comando rotativo | Manopole tipo iDrive | Impostazione | B | M | Non ce l'hai | No |

## Automazioni

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Avvisa solo se si accende la radio | Avvio dell'app allo schermo acceso | Impostazione | B | M | Non serve con "Non dormire" | 3 |
| Comandi esterni (Tasker, ADB) | Controlla l'app da altre app | Impostazione | M | M | Non hai altre app lì | 3 |
| Disattivare Zlink in modo stabile | Disattivazione via ADB se Zlink torna | Da PC | M | B | Solo se torna: oggi è rimasto spento | Di riserva |
| Launcher auto | L'app diventa la Home | Impostazione | B | B | Provato: la radio lo ignora | No |
| Chiudi app alla disconnessione | Chiude tutto quando cade | Impostazione | B | A | Annulla il guardiano | No |

## Impostazioni e dati

| Modifica | Cosa fa | Dove | Diff. | Rischio | Per la tua radio | Priorità |
|---|---|---|---|---|---|---|
| Backup delle impostazioni | Salva tutto in un file da ripristinare | Impostazione: Backup | B | B | Fallo appena la configurazione ti piace | 1 |
| Nome del veicolo sul telefono | Il nome con cui il telefono vede l'auto | Impostazione: Informazioni veicolo | B | B | Per esempio "Clio" | 2 |
| Marca del veicolo | Logo sul tasto di uscita | Impostazione | B | M | Con una marca vera (nel wizard hai scelto Renault) Android Auto non elenca più le app installate fuori dal Play Store. Lasciala così se non ne usi | Controllo |
| Avviso giallo Wi-Fi corretto | Toglie l'avviso quando il Wi-Fi è acceso | Codice | B | B | Oggi resta anche a Wi-Fi acceso | 2 |
| ID veicolo | Identificativo interno | Impostazione | B | A | Cambiarlo azzera le preferenze di Android Auto | No |
| Blocco video disattivato | Finge una velocità bassa per permettere i video | Impostazione, attiva di serie | B | A | Per sicurezza guardare video guidando va evitato: valuta di spegnerla | Controllo |

## Le 8 da cui partirei

Tutte già nelle impostazioni, nessuna richiede una nuova versione:
1. Scala interfaccia e dimensione dei pulsanti
2. DPI di Android Auto
3. Modalità notte con alba e tramonto
4. Pulsante flottante e azione di uscita
5. Riprendi la musica alla riconnessione
6. Comandi al volante
7. Backup delle impostazioni
8. Controllo del moltiplicatore di latenza a 8x
