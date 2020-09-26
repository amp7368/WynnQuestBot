package apple.questing.data;

import java.util.Collection;

public class FinalQuestComboAmount extends FinalQuestCombo {
    private final long amountDesired;

    public FinalQuestComboAmount(Collection<Quest> quests, boolean isXpDesired, long amountDesired, boolean isIncludeCollection) {
        super(quests, isXpDesired, isIncludeCollection);
        this.amountDesired = amountDesired;
    }

    public long getAmountLeft() {
        long amountLeft = amountDesired;
        for (Quest quest : quests) {
            amountLeft -= isXpDesired ? quest.xp : quest.emerald;
        }
        return amountLeft;
    }
}
