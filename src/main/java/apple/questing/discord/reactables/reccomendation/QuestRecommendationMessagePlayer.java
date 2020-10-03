package apple.questing.discord.reactables.reccomendation;

import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.discord.reactables.class_choice.ChoiceArguments;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QuestRecommendationMessagePlayer extends QuestRecommendationMessage {

    public QuestRecommendationMessagePlayer(String spreadsheetId, FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ChoiceArguments choiceArguments, long xpDesiredGivenPerc, long emeraldDesiredGivenPerc) {
        super(spreadsheetId, finalQuestOptionsAll, channel, choiceArguments, xpDesiredGivenPerc, emeraldDesiredGivenPerc);
        initialize();
    }

    @Override
    public String makeMessage() {
        return String.format("**Options for %s (All classes)**\n",
                choiceArguments.player.name)
                + "<" + spreadsheetId + ">\n"
                + super.makeBodyMessage();
    }
}
