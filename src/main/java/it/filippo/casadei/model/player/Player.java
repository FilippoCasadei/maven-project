package it.filippo.casadei.model.player;

import it.filippo.casadei.model.card.Card;

/**
 * Rappresenta un giocatore astratto nel gioco della Briscola.
 * Ogni giocatore ha un nome, una mano di carte e un punteggio.
 * Questa classe fornisce le funzionalità di base comuni a tutti i tipi di giocatori (umano e cpu).
 */
public abstract class Player {
    private final String name;
    private final Hand hand;
    private int points;

    // === COSTRUTTORE ===

    /**
     * Crea un nuovo giocatore con il nome specificato.
     * Inizializza una mano vuota e imposta il punteggio a 0.
     *
     * @param name il nome del giocatore
     */
    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.points = 0;
    }

    // == METODI PUBBLICI ==

    /**
     * Aggiunge una carta alla mano del giocatore.
     *
     * @param card la carta da aggiungere alla mano
     */
    public void addCardToHand(Card card) {
        hand.addCard(card);
    }

    /**
     * Gioca una carta rimuovendola dalla mano del giocatore.
     *
     * @param card la carta da giocare e rimuovere dalla mano
     */
    public void playCard(Card card) {
        hand.removeCard(card);
    }

    /**
     * Aggiunge punti al punteggio totale del giocatore.
     *
     * @param pointsToAdd i punti da aggiungere al punteggio attuale
     */
    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    /**
     * Azzera il punteggio del giocatore.
     */
    public void resetPoints() {
        this.points = 0;
    }

    // == GETTER E SETTER ==

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getPoints() {
        return points;
    }
}