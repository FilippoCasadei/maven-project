package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.Player;

import java.util.List;

/**
 * Rappresenta un giocatore controllato dal computer nel gioco della Briscola.
 * La CPU può essere configurata con diversi livelli di difficoltà che influenzano
 * la strategia di gioco e la scelta delle carte.
 */
public class Cpu extends Player {

    private final CpuDifficulty difficulty;
    private final Memory memory;

    // == COSTRUTTORE ==
    /**
     * Costruisce un nuovo giocatore CPU con il nome specificato.
     *
     * @param name il nome da assegnare al giocatore CPU
     */
    public Cpu(String name, CpuDifficulty difficulty) {
        super(name);
        this.difficulty = difficulty;
        this.memory = new Memory();
    }

    // == METODI PUBBLICI ==
    /**
     * Seleziona una carta da giocare in base al livello di difficoltà impostato.
     * La strategia di scelta varia in base alla difficoltà della CPU.
     *
     * @param context contesto del gioco attuale
     * @return la carta scelta per essere giocata
     */
    public Card chooseCard(GameContext context) {
        return difficulty.chooseCard(context, memory); 
    }

    /**
     * Inizializza la memoria della CPU con il mazzo di carte completo (nessuna carta è uscita).
     *
     * @param deck il mazzo di carte completo all'inizio della partita
     */
    public void initializeMemory(List<Card> deck) {
        memory.initialize(deck);
    }

    /**
     * Aggiorna i punti nella memoria della CPU alla fine della mano di gioco.
     *
     * @param handPoints i punti totalizzati nella mano
     * @param playedCards le carte giocate nel turno
     * @param cpuWon true se la CPU ha vinto la mano, false altrimenti
     */
    public void updateAfterTurn(int handPoints, List<Card> playedCards, boolean cpuWon) {
        memory.updateAfterTurn(handPoints, playedCards, cpuWon);
    }
}
