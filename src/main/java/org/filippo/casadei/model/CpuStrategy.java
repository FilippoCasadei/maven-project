package org.filippo.casadei.model;

public interface CpuStrategy {

    // TODO: PER ORA NON MI INTERESSA IL CONTESTO (GIOCA CARTE A CASO)
    Card chooseCard(Cpu cpu);

}
