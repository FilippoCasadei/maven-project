package it.filippo.casadei.model;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class CpuTest {

    private BriscolaGame game;
    private Cpu cpu;

    @BeforeEach
    public void setUp() {
        // Creo 2 giocatori: uno umano (mock o dummy) e una CPU
        Player human = new HumanPlayer("Umano");
        cpu = new Cpu("CPU", new EasyStrategy()); // o EasyCpuStrategy, se è lì che stai testando la logica
        Deck deck = Deck.createDeck();  // o Deck.createDeck() se necessario
        Table table = new Table();

        game = new BriscolaGame(human, cpu, deck, table);

        // Imposta la briscola manualmente
        game.setBriscola(new Card(Suit.COINS, Rank.KING));  // ad esempio DENARI
    }

    @org.junit.jupiter.api.Test
    public void testChoosesBriscolaWithHighestPoints() {

        cpu.addCardToHand(new Card(Suit.COINS, Rank.KNIGHT)); // 3 punti
        cpu.addCardToHand(new Card(Suit.COINS, Rank.KNAVE));   // 2 punti
        cpu.addCardToHand(new Card(Suit.CUPS, Rank.KING));        // 4 punti ma non briscola

        Card chosen = cpu.chooseCard(game);

        assertEquals(Suit.COINS, chosen.getSuit());
        assertEquals(Rank.KNIGHT, chosen.getRank()); // 3 punti, la briscola con più punti
    }

    @org.junit.jupiter.api.Test
    public void testChoosesBriscolaWithHigherRankWhenPointsEqual() {

        cpu.addCardToHand(new Card(Suit.COINS, Rank.KNAVE));        // 2 punti, ordinal = 0
        cpu.addCardToHand(new Card(Suit.COINS, Rank.KNIGHT)); // 2 punti, ordinal = 1 (più alto)
        cpu.addCardToHand(new Card(Suit.SWORDS, Rank.KING));        // 4 punti ma non briscola

        Card chosen = cpu.chooseCard(game);

        assertEquals(Suit.COINS, chosen.getSuit());
        assertEquals(Rank.KNIGHT, chosen.getRank()); // stessa priorità ma rank maggiore
    }

    // Test da rifare
    /*@org.junit.jupiter.api.Test
    public void testChoosesHighestPointCardWhenNoBriscola() {

        cpu.addCardToHand(new Card(Suit.SWORDS, Rank.KNAVE));   // 2 punti
        cpu.addCardToHand(new Card(Suit.CUPS, Rank.KING));      // 4 punti
        cpu.addCardToHand(new Card(Suit.BATONS, Rank.SEVEN));  // 0 punti

        Card chosen = cpu.chooseCard(game);

        assertEquals(Suit.CUPS, chosen.getSuit());
        assertEquals(Rank.KING, chosen.getRank()); // 4 punti, nessuna briscola
    }
    */

}
