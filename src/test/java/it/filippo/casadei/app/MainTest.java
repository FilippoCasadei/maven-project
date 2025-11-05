package it.filippo.casadei.app;

import it.filippo.casadei.model.*;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.*;
import it.filippo.casadei.model.player.cpu.*;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testa le funzionalità core del gioco della Briscola.
 */
public class MainTest {

    private BriscolaGame model;
    private Human player1;
    private Cpu player2;

    @BeforeEach
    void setUp() {
        // Inizializza i giocatori (assumo costruttore con nome)
        player1 = new Human("Giocatore1");
        player2 = new Cpu("Cpu", new HardDifficulty());
        model = new BriscolaGame(player1, player2);
    }

    @AfterEach
    void reset() {
        model = null;
        player1 = null;
        player2 = null;
    }

    // === TEST INIZIALIZZAZIONE GIOCO ===

    @Test
    @DisplayName("Setup gioco: mazzo popolato correttamente")
    void testSetupGamePopulatesDeck() {
        model.setupGame();

        // Il mazzo dovrebbe avere 33 carte (40 - 6 distribuite - 1 briscola pescata)
        int expectedCardsInDeck = 40 - (Hand.MAX_CARDS_IN_HAND * 2) - 1;
        assertEquals(expectedCardsInDeck, model.getDeck().size(),
                "Il mazzo dovrebbe contenere 33 carte dopo il setup");
    }

    @Test
    @DisplayName("Setup gioco: giocatori ricevono 3 carte ciascuno")
    void testSetupGameDistributesCards() {
        model.setupGame();

        assertEquals(Hand.MAX_CARDS_IN_HAND, player1.getHand().getCards().size(),
                "Player1 dovrebbe avere 3 carte in mano");
        assertEquals(Hand.MAX_CARDS_IN_HAND, player2.getHand().getCards().size(),
                "Player2 dovrebbe avere 3 carte in mano");
    }

    @Test
    @DisplayName("Setup gioco: briscola pescata correttamente")
    void testSetupGameDrawsTrumpCard() {
        model.setupGame();

        assertNotNull(model.getBriscola(), "La briscola dovrebbe essere pescata");
    }

    @Test
    @DisplayName("Setup gioco: ordine giocatori impostato sul tavolo")
    void testSetupGameSetsPlayersOrder() {
        model.setupGame();

        assertNotNull(model.getTable().getFirstPlayer(),
                "Il primo giocatore dovrebbe essere impostato");
        assertNotNull(model.getTable().getSecondPlayer(),
                "Il secondo giocatore dovrebbe essere impostato");
    }

    // === TEST GIOCO CARTE ===

    @Test
    @DisplayName("Gioca carta: carta rimossa dalla mano del giocatore")
    void testPlayCardRemovesCardFromHand() {
        model.setupGame();
        Card cardToPlay = player1.getHand().getCards().get(0);
        int initialHandSize = player1.getHand().getCards().size();

        model.playCard(player1, cardToPlay);

        assertEquals(initialHandSize - 1, player1.getHand().getCards().size(),
                "La mano dovrebbe avere una carta in meno");
        assertFalse(player1.getHand().getCards().contains(cardToPlay),
                "La carta giocata non dovrebbe essere più nella mano");
    }

    @Test
    @DisplayName("Gioca carta: carta posizionata sul tavolo")
    void testPlayCardPlacesCardOnTable() {
        model.setupGame();
        Card cardToPlay = player1.getHand().getCards().get(0);

        model.playCard(player1, cardToPlay);

        assertEquals(cardToPlay, model.getTable().getFirstCard(),
                "La carta dovrebbe essere sul tavolo");
    }

    @Test
    @DisplayName("Gioca carta: entrambi i giocatori giocano correttamente")
    void testPlayCardBothPlayers() {
        model.setupGame();
        Card card1 = player1.getHand().getCards().get(0);
        Card card2 = player2.getHand().getCards().get(0);

        model.playCard(player1, card1);
        model.playCard(player2, card2);

        assertNotNull(model.getTable().getFirstCard(),
                "Prima carta dovrebbe essere sul tavolo");
        assertNotNull(model.getTable().getSecondCard(),
                "Seconda carta dovrebbe essere sul tavolo");
    }

    // === TEST SCELTA CARTA CPU ===

