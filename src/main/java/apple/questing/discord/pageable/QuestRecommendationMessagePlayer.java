package apple.questing.discord.pageable;

import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.reaction.ChoiceArguments;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QuestRecommendationMessagePlayer extends QuestRecommendationMessage {

    private final WynncraftPlayer player;

    public QuestRecommendationMessagePlayer(WynncraftPlayer player, FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ChoiceArguments choiceArguments) {
        super(finalQuestOptionsAll, channel, choiceArguments);
        this.player = player;
    }

    @Override
    public String makeMessage() {
        return String.format("**Options for %s (All classes)",
                player.name)
                + super.makeBodyMessage();
    }
}
