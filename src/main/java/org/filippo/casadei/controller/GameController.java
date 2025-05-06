package org.filippo.casadei.controller;

import org.filippo.casadei.model.*;
import org.filippo.casadei.view.*;

import java.util.*;

/**
 * Controller per gestire il flusso di una partita di Briscola 1vs1.
 * Si appoggia a un'interfaccia GameView per tutte le interazioni con l'utente (console, GUI, ecc.).
 */

public class GameController {
    private final BriscolaGame model;
    private final GameView view;
    private final Player player1;
    private final Player player2;
    private final Card briscolaCard;
    private final Suit briscolaSuit;
    private Player currentFirstPlayer;
    private boolean isBriscolaDrawn = false;

    public GameController(GameView view, Player player1, Player player2) {
        this.view = view;
        this.player1 = player1;
        this.player2 = player2;
        Deck deck = Deck.createDeck();
        Table table = new Table();
        this.model = new BriscolaGame(player1, player2, Deck.createDeck(), table);

        // Mescola e distribuisce inizialmente 3 carte a ciascun giocatore
        setupGame();

        // Determina la briscola estraendo la prossima carta dal mazzo
        this.briscolaCard = deck.draw();
        this.briscolaSuit = briscolaCard.getSuit();
        model.setBriscola(briscolaCard);
        view.showBriscola(briscolaCard);

        this.currentFirstPlayer = player1; // Imposta player1 come primo iniziale

        if (view instanceof GuiGameView gui) {
            gui.start(this);
        } else {
            startGame();
        }
    }

    private void setupGame() {
        Deck deck = model.getDeck();
        deck.shuffle();
        for (int i = 0; i < Hand.MAX_CARDS_IN_HAND; i++) {
            player1.addCardToHand(deck.draw());
            player2.addCardToHand(deck.draw());
        }
    }

    public void startGame() {
        while (!isGameOver()) {
            playTurn();
            evaluateHand();
            drawCards();
            model.getTable().clear();
        }
        endGame();
    }

    private void playTurn() {
        Player second = model.getOpponent(currentFirstPlayer);

        Card card1 = (currentFirstPlayer instanceof Cpu)
                ? ((Cpu) currentFirstPlayer).chooseCard(model)
                : view.requestCard(currentFirstPlayer);

        Card card2 = (second instanceof Cpu)
                ? ((Cpu) second).chooseCard(model)
                : view.requestCard(second);

        currentFirstPlayer.playCard(card1);
        second.playCard(card2);
        model.registerPlayedCard(card1);
        model.registerPlayedCard(card2);
        model.getTable().playCard(currentFirstPlayer, card1);
        model.getTable().playCard(second, card2);

        view.showPlayedCard(currentFirstPlayer, card1);
        view.showPlayedCard(second, card2);
    }

    private void evaluateHand() {
        Table table = model.getTable();

        // Prende la prima e seconda carta giocata
        Card first = table.getCardPlayedBy(currentFirstPlayer);
        Player second = model.getOpponent(currentFirstPlayer);
        Card secondCard = table.getCardPlayedBy(second);

        // Determina il vincitore della mano di gioco
        int winIdx = GameRules.determineWinner(first, secondCard, briscolaSuit);
        Player winner = (winIdx == 0) ? currentFirstPlayer : second;

        // Aggiunge i punti al giocatore che ha vinto la mano di gioco
        int points = GameRules.calculatePointsWon(first, secondCard);
        winner.addPoints(points);

        // Il vincitore gioca per primo il turno successivo
        currentFirstPlayer = winner;
        view.showHandResult(table.getPlayOrder(), winner, points);
    }

    private void drawCards() {
        Deck deck = model.getDeck();
        Player[] order = {currentFirstPlayer, model.getOpponent(currentFirstPlayer)};

        for (Player p : order) {
            if (!deck.isEmpty()) {
                Card drawn = deck.draw();
                p.addCardToHand(drawn);
                view.showDraw(p, drawn);
            } else if (!isBriscolaDrawn) {
                isBriscolaDrawn = true;
                p.addCardToHand(briscolaCard);
                view.showDraw(p, briscolaCard);
            }
        }
    }

    private boolean isGameOver() {
        return model.getDeck().isEmpty()
                && player1.getHand().isEmpty()
                && player2.getHand().isEmpty();
    }

    private void endGame() {
        Map<Player, Integer> scores = new LinkedHashMap<>();
        scores.put(player1, player1.getPoints());
        scores.put(player2, player2.getPoints());

        view.showFinalScores(scores);
        Player champion = (player1.getPoints() > player2.getPoints()) ? player1 : player2;
        view.showWinner(champion);
    }
}