    @Test
    @DisplayName("Scelta carta CPU Easy: carta scelta è valida")
    void testCpuEasyChooseCardReturnsValidCard() {
        Cpu cpuEasy = new Cpu("Cpu", new EasyDifficulty());
        model = new BriscolaGame(player1, cpuEasy);
        model.setupGame();
        GameContext context = new GameContext(cpuEasy.getHand(),
            model.getTable(),
             model.getBriscola(),
             true,
             model.getDeck().size() == 1
        );

        Card chosenCard = cpuEasy.chooseCard(context);

        assertTrue(cpuEasy.getHand().getCards().contains(chosenCard),
                "La CPU a livello easy dovrebbe raggiungere sempre una carta scelta " +
                "e la carta  dovrebbe essere nella sua mano");
    }

    @Test
    @DisplayName("Scelta carta CPU Medium: carta scelta è valida")
    void testCpuMediumChooseCardReturnsValidCard() {
        Cpu cpuMedium = new Cpu("Cpu", new MediumDifficulty());
        model = new BriscolaGame(player1, cpuMedium);
        model.setupGame();
        GameContext context = new GameContext(cpuMedium.getHand(),
            model.getTable(),
             model.getBriscola(),
             true,
             model.getDeck().size() == 1
        );

        Card chosenCard = cpuMedium.chooseCard(context);

        assertTrue(cpuMedium.getHand().getCards().contains(chosenCard),
                "La CPU a livello medium dovrebbe raggiungere sempre una carta scelta " +
                "e la carta  dovrebbe essere nella sua mano");
    }

    @Test
    @DisplayName("Scelta carta CPU Easy: carta scelta è valida")
    void testCpuHardChooseCardReturnsValidCard() {
        model.setupGame();  // la cpu nel setup è hard
        Cpu cpuHard = player2;
        GameContext context = new GameContext(cpuHard.getHand(),
            model.getTable(),
             model.getBriscola(),
             true,
             model.getDeck().size() == 1
        );

        Card chosenCard = cpuHard.chooseCard(context);

        assertTrue(cpuHard.getHand().getCards().contains(chosenCard),
                "La CPU a livello hard dovrebbe raggiungere sempre una carta scelta " +
                "e la carta  dovrebbe essere nella sua mano");
    }
    

    // === TEST VALUTAZIONE MANO ===

    @Test
    @DisplayName("Valuta mano: vincitore determinato correttamente")
    void testEvaluateHandDeterminesWinner() {
        model.setupGame();

        // Gioca due carte
        Card card1 = player1.getHand().getCards().get(0);
        Card card2 = player2.getHand().getCards().get(0);
        model.playCard(player1, card1);
        model.playCard(player2, card2);

        model.evaluateHand();

        assertNotNull(model.getTable().getWinner(),
                "Dovrebbe esserci un vincitore della mano");
        assertTrue(model.getTable().getWinner().equals(player1) ||
                        model.getTable().getWinner().equals(player2),
                "Il vincitore deve essere uno dei due giocatori");
    }

    @Test
    @DisplayName("Valuta mano: punti assegnati correttamente")
    void testEvaluateHandAssignsPoints() {
        model.setupGame();

        Card card1 = player1.getHand().getCards().get(0);
        Card card2 = player2.getHand().getCards().get(0);
        model.playCard(player1, card1);
        model.playCard(player2, card2);

        int totalPointsBefore = player1.getPoints() + player2.getPoints();
        model.evaluateHand();
        int totalPointsAfter = player1.getPoints() + player2.getPoints();

        assertTrue(totalPointsAfter >= totalPointsBefore,
                "I punti totali dovrebbero aumentare o rimanere uguali");
    }

    @Test
    @DisplayName("Valuta mano: ordine giocatori aggiornato")
    void testEvaluateHandUpdatesPlayersOrder() {
        model.setupGame();

        Card card1 = player1.getHand().getCards().get(0);
        Card card2 = player2.getHand().getCards().get(0);
        model.playCard(player1, card1);
        model.playCard(player2, card2);

        model.evaluateHand();
        Player winner = model.getTable().getWinner();

        assertEquals(winner, model.getTable().getFirstPlayer(),
                "Il vincitore dovrebbe giocare per primo nel turno successivo");
    }

    // === TEST PESCA CARTE ===

