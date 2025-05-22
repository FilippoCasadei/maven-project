package it.filippo.casadei;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.*;
import it.filippo.casadei.view.*;

public class Main {
    public static void main(String[] args) {


        /*
    SETUP GIOCATORI
    public void startGame(String difficulty) {
    Cpu cpu = new Cpu("CPU", null);

    cpu.setStrategy(DifficultyStrategyFactory.create(difficulty));

    Player human = new HumanPlayer("Giocatore");

    BriscolaGame game = new BriscolaGame(human, cpu, new Deck(), new Table());

    // poi lanci la partita
    }
         */
        CpuStrategy medium = new MediumStrategy();
        CpuStrategy hard = new HardStrategy();
        BriscolaView consoleView = new ConsoleBriscolaViewImpl();
        BriscolaView guiView = new GuiBriscolaViewImpl();
        Player p1 = new HumanPlayer("Giocatore1");
        Cpu p2 = new Cpu("Giocatore2", null);
        Cpu p3 = new Cpu("Giocatore3", null);
        p2.setStrategy(medium);
        p3.setStrategy(hard);
        BriscolaController gc = new BriscolaController(guiView, p1, p3);

        gc.startGame();
    }
}