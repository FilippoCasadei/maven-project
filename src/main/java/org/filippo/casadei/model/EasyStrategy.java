package org.filippo.casadei.model;

public class EasyStrategy implements CpuStrategy{

// TODO: Potrei mettere che sceglie sempre la carta più forte

    @Override
    public Card chooseCard(Cpu cpuPlayer, BriscolaGame game) {
        return cpuPlayer.getHand().getCards().getFirst();
    }
}