    @Test
    @DisplayName("Pesca carta: carta pescata dal mazzo quando disponibile")
    void testDrawCardFromDeck() {
        model.setupGame();
        int initialDeckSize = model.getDeck().size();
        int initialHandSize = player1.getHand().getCards().size();

        player1.playCard(player1.getHand().getCards().get(0));  // Gioca una carta per permettere la pesca
        Optional<Card> drawn = model.drawCard(player1);

        assertTrue(drawn.isPresent(), "Dovrebbe essere pescata una carta");
        assertEquals(initialDeckSize - 1, model.getDeck().size(),
                "Il mazzo dovrebbe avere una carta in meno");
        assertEquals(initialHandSize, player1.getHand().getCards().size(),
                "La mano dovrebbe avere lo stesso numero di carte dopo aver giocato e pescato");
    }

    @Test
    @DisplayName("Pesca carta: briscola pescata quando mazzo vuoto")
    void testDrawCardDrawsTrumpWhenDeckEmpty() {
        model.setupGame();
        Card briscola = model.getBriscola();

        // Svuota il mazzo
        while (!model.getDeck().isEmpty()) {
            model.getDeck().draw();
        }

        player1.playCard(player1.getHand().getCards().get(0));  // Gioca una carta per permettere la pesca
        Optional<Card> drawn = model.drawCard(player1);

        assertTrue(drawn.isPresent(), "La briscola dovrebbe essere pescata");
        assertEquals(briscola, drawn.get(),
                "La carta pescata dovrebbe essere la briscola");
    }

    @Test
    @DisplayName("Pesca carta: nessuna carta pescata se mazzo vuoto e briscola già pescata")
    void testDrawCardReturnsEmptyWhenNoCardsAvailable() {
        model.setupGame();

        // Svuota il mazzo
        while (!model.getDeck().isEmpty()) {
            model.getDeck().draw();
        }

        player1.playCard(player1.getHand().getCards().get(0));  // Gioca una carta per permettere la pesca
        // Pesca la briscola
        model.drawCard(player1);

        // Tentativo di pescare ancora
        Optional<Card> drawn = model.drawCard(player2);

        assertFalse(drawn.isPresent(),
                "Non dovrebbe essere possibile pescare se mazzo è vuoto e briscola già pescata");
    }


    // === TEST FINE GIOCO ===

    @Test
    @DisplayName("Fine gioco: non terminato all'inizio")
    void testIsGameOverReturnsFalseAtStart() {
        model.setupGame();

        assertFalse(model.isGameOver(),
                "Il gioco non dovrebbe essere terminato all'inizio");
    }

    @Test
    @DisplayName("Fine gioco: terminato quando mazzo e mani vuoti")
    void testIsGameOverReturnsTrueWhenAllCardsPlayed() {
        model.setupGame();

        // Svuota il mazzo
        while (!model.getDeck().isEmpty()) {
            model.getDeck().draw();
        }

        player1.playCard(player1.getHand().getCards().get(0));
        // Pesca la briscola
        model.drawCard(player1);

        // Svuota le mani
        player1.getHand().clear();
        player2.getHand().clear();

        assertTrue(model.isGameOver(),
                "Il gioco dovrebbe essere terminato quando tutto è vuoto");
    }

    @Test
    @DisplayName("Fine gioco: non terminato se mani ancora piene")
    void testIsGameOverReturnsFalseWithCardsInHand() {
        model.setupGame();

        // Svuota solo il mazzo
        while (!model.getDeck().isEmpty()) {
            model.getDeck().draw();
        }

        player1.playCard(player1.getHand().getCards().get(0));
        model.drawCard(player1); // pesca briscola

        assertFalse(model.isGameOver(),
                "Il gioco non dovrebbe essere terminato se ci sono carte in mano");
    }

    // === TEST VINCITORE ===

    @Test
    @DisplayName("Vincitore: player1 vince con più punti")
    void testGetWinnerPlayer1Wins() {
        model.setupGame();

        player1.addPoints(80);
        player2.addPoints(40);

        Optional<Player> winner = model.getWinner();

        assertTrue(winner.isPresent(), "Dovrebbe esserci un vincitore");
        assertEquals(player1, winner.get(), "Player1 dovrebbe vincere");
    }

    @Test
    @DisplayName("Vincitore: player2 vince con più punti")
    void testGetWinnerPlayer2Wins() {
        model.setupGame();

        player1.addPoints(40);
        player2.addPoints(80);

        Optional<Player> winner = model.getWinner();

        assertTrue(winner.isPresent(), "Dovrebbe esserci un vincitore");
        assertEquals(player2, winner.get(), "Player2 dovrebbe vincere");
    }

