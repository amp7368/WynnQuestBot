package apple.questing.data;

import java.util.Collection;

public class Quest {
    public String name;
    public String[] immediateRequirements;
    public String[] allRequirements;
    public short levelMinimum;
    public Length length;
    public double time;
    public double collectionTime;
    public int xp;
    public int emerald;

    public Quest(String name, String[] immediateRequirements, String[] allRequirements, short levelMinimum,
                 Length length, double time, double collectionTime, int xp, int emerald) {
        this.name = name;
        this.immediateRequirements = immediateRequirements;
        this.allRequirements = allRequirements;
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
