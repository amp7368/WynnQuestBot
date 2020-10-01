package apple.questing.data.quest;

import apple.questing.data.player.WynncraftClass;

import java.util.ArrayList;
import java.util.Collection;

public class QuestLinked {
    public Quest quest;
    public Collection<String> allRequirements = new ArrayList<>();
    public Collection<String> immediateRequirements = new ArrayList<>();
    public Collection<String> reqMe = new ArrayList<>();

    public QuestLinked(WynncraftClass wynncraftClass, Quest quest) {
        this.quest = quest;
        for (String questName : quest.allRequirements) {
            if (!wynncraftClass.questsCompleted.contains(questName)) {
                allRequirements.add(questName);
            }
        }
        for (String questName : quest.immediateRequirements) {
            if (!wynncraftClass.questsCompleted.contains(questName)) {
                immediateRequirements.add(questName);
            }
        }
    }
}