    @Test
    @DisplayName("Vincitore: pareggio con punti uguali")
    void testGetWinnerReturnsEmptyOnDraw() {
        model.setupGame();

        player1.addPoints(BriscolaGame.HALF_TOTAL_POINTS);
        player2.addPoints(BriscolaGame.HALF_TOTAL_POINTS);

        Optional<Player> winner = model.getWinner();

        assertFalse(winner.isPresent(),
                "Non dovrebbe esserci un vincitore in caso di pareggio");
    }

    // === TEST RESET GIOCO ===

    @Test
    @DisplayName("Reset gioco: tavolo pulito")
    void testResetGameClearsTable() {
        model.setupGame();

        // Gioca alcune carte
        Card card1 = player1.getHand().getCards().get(0);
        Card card2 = player2.getHand().getCards().get(0);
        model.playCard(player1, card1);
        model.playCard(player2, card2);

        model.resetGame();

        assertNull(model.getTable().getFirstCard(),
                "Il tavolo dovrebbe essere pulito dopo il reset");
        assertNull(model.getTable().getSecondCard(),
                "Il tavolo dovrebbe essere pulito dopo il reset");
    }

    @Test
    @DisplayName("Reset gioco: mani svuotate")
    void testResetGameClearsHands() {
        model.setupGame();

        model.resetGame();

        assertTrue(player1.getHand().isEmpty(),
                "La mano di player1 dovrebbe essere vuota dopo il reset");
        assertTrue(player2.getHand().isEmpty(),
                "La mano di player2 dovrebbe essere vuota dopo il reset");
    }

    @Test
    @DisplayName("Reset gioco: punti azzerati")
    void testResetGameResetsPoints() {
        model.setupGame();

        player1.addPoints(50);
        player2.addPoints(30);

        model.resetGame();

        assertEquals(0, player1.getPoints(),
                "I punti di player1 dovrebbero essere azzerati");
        assertEquals(0, player2.getPoints(),
                "I punti di player2 dovrebbero essere azzerati");
    }

    @Test
    @DisplayName("Reset gioco: briscola resettata")
    void testResetGameResetsTrump() {
        model.setupGame();

        model.resetGame();

        assertNull(model.getBriscola(),
                "La briscola dovrebbe essere null dopo il reset");
    }

    // === TEST TURNO (SIMULAZIONE PARTITA COMPLETA) ===

    @Test
    @DisplayName("Turno: simulazione di una mano completa")
    void testFullHandSimulation() {
        model.setupGame();

        // Gioca una mano completa
        Card card1 = player1.getHand().getCards().get(0);
        Card card2 = player2.getHand().getCards().get(0);

        model.playCard(player1, card1);
        model.playCard(player2, card2);
        model.evaluateHand();

        // Entrambi pescano
        model.drawCard(model.getTable().getFirstPlayer());
        model.drawCard(model.getTable().getSecondPlayer());

        // Verifica stato coerente
        assertEquals(Hand.MAX_CARDS_IN_HAND, player1.getHand().getCards().size(),
                "Player1 dovrebbe avere 3 carte dopo aver pescato");
        assertEquals(Hand.MAX_CARDS_IN_HAND, player2.getHand().getCards().size(),
                "Player2 dovrebbe avere 3 carte dopo aver pescato");
        assertNotNull(model.getTable().getWinner(),
                "Dovrebbe esserci un vincitore della mano");
    }


    // === TEST CASI LIMITE ===

    @Test
    @DisplayName("Caso limite: gioco con entrambi CPU")
    void testGameWithBothCpuPlayers() {
        Player cpu1 = new Cpu("CPU1", new EasyDifficulty());
        Player cpu2 = new Cpu("CPU2", new HardDifficulty());
        BriscolaGame cpuGame = new BriscolaGame(cpu1, cpu2);

        assertDoesNotThrow(() -> cpuGame.setupGame(),
                "Setup con due CPU non dovrebbe lanciare eccezioni");
    }

    @Test
    @DisplayName("Edge case: valutazione senza carte sul tavolo dovrebbe fallire")
    void testEvaluateHandWithoutCardsOnTableShouldFail() {
        model.setupGame();

        // La valutazione di una mano di gioco senza aver giocato carte
        // dovrebbe lanciare un'eccezione
        assertThrows(Exception.class, () -> model.evaluateHand(),
                "Valutare senza carte sul tavolo dovrebbe lanciare eccezione");
    }
}