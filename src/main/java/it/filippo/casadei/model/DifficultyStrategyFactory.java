package it.filippo.casadei.model;

public class DifficultyStrategyFactory {
    public static CpuStrategy create(Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> new EasyStrategy();
            case MEDIUM -> new MediumStrategy();
            case HARD -> new HardStrategy();
        };
    }
}

