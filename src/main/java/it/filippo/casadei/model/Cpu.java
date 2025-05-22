package it.filippo.casadei.model;

public class Cpu extends Player{

    private CpuStrategy strategy;

    public Cpu(String name, CpuStrategy strategy) {
        super(name);
        this.strategy = strategy;
    }

    public CpuStrategy getStrategy() {
        return strategy;
    }

    // TODO: DEVO UTILIZZARE QUESTO METODO NEL CONTROLLER/MAIN
    public void setStrategy(CpuStrategy strategy) {
        this.strategy = strategy;
    }

    // Sceglie la carta in base alla difficoltà (strategy) impostata
    public Card chooseCard(BriscolaGame game) {
        return strategy.chooseCard(this, game);
    }
}
