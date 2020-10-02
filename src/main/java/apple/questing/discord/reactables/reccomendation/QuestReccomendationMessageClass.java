package apple.questing.discord.reactables.reccomendation;

import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.discord.reactables.class_choice.ChoiceArguments;
import net.dv8tion.jda.api.entities.MessageChannel;

public class QuestReccomendationMessageClass extends QuestRecommendationMessage {
    private final WynncraftClass wynncraftClass;

    public QuestReccomendationMessageClass(String spreadsheetId, WynncraftClass wynncraftClass, FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ChoiceArguments classChoiceMessage, long xpDesiredGivenPerc, long emeraldDesiredGivenPerc) {
        super(spreadsheetId, finalQuestOptionsAll, channel, classChoiceMessage, xpDesiredGivenPerc, emeraldDesiredGivenPerc);
        this.wynncraftClass = wynncraftClass;
        initialize();
    }

    @Override
    public String makeMessage() {
        String messageText = String.format("**Options for %s, Lvl: %d/%d, Dungeons: %d\n**",
                wynncraftClass.namePretty, wynncraftClass.combatLevel, wynncraftClass.totalLevel, wynncraftClass.dungeonsWon)
                + "<" + spreadsheetId + ">\n"
                + super.makeBodyMessage();
        String[] split = messageText.split("\n");
        for (int i = 0; i < split.length; i++)
            split[i] = split[i].trim();
        return String.join("\n", split);
    }
}
