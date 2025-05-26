package it.filippo.casadei.model;

/**
 * Rappresenta un giocatore controllato dal computer nel gioco della Briscola.
 * La CPU può essere configurata con diversi livelli di difficoltà che influenzano
 * la strategia di gioco e la scelta delle carte.
 */
public class Cpu extends Player{

    private CpuDifficulty difficulty;

    // == COSTRUTTORE ==
    /**
     * Costruisce un nuovo giocatore CPU con il nome specificato.
     *
     * @param name il nome da assegnare al giocatore CPU
     */
    public Cpu(String name) {
        super(name);
    }

    // == METODI PUBBLICI ==
    /**
     * Seleziona una carta da giocare in base al livello di difficoltà impostato.
     * La strategia di scelta varia in base alla difficoltà della CPU.
     *
     * @param game il riferimento alla partita corrente
     * @return la carta scelta per essere giocata
     */
    public Card chooseCard(BriscolaGame game) {
        return difficulty.chooseCard(this, game);
    }

    // == GETTER E SETTER ==
    /**
     * Restituisce il livello di difficoltà corrente della CPU.
     *
     * @return il livello di difficoltà attuale
     */
    public CpuDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Imposta il livello di difficoltà della CPU.
     *
     * @param difficulty il nuovo livello di difficoltà da impostare
     */
    public void setDifficulty(CpuDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
