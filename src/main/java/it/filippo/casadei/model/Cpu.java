package it.filippo.casadei.model;

public class Cpu extends Player{

    private CpuDifficulty difficulty;

    // == COSTRUTTORE ==
    public Cpu(String name) {
        super(name);
    }

    // == METODI PUBBLICI ==
    // Sceglie la carta in base alla difficoltà (strategy) impostata
    public Card chooseCard(BriscolaGame game) {
        return difficulty.chooseCard(this, game);
    }

    // == GETTER E SETTER ==
    public CpuDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(CpuDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
