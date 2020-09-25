package apple.questing.data;

import java.util.ArrayList;
import java.util.Collection;

public class FinalQuestCombo {
    private Collection<Quest> quests;
    private boolean isXpDesired;  // alternative is emerald is desired
    private boolean isTimeDriven; // alternative is emerald threshold
    private long timeToSpend;
    private boolean isIncludeCollection;

    public FinalQuestCombo(Collection<Quest> quests, boolean isXpDesired, boolean isTimeDriven, long timeToSpend, boolean isIncludeCollection) {
        this.quests = quests;
        this.isXpDesired = isXpDesired;
        this.isTimeDriven = isTimeDriven;
        this.timeToSpend = timeToSpend;
        this.isIncludeCollection = isIncludeCollection;
    }

    public boolean hasQuest(Quest quest) {
        return quests.contains(quest);
    }

    public boolean addQuest(Quest quest) {
        quests.add(quest);
        return false;
    }

    public double amountPerTime() {
        double time = 0;
        if (isIncludeCollection) {
            for (Quest quest : quests) {
                time += quest.collectionTime + quest.time;
            }
        } else {
            for (Quest quest : quests) {
                time += quest.time;
            }
        }
        if (isXpDesired) {
            double xp = 0;
            for (Quest quest : quests) {
                xp += quest.xp;
            }
            return xp / time;
        } else {
            double em = 0;
            for (Quest quest : quests) {
                em += quest.emerald;
            }
            return em / time;
        }
    }

    public long getTimeToUse() {
        long timeToUse = timeToSpend;
        for (Quest quest : quests) {
            timeToUse -= isIncludeCollection ? quest.time + quest.collectionTime : quest.time;
        }
        return timeToUse;
    }

    public Collection<Quest> getQuests() {
        return quests;
    }

    public boolean isEmpty() {
        return quests.isEmpty();
    }

    @Override
    public String toString() {
        Collection<String> questNames = new ArrayList<>();
        for (Quest quest : quests) {
            questNames.add(quest.name);
        }
        return String.join(", ", questNames);
    }
}
