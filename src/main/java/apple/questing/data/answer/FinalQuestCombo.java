package apple.questing.data.answer;

import apple.questing.data.quest.Quest;
import apple.questing.data.quest.QuestLinked;
import apple.questing.utils.Pretty;
import apple.questing.utils.Sorting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FinalQuestCombo {
    final List<QuestLinked> quests;
    public final boolean isXpDesired;  // alternative is emerald is desired
    public final boolean isIncludeCollection;


    public FinalQuestCombo(@NotNull Collection<QuestLinked> quests, boolean isXpDesired, boolean isIncludeCollection) {
        this.quests = new ArrayList<>(quests);
        this.isXpDesired = isXpDesired;
        this.isIncludeCollection = isIncludeCollection;
        // todo sort quests
    }

    public boolean addQuest(QuestLinked quest) {
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

    @NotNull
    public List<QuestLinked> getQuests() {
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

    public String getTimePretty() {
        double time = getTime();
        return Pretty.time(time);
    }

    public String getAmountPretty() {
        long amount = getAmount();
        if (isXpDesired) {
            return Pretty.commasXp(amount);
        } else {
            return Pretty.getMon(amount);
        }
    }

    public String getAmountPerTimePretty() {
        double amountPerTime = amountPerTime();
        if (isXpDesired) {
            return Pretty.commasXp((long) amountPerTime);
        } else {
            return Pretty.getMon(amountPerTime);
        }
    }

    public void sortByAPT() {
        Sorting.sortQuestsByAPT(isXpDesired, isIncludeCollection, quests);
        final int size = quests.size();
        for (int i = 0; i < size; i++) {
            final QuestLinked questAtI = quests.get(i);
            for (String quest : (questAtI.allRequirements)) {
                for (int questsPlace = i + 1; questsPlace < size; questsPlace++) {
                    final QuestLinked questAtPlaced = quests.get(questsPlace);
                    if (questAtPlaced.name.equals(quest) && questAtPlaced.playerClass.name.equals(questAtI.playerClass.name)) {
                        // push this quest to i
                        quests.add(i++, quests.remove(questsPlace));
                    }
                }
            }
        }
    }
}
