package it.filippo.casadei.controller;

import it.filippo.casadei.model.*;
import it.filippo.casadei.view.BriscolaView;
import it.filippo.casadei.view.BriscolaViewObserver;
import it.filippo.casadei.view.GuiBriscolaViewImpl;


import java.util.*;

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

    public BriscolaController(BriscolaView view, Player player1, Player player2) {
        this.view = view;
        this.player1 = player1;
        this.player2 = player2;
        this.model = new BriscolaGame(player1, player2, Deck.createDeck(), new Table());

        // Sceglie quale view utilizzare in base all'istanza in runtime dell'interfaccia view
        view.start(this);  //TODO: decidere se usare o no observer
    }

    /** Gestisce tutta la sessione di gioco */
    public void startGame() {
        do {
            playSingleGame();
        } while (view.askPlayAgain());
    }

    /** Gestisce una singola partita */
    public void playSingleGame() {
        // TODO: AGGIUNGERE model.initialize() che inizializza il deck e il table per la nuova partita (forse basta solo il deck)
        model.setupGame();
        view.showSetup(model.getBriscola(), player1, player2);
        view.showBriscola(model.getBriscola());
        while (!model.isGameOver()) {
            playTurn();
        }
        endGame();
    }

    private void playCard(Player player) {
        // Giocatore sceglie la carta (il modo dipende se è Cpu o umano)
        Card card = (player instanceof Cpu)
                ? ((Cpu) player).chooseCard(model)
                : view.requestCard(player);
        // Giocatore gioca la carta e aggiorna il model e la view
        player.playCard(card);
        model.playCard(player, card);
        view.showPlayedCard(player, card);

    }
    private void playTurn() {
        Table table = model.getTable();

        // Primo giocatore gioca la carta e aggiorna il model e la view
        playCard(table.getFirstPlayer());
        // TODO: eventuale metodo per aggiornare view (le carte in mano alla mano del giocatore cambiano)
        // Secondo giocatore gioca la carta e aggiorna il model e la view
        playCard(table.getSecondPlayer());
        // TODO: eventuale metodo per aggiornare view

        // Si valuta la mano di gioco
        model.evaluateHand();
        view.showHandResult(table.getPlayOrder(), table.getWinner(), table.getPointsWon());
        table.clear();

        // Primo giocatore della mano successiva pesca una carta
        model.drawCard(table.getFirstPlayer())
                .ifPresent(c -> view.showDraw(table.getFirstPlayer(), c));
        // Secondo giocatore della mano successiva pesca una carta
        model.drawCard(table.getFirstPlayer())
                .ifPresent(c -> view.showDraw(table.getSecondPlayer(), c));
    }

    private void endGame() {
        Map<Player, Integer> scores = new LinkedHashMap<>();
        scores.put(player1, player1.getPoints());
        scores.put(player2, player2.getPoints());

        view.showFinalScores(scores);
        Player winner = (player1.getPoints() > player2.getPoints()) ? player1 : player2;
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

    public void playTurn1() {

    }
}

//private void playTurn() {
//    Table table = model.getTable();
//    Player firstPlayer = table.getFirstPlayer();
//    Player secondPlayer = table.getSecondPlayer();
//
//    // Primo giocatore sceglie la carta
//    Card card1 = (firstPlayer instanceof Cpu)
//            ? ((Cpu) firstPlayer).chooseCard(model)
//            : view.requestCard(firstPlayer);
//
//    // Primo giocatore gioca la carta e aggiorna il model e la view
//    firstPlayer.playCard(card1);
//    model.registerPlayedCard(card1);
//    table.playCard(firstPlayer, card1);
//    view.showPlayedCard(firstPlayer, card1);
//
//    // Secondo giocatore sceglie la carta
//    Card card2 = (secondPlayer instanceof Cpu)
//            ? ((Cpu) secondPlayer).chooseCard(model)
//            : view.requestCard(secondPlayer);
//
//    // Secondo giocatore gioca la carta e aggiorna il model e la view
//    secondPlayer.playCard(card2);
//    model.registerPlayedCard(card2);
//    table.playCard(secondPlayer, card2);
//    view.showPlayedCard(secondPlayer, card2);
//}

//public void startGame() {
//    while (!isGameOver()) {
//        playTurn();
//        evaluateHand();
//        drawCards();
//        model.getTable().clear();
//    }
//    endGame();
//}