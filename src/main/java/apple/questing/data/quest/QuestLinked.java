package apple.questing.data.quest;

import apple.questing.data.player.WynncraftClass;

import java.util.ArrayList;
import java.util.Collection;

public class QuestLinked extends Quest {
    public final Collection<String> allRequirements = new ArrayList<>();
    public final Collection<String> immediateRequirements = new ArrayList<>();
    public final Collection<String> reqMe = new ArrayList<>();
    public final WynncraftClass playerClass;

    public QuestLinked(WynncraftClass wynncraftClass, Quest quest) {
        super(quest);
        this.playerClass = wynncraftClass;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuestLinked) {
            QuestLinked other = (QuestLinked) obj;
            return super.name.equals(other.name) &&
                    playerClass==null?(other.playerClass==null):
                    playerClass.name.equals(other.playerClass.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        long questName = super.name.hashCode();
        if (playerClass == null)
            return (int) questName;
        long playerClassName = playerClass.name.hashCode();
        return (int) ((questName + playerClassName) % Integer.MAX_VALUE);
    }
}
