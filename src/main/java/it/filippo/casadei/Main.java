package it.filippo.casadei;

import it.filippo.casadei.controller.GameController;
import it.filippo.casadei.model.*;
import it.filippo.casadei.view.ConsoleGameView;
import it.filippo.casadei.view.GuiGameView;
import it.filippo.casadei.view.GameView;

public class Main {
    public static void main(String[] args) {

        CpuStrategy difficulty = new MediumStrategy();
        GameView consoleView = new ConsoleGameView();
        GameView guiView = new GuiGameView();
        Player p1 = new HumanPlayer("Giocatore1");
        Player p2 = new Cpu("Giocatore2", difficulty);
        GameController gc = new GameController(consoleView, p1, p2);

        gc.startGame();
    }
}