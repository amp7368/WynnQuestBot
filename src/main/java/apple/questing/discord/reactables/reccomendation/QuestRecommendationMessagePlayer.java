package apple.questing.discord.reactables.reccomendation;

import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.discord.reactables.class_choice.ChoiceArguments;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QuestRecommendationMessagePlayer extends QuestRecommendationMessage {

    private final WynncraftPlayer player;

    public QuestRecommendationMessagePlayer(WynncraftPlayer player, FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ChoiceArguments choiceArguments, long xpDesiredGivenPerc, long emeraldDesiredGivenPerc) {
        super(finalQuestOptionsAll, channel, choiceArguments,  xpDesiredGivenPerc,  emeraldDesiredGivenPerc);
        this.player = player;
        initialize();
    }

    @Override
    public String makeMessage() {
        return String.format("**Options for %s (All classes)**",
                player.name)
                + super.makeBodyMessage();
    }
}
