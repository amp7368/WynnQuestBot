package apple.questing.utils;

import apple.questing.data.quest.QuestLinked;

import java.util.List;

public class Sorting {
    public static void sortQuestsByAPT(boolean isXpDesired, boolean isIncludeCollection, List<QuestLinked> quests) {
        quests.sort((o1, o2) -> ((int) ((isXpDesired ? o2.xp : o2.emerald) / (isIncludeCollection ? o2.collectionTime + o2.time : o2.time)) -
                (int) ((isXpDesired ? o1.xp : o1.emerald) / (isIncludeCollection ? o1.collectionTime + o1.time : o1.time))));
    }
}
