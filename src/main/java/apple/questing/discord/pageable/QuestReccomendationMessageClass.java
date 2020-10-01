package apple.questing.discord.pageable;

import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.reaction.ChoiceArguments;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QuestReccomendationMessageClass extends QuestRecommendationMessage {
    private final WynncraftClass wynncraftClass;

    public QuestReccomendationMessageClass(WynncraftClass wynncraftClass, FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ChoiceArguments classChoiceMessage) {
        super(finalQuestOptionsAll, channel, classChoiceMessage);
        this.wynncraftClass = wynncraftClass;

    }

    @Override
    public String makeMessage() {
        return String.format("**Options for %s, Lvl: %d/%d, Dungeons: %d**",
                wynncraftClass.name, wynncraftClass.combatLevel, wynncraftClass.totalLevel, wynncraftClass.dungeonsWon)
                + super.makeBodyMessage();
    }
}
