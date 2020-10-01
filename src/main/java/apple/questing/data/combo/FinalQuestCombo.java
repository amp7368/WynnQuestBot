package apple.questing.data.combo;

import apple.questing.data.quest.Quest;
import apple.questing.utils.Pretty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FinalQuestCombo {
    final List<Quest> quests;
    public final boolean isXpDesired;  // alternative is emerald is desired
    public final boolean isIncludeCollection;


    public FinalQuestCombo(@NotNull Collection<Quest> quests, boolean isXpDesired, boolean isIncludeCollection) {
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

    @NotNull
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

    public String getTimePretty() {
        double time = getTime();
        int hr = (int) (time / 60);
        int min = (int) Math.ceil(time % 60);
        StringBuilder timePretty = new StringBuilder();
        if (hr != 0) {
            timePretty.append(hr);
            if (hr == 1)
                timePretty.append(" hr");
            else
                timePretty.append(" hrs");
            if (min != 0)
                timePretty.append(' ');
        }
        if (min != 0) {
            timePretty.append(min);
            if (min == 1)
                timePretty.append(" min");
            else
                timePretty.append(" mins");
        }
        return timePretty.toString();
    }

    public String getAmountPretty() {
        long amount = getAmount();
        if (isXpDesired) {
            return Pretty.commas(amount);
        } else {
            return Pretty.getMon(amount);
        }
    }

    public String getAmountPerTimePretty() {
        double amountPerTime = amountPerTime();
        if (isXpDesired) {
            return Pretty.commas((long) amountPerTime);
        } else {
            return Pretty.getMon(amountPerTime);
        }
    }
}
