package it.filippo.casadei.model;

/**
 * Interfaccia che definisce il livello di difficoltà della CPU nel gioco della Briscola.
 * Ogni implementazione di questa interfaccia rappresenta una diversa strategia di gioco
 * che la CPU può utilizzare per scegliere quale carta giocare.
 */
public interface CpuDifficulty {

    /**
     * Sceglie una carta da giocare in base alla strategia implementata.
     *
     * @param cpu  il giocatore CPU che deve scegliere la carta
     * @param game il riferimento alla partita corrente
     * @return la carta scelta per essere giocata
     */
    Card chooseCard(Cpu cpu, BriscolaGame game);
}