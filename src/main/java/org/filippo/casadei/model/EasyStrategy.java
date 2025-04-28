package org.filippo.casadei.model;

public class EasyStrategy implements CpuStrategy{

// TODO: Potrei mettere che sceglie sempre la carta più forte

    // TODO: PER ORA NON MI INTERESSA IL CONTESTO (GIOCA CARTE A CASO)
    @Override
    public Card chooseCard(Cpu cpuPlayer) {
        return cpuPlayer.getHand().getCards().getFirst();
    }
}
