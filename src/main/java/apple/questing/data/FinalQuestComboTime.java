package apple.questing.data;


import java.util.Collection;

public class FinalQuestComboTime extends FinalQuestCombo {
    private final long timeToSpend;

    public FinalQuestComboTime(Collection<Quest> quests, boolean isXpDesired, long timeToSpend, boolean isIncludeCollection) {
        super(quests, isXpDesired, isIncludeCollection);
        this.timeToSpend = timeToSpend;
    }

    public long getTimeToUse() {
        long timeToUse = timeToSpend;
        for (Quest quest : quests) {
            timeToUse -= isIncludeCollection ? quest.time + quest.collectionTime : quest.time;
        }
        return timeToUse;
    }
}
