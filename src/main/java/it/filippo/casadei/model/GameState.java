package it.filippo.casadei.model;

/**
 * Enum che rappresenta i possibili stati di una partita a Briscola.
 */
public enum GameState {
    SETUP,
    PLAYER1_TURN,
    PLAYER2_TURN,
    EVALUATING_HAND,
    DRAWING_CARDS,
    GAME_OVER
}
