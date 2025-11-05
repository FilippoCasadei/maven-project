package it.filippo.casadei.controller;

import javax.swing.Timer;   // per gestire i delay nell'interfaccia grafica

import it.filippo.casadei.model.*;
import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.*;
import it.filippo.casadei.model.player.cpu.Cpu;
import it.filippo.casadei.model.player.cpu.CpuDifficulty;
import it.filippo.casadei.model.player.cpu.GameContext;
import it.filippo.casadei.view.*;

/**
 * Classe Controller dell'archittetura MVC.
 * 
 * Controller del gioco Briscola che gestisce la logica del gioco e l'interazione
 * tra il modello (BriscolaGame) e la vista (BriscolaView).
 * Implementa una macchina a stati per gestire i vari momenti del gioco.
 */
public class BriscolaController implements BriscolaViewObserver {
    
    private final BriscolaView view;
    private BriscolaGame model;  // non final perché inizializzato dopo la scelta della difficoltà CPU
    private GameState currentState;

    private static final int DELAY_THINKING_CPU = 1000;
    private static final int DELAY_CLEAR_TABLE = 2000;
    
    // === COSTRUTTORE ===

    /**
     * Crea un nuovo controller per il gioco.
     */
    public BriscolaController() {
        this.view = new BriscolaViewImpl();
        this.view.setObserver(this);
        this.view.start();
        // Chiede la difficoltà della CPU per creare il model in createModel()
        chooseCpuDifficulty();
        // Avvia il gioco dopo che il model è stato creato
        startGame();
    }

    // == METODI PUBBLICI OBSERVER ==

    @Override
    public void humanPlaysCard(Player humanPlayer, Card cardChosen) {
        
        // Gioca la carta e aggiorna la view
        model.playCard(humanPlayer, cardChosen);
        view.disableCardSelection();
        view.showPlayedCard(humanPlayer, cardChosen);
        
        // Transizione allo stato successivo
        GameState nextState = (currentState == GameState.PLAYER1_TURN) 
            ? GameState.PLAYER2_TURN 
            : GameState.EVALUATING_HAND;
        transitionTo(nextState);
    }

    @Override
    public void createModel(CpuDifficulty difficulty) {
        Player humanPlayer = new Human("Utente");
        Player cpuPlayer = new Cpu("CPU", difficulty);
        this.model = new BriscolaGame(humanPlayer, cpuPlayer);
    }

    @Override
    public void restartGame(boolean playAgain) {

        // Se l'utente vuole giocare di nuovo, resetta il gioco e avvia una nuova partita
        if (playAgain) {
            model.resetGame();
            startGame();
        // Altrimenti termina applicazione
        } else {
            System.exit(0);  
        }
    }

    // == METODI PRIVATI ==

    /**
     * Avvia il gioco.
     */
    private void startGame() {
        model.setupGame();
        view.showSetup(model.getBriscola(), model.getPlayer1(), model.getPlayer2());
        
        // Stato di gioco iniziale dopo il setup
        currentState = GameState.PLAYER1_TURN;
        processCurrentState();
    }

    /*
     * Chiede alla view di scegliere la difficoltà della CPU. La view poi notifica
     * indietro per creare il model in base alla scelta fatta in createModel().
     */
    private void chooseCpuDifficulty() {
        view.chooseCpuDifficulty();
    }

    /*
     * Processa lo stato corrente del gioco e invoca la logica appropriata.
     */
    private void processCurrentState() {
        switch (currentState) {
            case PLAYER1_TURN:
                handlePlayerTurn(model.getTable().getFirstPlayer(), GameState.PLAYER2_TURN);
                break;
                
            case PLAYER2_TURN:
                handlePlayerTurn(model.getTable().getSecondPlayer(), GameState.EVALUATING_HAND);
                break;
                
            case EVALUATING_HAND:
                evaluateHand();
                break;
                
            case DRAWING_CARDS:
                handleDrawing();
                break;
                
            case GAME_OVER:
                endGame();
                break;
            
            default:
                throw new IllegalStateException("Stato di gioco invalido: " + currentState);
        }
    }
    
