package it.filippo.casadei.app;

import it.filippo.casadei.controller.BriscolaController;

/*
 * Classe principale che avvia l'applicazione Briscola. 
 
 * Il programma permette all'utente di giocare a briscola contro una CPU di cui può
 * scegliere il livello di difficoltà.
 * Il gioco è implementato seguendo il pattern MVC (Model-View-Controller).
 */
public class Main {

    /*
     * Metodo principale che avvia l'applicazione Briscola.
     */
    public static void main(String[] args) {
        // Creazione del controller e avvio del gioco
        new BriscolaController();
    }
}