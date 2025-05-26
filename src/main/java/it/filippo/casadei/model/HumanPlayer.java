package it.filippo.casadei.model;

/**
 * Rappresenta un giocatore umano nel gioco della Briscola.
 * Questa classe estende la classe astratta Player e viene utilizzata
 * per gestire le azioni di un giocatore reale durante la partita.
 */
public class HumanPlayer extends Player {

    // == COSTRUTTORE ==

    /**
     * Crea una nuova istanza di giocatore umano.
     *
     * @param name il nome del giocatore umano
     */
    public HumanPlayer(String name) {
        super(name);
    }
}