package apple.questing.data.combo;

import apple.questing.data.Quest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FinalQuestCombo {
    final List<Quest> quests;
    final boolean isXpDesired;  // alternative is emerald is desired
    final boolean isIncludeCollection;


    public FinalQuestCombo(Collection<Quest> quests, boolean isXpDesired, boolean isIncludeCollection) {
        this.quests = new ArrayList<>(quests);
        this.isXpDesired = isXpDesired;
        this.isIncludeCollection = isIncludeCollection;
        // todo sort quests
    }

    public boolean hasQuest(Quest quest) {
        return quests.contains(quest);
    }

    public boolean addQuest(Quest quest) {
        quests.add(quest);
        return false;
    }

    public double amountPerTime() {
        double time = getTime();
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

    public double getTime() {
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
        return time;
    }

    public boolean isEmpty() {
        return quests.isEmpty();
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public long getAmount() {
        long amount = 0;
        if (isXpDesired) {
            for (Quest quest : quests) {
                amount += quest.xp;
            }
        } else {
            for (Quest quest : quests) {
                amount += quest.emerald;
            }
        }
        return amount;
    }

}
