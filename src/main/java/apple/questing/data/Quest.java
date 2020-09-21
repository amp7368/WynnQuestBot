package apple.questing.data;

import java.util.Collection;

public class Quest {
    public String name;
    public String[] requirements;
    public short levelMinimum;
    public Length length;
    public double time;
    public double collectionTime;
    public int xp;
    public int emerald;

    public Quest(String name, String[] requirements, short levelMinimum, Length length, double time, double collectionTime, int xp, int emerald) {
        this.name = name;
        this.requirements = requirements;
        this.levelMinimum = levelMinimum;
        this.length = length;
        this.time = time;
        this.collectionTime = collectionTime;
        this.xp = xp;
        this.emerald = emerald;
    }

    public enum Length {
        SHORT,
        MEDIUM,
        LONG
    }
}
