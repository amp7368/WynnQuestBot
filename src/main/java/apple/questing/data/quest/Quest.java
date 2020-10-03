package apple.questing.data.quest;


public class Quest {
    public final String name;
    public final String[] immediateRequirements;
    public String[] allRequirements;
    public final short levelMinimum;
    public final Length length;
    public final double time;
    public final double collectionTime;
    public final int xp;
    public final int emerald;

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

    public Quest(Quest quest) {
        this.name = quest.name;
        this.immediateRequirements = quest.immediateRequirements;
        this.allRequirements = quest.allRequirements;
        this.levelMinimum = quest.levelMinimum;
        this.length = quest.length;
        this.time = quest.time;
        this.collectionTime = quest.collectionTime;
        this.xp = quest.xp;
        this.emerald = quest.emerald;
    }

    public enum Length {
        SHORT,
        MEDIUM,
        LONG
    }
}
