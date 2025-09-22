package it.filippo.casadei.controller;

import it.filippo.casadei.model.*;
import it.filippo.casadei.view.BriscolaView;
import it.filippo.casadei.view.BriscolaViewObserver;

import java.util.Optional;

// TODO: DECIDERE SE UTILIZZARE OBSERVER O NO

/**
 * Controller per gestire il flusso di una partita di Briscola 1vs1.
 * Si appoggia a un'interfaccia GameView per tutte le interazioni con l'utente (console, GUI, ecc.).
 */

public class BriscolaController implements BriscolaViewObserver {
    private final BriscolaGame model;
    private final BriscolaView view;
    private final Player player1;
    private final Player player2;

    // == COSTRUTTORE ==

    /**
     * Crea una nuova istanza del controller per gestire una partita di Briscola.
     * Inizializza il model con i giocatori forniti, un nuovo mazzo e un nuovo tavolo da gioco.

     * @param view    l'interfaccia utente da utilizzare per la partita
     * @param player1 il primo giocatore della partita
     * @param player2 il secondo giocatore della partita
     */
    public BriscolaController(BriscolaView view, Player player1, Player player2) {
        this.view = view;
        this.player1 = player1;
        this.player2 = player2;
        this.model = new BriscolaGame(player1, player2, new Deck(), new Table());

        // Sceglie quale view utilizzare in base all'istanza in runtime dell'interfaccia view
        view.start(this);  //TODO: decidere se usare o no observer
    }

    // == METODI PUBBLICI ==
    /**
     * Gestisce tutta la sessione di gioco ripetendo il ciclo di gioco finché l'utente non decide
     * di terminare la sessione
     */
    public void startGame() {
        // ciclo del gioco finché non si decide di terminare la sessione
        do {
            model.resetGame();  // resetta lo stato del gioco allo stato iniziale (necessario in caso di nuova partita)
            playSingleGame();
        } while (view.askPlayAgain());
        view.close();
    }

    /**
     * Gestisce una singola partita
     */
    public void playSingleGame() {
        model.setupGame();
        view.showSetup(model.getBriscola(), player1, player2);
        view.showBriscola(model.getBriscola());
        while (!model.isGameOver()) {
            playTurn();
        }
        endGame();
    }

    // == METODI HELPER ==
    
    /**
     * Gestisce il processo di giocata di una carta da parte di un giocatore.
     * Se il giocatore è una CPU, la carta viene scelta automaticamente,
     * altrimenti viene richiesta all'utente attraverso la view.
     *
     * @param player il giocatore che deve giocare la carta
     */
    private void playCard(Player player) {
        // Giocatore sceglie la carta (il modo dipende se player è Cpu o umano)
        Card card = (player instanceof Cpu)
                ? ((Cpu) player).chooseCard(model)
                : view.requestCard(player);
        // Giocatore gioca la carta
        model.playCard(player, card);
        view.showPlayedCard(player, card);
    }

    
    /**
     * Gestisce un singolo turno di gioco.
     * I giocatori giocano una carta > Si valuta la mano di gioco > I giocatori pescano una carta
     */
    private void playTurn() {
        Table table = model.getTable();

        // Primo giocatore gioca la carta e aggiorna il model e la view
        playCard(table.getFirstPlayer());
        // Secondo giocatore gioca la carta e aggiorna il model e la view
        playCard(table.getSecondPlayer());

        // Si valuta la mano di gioco
        model.evaluateHand();
        view.showHandResult(table.getWinner(), table.getPointsWon());
        table.clear();

        // Primo giocatore della mano successiva pesca una carta
        handleDraw(table.getFirstPlayer());
        // Secondo giocatore della mano successiva pesca una carta
        handleDraw(table.getSecondPlayer());
    }

    
    /**
     * Gestisce il processo di pesca di una carta per un giocatore
     *
     * @param p il giocatore che deve pescare la carta
     */
    private void handleDraw(Player p) {
        model.drawCard(p).ifPresent(c -> {
            view.showDraw(p, c);
            // Se rimane una carta nel deck avvisa utente che il prossimo è l'ultimo turno di pesca
            if (model.getDeck().size() == 1) view.showLastDrawingTurn();
            // Se la carta pescata è la briscola elimina la briscola dal tavolo
            else if (c.equals(model.getBriscola())) view.hideBriscola();
        });
        // Se il deck è vuoto elimina il deck dal tavolo 
        if (model.getDeck().isEmpty()) view.hideDeck();
    }


    /**
     * Gestisce la fine della partita mostrando i punteggi finali e il vincitore.
     */
    private void endGame() {
        Optional<Player> winner = model.getWinner();
        view.showFinalScores(player1, player2, player1.getPoints(), player2.getPoints());
        view.showWinner(winner);
    }


    /**
     * @param player
     * @param card
     */
    @Override
    public void onCardChosen(Player player, Card card) {
        // aggiorna model mettendo la carta nel table come prima o seconda (in base a posizione user)
        // Se è il primo giocatore aggiunge la carta al table, fa pescare il secondo, da i risultati e va al turno successivo
        // Se è il secondo giocatore aggiunge la carta al table, da i risultati e va al turno successivo
        // aggiorna view facendo vedere che la carta viene giocata
    }

//    playTurn() {
//        fa pescare il primo e il secondo giocatore
//                se il primo giocatore è la cpu gli fa scegliere e giocare la carta
//                ???
//    }

    /**
     *
     */
    @Override
    public void onNextTurn() {
        // aggiorna model andando al turno successivo (pesca oppure gioca oppure fine partita)
        // nella view non succede nulla
    }

    /**
     *
     */
    @Override
    public void onQuit() {
        // chiudi app
    }
}