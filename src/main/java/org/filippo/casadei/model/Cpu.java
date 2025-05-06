package org.filippo.casadei.model;

public class Cpu extends Player{

    private CpuStrategy strategy;

    public Cpu(String name, CpuStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    public void setStrategy(CpuStrategy strategy) {
        this.strategy = strategy;
    }

    // TODO: PER ORA NON MI INTERESSA IL CONTESTO (GIOCA CARTE A CASO)
    public Card chooseCard(BriscolaGame game) {
        // Usa la strategia configurata per selezionare una carta
        return strategy.chooseCard(this, game);
    }

}