    /*
     * Gestisce il turno di un giocatore, sia umano che CPU.
     * 
     * @param player il giocatore di cui è il turno
     * @param nextState lo stato successivo dopo il turno del giocatore
     */
    private void handlePlayerTurn(Player player, GameState nextState) {
        // Turno della CPU
        if (player instanceof Cpu) {
            // delay per simulare il pensiero della CPU
            Timer timer = new Timer(DELAY_THINKING_CPU, e -> {
                cpuPlaysCard((Cpu) player);
                transitionTo(nextState);
            });
            timer.setRepeats(false);
            timer.start();
        // Turno dell'utente
        } else {
            // abilita selezione carta nella view e aspetta l'interazione dell'utente
            view.enableCardSelection(player);
            // Il flusso riprenderà in humanPlaysCard()
        }
    }
    
    /*
     * Gestisce la logica per far giocare una carta alla CPU.
     * 
     * @param cpu la CPU che deve giocare una carta
     */
    private void cpuPlaysCard(Cpu cpu) {
        GameContext context = createGameContext(cpu);
        Card chosen = cpu.chooseCard(context);
        
        model.playCard(cpu, chosen);
        view.showPlayedCard(cpu, chosen);
    }
    
    /*
     * Valuta la mano giocata, determina il vincitore e aggiorna i punteggi.
     */
    private void evaluateHand() {
        model.evaluateHand();
        
        // Pausa per lasciare sul tavolo le carte per un po' prima di pulire
        Timer timer = new Timer(DELAY_CLEAR_TABLE, e -> {  
            model.getTable().clear();
            view.clearTable();
            transitionTo(GameState.DRAWING_CARDS);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /*
     * Gestisce la fase di pesca delle carte dopo la valutazione della mano di gioco.
     */
    private void handleDrawing() {
        Player firstPlayer = model.getTable().getFirstPlayer();
        Player secondPlayer = model.getTable().getSecondPlayer();
        
        drawCardFor(firstPlayer);
        drawCardFor(secondPlayer);
        
        // Controlla se il gioco è finito, altrimenti ricomincia un nuovo turno
        if (model.isGameOver()) {
            transitionTo(GameState.GAME_OVER);
        } else {
            transitionTo(GameState.PLAYER1_TURN);
        }
    }
    
    /*
     * Permette a un giocatore di pescare una carta (se presente) e aggiorna la vista di conseguenza.
     * 
     * @param player il giocatore che deve pescare una carta
     */
    private void drawCardFor(Player player) {
        model.drawCard(player).ifPresent(card -> {
            view.showDraw(player, card);
            
            // se la carta pescata è la briscola, togli la briscola dalla view
            if (card.equals(model.getBriscola())) {
                view.hideBriscola();
            }
            
            // se la carta pescata è l'ultima del mazzo, avvisa la view 
            if (model.getDeck().size() == 1) {
                view.showLastDrawingTurn();
            }
        });

        // se il mazzo è vuoto, togli il mazzo dalla view
        if (model.getDeck().isEmpty()) {
            view.hideDeck();
        }
    }
    
    /*
     * Gestisce la fine del gioco, mostra i risultati finali e chiede se l'utente vuole 
     * giocare di nuovo.
     */
    private void endGame() {
        Player p1 = model.getPlayer1();
        Player p2 = model.getPlayer2();
        
        view.showFinalScores(p1, p2, p1.getPoints(), p2.getPoints());
        view.showWinner(model.getWinner());
        
        // Chiede se l'utente vuole giocare di nuovo. View notifica indietro tramite restartGame()
        view.askPlayAgain();
    }
    
    /*
     * Gestisce la transizione a un nuovo stato di gioco.
     * 
     * @param newState il nuovo stato di gioco
     */
    private void transitionTo(GameState newState) {
        currentState = newState;
        processCurrentState();
    }
    
    /*
     * Crea il contesto di gioco per il giocatore specificato.
     */
    private GameContext createGameContext(Player player) {
        return new GameContext(
            player.getHand(),
            model.getTable(),
            model.getBriscola(),
            model.getTable().getFirstPlayer().equals(player),
            model.getDeck().size() == 1
        );
    }
}
