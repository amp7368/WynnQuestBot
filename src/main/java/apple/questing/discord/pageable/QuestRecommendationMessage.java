package apple.questing.discord.pageable;

import apple.questing.data.FinalQuestOptions;
import apple.questing.data.Quest;
import apple.questing.data.WynncraftClass;
import apple.questing.data.reaction.ClassChoiceMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

public class QuestRecommendationMessage implements Pageable {

    private static final int ENTRIES_PER_PAGE = 10;
    private final WynncraftClass wynncraftClass;
    private final long lastUpdated;
    private final FinalQuestOptions questOptions;
    private final Message message;
    private final ClassChoiceMessage classChoiceMessage;
    private int page = 0;

    public QuestRecommendationMessage(WynncraftClass wynncraftClass, FinalQuestOptions questOptions, MessageChannel channel, ClassChoiceMessage classChoiceMessage) {
        this.lastUpdated = System.currentTimeMillis();
        this.wynncraftClass = wynncraftClass;
        this.questOptions = questOptions;
        this.classChoiceMessage = classChoiceMessage;
        // print the results
        message = channel.sendMessage(makeMessage()).complete();
        PageableMessages.add(this);
        message.addReaction("\u2B05").queue();
        message.addReaction("\u27A1").queue();
    }

    private String makeMessage() {
        StringBuilder messageText = new StringBuilder();
        messageText.append(String.format("**Options for %s, Lvl: %d/%d, Dungeons: %d**", wynncraftClass.name, wynncraftClass.combatLevel, wynncraftClass.totalLevel, wynncraftClass.dungeonsWon));
        messageText.append("```md\n# Optimize Amount/minute\n");
        messageText.append(String.format("[Amount][%d] <|> [Time][%d] <|> [Quests][%d] <|> [Amount/minute][%d]\n",
                questOptions.bestAmountPerTime.getAmount(), (int) questOptions.bestAmountPerTime.getTime(),
                questOptions.bestAmountPerTime.getQuests().size(), (int) questOptions.bestAmountPerTime.amountPerTime()));
        messageText.append("\n");
        messageText.append(String.format("#    %-26s| <Amount>\n", "Quests to do"));

        List<Quest> quests = questOptions.bestAmountPerTime.getQuests();
        int lower = page * ENTRIES_PER_PAGE;
        int upper = Math.min(quests.size(), (page + 1) * ENTRIES_PER_PAGE);
        for (int i = lower; i < upper; i++) {
            messageText.append(String.format("%-31s| <%d>\n", String.format("<%-3s %s>", i + 1 + ".", quests.get(i).name), classChoiceMessage.isXpDesired ? quests.get(i).xp : quests.get(i).emerald));
        }
        messageText.append("\n```");
        return messageText.toString();
    }

    @Override
    public void forward() {
        if ((page + 1) * ENTRIES_PER_PAGE < questOptions.bestAmountPerTime.getQuests().size()) {
            ++page;
            message.editMessage(makeMessage()).queue();
        }
    }

    @Override
    public void backward() {
        if (page - 1 != -1) {
            --page;
            message.editMessage(makeMessage()).queue();
        }
    }

    @Override
    public Long getId() {
        return message.getIdLong();
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }
}
