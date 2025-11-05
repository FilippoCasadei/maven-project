package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.Table;
import it.filippo.casadei.model.card.Suit;
import it.filippo.casadei.model.player.Hand;

/**
 * Contiene tutte le informazioni necessarie per permettere alla CPU
 * di scegliere la carta da giocare in base allo stato attuale del gioco.
 */
public class GameContext {

    private final Hand cpuHand;
    private final Table table;
    private final Card briscolaCard;
    private final Suit briscolaSuit;
    private final boolean isCpuFirst;
    private final boolean isLastDraw;

    // == COSTRUTTORE ==
    /**
     * Costruisce un nuovo contesto di gioco per la CPU.
     *
     * @param cpuHand la mano della CPU
     * @param table   il tavolo di gioco corrente
     * @param briscolaCard la carta di briscola corrente
     * @param isCpuFirst vero se la cpu gioca per prima
     * @param isLastDraw vero se è rimasta l'ultima pescata
     */
    public GameContext(Hand cpuHand, Table table, Card briscolaCard, boolean isCpuFirst, boolean isLastDraw) {
        this.cpuHand = cpuHand;
        this.table = table;
        this.briscolaCard = briscolaCard;
        this.briscolaSuit = briscolaCard.getSuit();
        this.isCpuFirst = isCpuFirst;
        this.isLastDraw = isLastDraw;
    }

    // == GETTER E SETTER ==

    public Hand getCpuHand() {
        return cpuHand;
    }

    public Table getTable() {
        return table;
    }

    public Card getBriscolaCard() {
        return briscolaCard;
    }

    public Suit getBriscolaSuit() {
        return briscolaSuit;
    }

    public boolean isCpuFirst() {
        return isCpuFirst;
    }

    public boolean isLastDraw() {
        return isLastDraw;
    }
}

