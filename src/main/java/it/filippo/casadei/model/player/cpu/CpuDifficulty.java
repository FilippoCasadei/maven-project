package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.card.Card;

/**
 * Interfaccia che definisce il livello di difficoltà della CPU nel gioco della Briscola.
 * Ogni implementazione di questa interfaccia rappresenta una diversa strategia di gioco
 * che la CPU può utilizzare per scegliere quale carta giocare.
 */
public interface CpuDifficulty {

    /**
     * Sceglie la carta da giocare in base al contesto corrente del gioco.
     *
     * @param context informazioni sullo stato del gioco (mano, tavolo, briscola)
     * @param memory la memoria della CPU fino a questo momento (è utilizzata solo dalla difficoltà Hard)
     * @return la carta scelta dalla CPU
     */
    Card chooseCard(GameContext context, Memory memory);
}