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
    private int page1 = 0; // separate pages in case it changes where we want to scroll individually
    private int page2 = 0;

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
        messageText.append("```md\n");
        messageText.append(String.format("#%-56s", "# Optimize Amount/minute"));
        messageText.append(String.format("|%-56s\n", "# Optimize Amount"));


        messageText.append(String.format("|%-56s",
                String.format(" [Total Amount][%s]",
                        questOptions.answer1.getAmountPretty())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Amount][%s]",
                        questOptions.answer2.getAmountPretty())));

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Time][%s]",
                        questOptions.answer1.getTimePretty())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Time][%s]",
                        questOptions.answer2.getTimePretty())));

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Quests][%d]",
                        questOptions.answer1.getQuests().size())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Quests][%d]",
                        questOptions.answer1.getQuests().size())));

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Amount/minute][%s]",
                        questOptions.answer1.amountPerTimePretty())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Amount/minute][%s]",
                        questOptions.answer2.amountPerTimePretty())));

        messageText.append("\n\n");
        messageText.append(String.format("##    %-26s| <Amount>  | %-11s|", "Quests to do", "<Time>"));
        messageText.append(String.format("#    %-26s| <Amount>  | %-11s|\n", "Quests to do", "<Time>"));

        List<Quest> quests1 = questOptions.answer1.getQuests();
        List<Quest> quests2 = questOptions.answer2.getQuests();
        int lower1 = page1 * ENTRIES_PER_PAGE;
        int lower2 = page2 * ENTRIES_PER_PAGE;
        for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
            final Quest quest1 = quests1.size() > lower1 ? quests1.get(lower1++) : null;
            final Quest quest2 = quests2.size() > lower2 ? quests2.get(lower2++) : null;
            if (quest1 == null) {
                messageText.append(String.format("|%-31s  %-10s  %-11s", "", "", ""));
            } else {
                final String name1 = quest1.name;
                messageText.append(String.format("|%-31s| %-10s| %-11s",
                        String.format("<%-3s %s>", lower1 + ".", name1.length() > 25 ? name1.substring(0, 22) + "..." : name1),
                        String.format("<%d>", classChoiceMessage.isXpDesired ? quest1.xp : quest1.emerald),
                        String.format("<%d mins>", (int) (Math.ceil(quest1.time)))));

            }
            messageText.append("|");
            if (quest2 == null) {
                messageText.append(String.format("%-31s  %-10s  %-11s", "", "", ""));
            } else {
                final String name2 = quest2.name;
                messageText.append(String.format("%-31s| %-10s| %-11s",
                        String.format("<%-3s %s>", lower2 + ".", name2.length() > 25 ? name2.substring(0, 22) + "..." : name2),
                        String.format("<%d>", classChoiceMessage.isXpDesired ? quest2.xp : quest2.emerald),
                        String.format("<%d mins>", (int) (Math.ceil(quest2.time)))));
            }
            messageText.append("|");
            messageText.append("\n");
        }
        messageText.append("\n```");
        return messageText.toString();
    }

    @Override
    public void forward() {
        if ((page1 + 1) * ENTRIES_PER_PAGE < questOptions.answer1.getQuests().size() ||
                (page2 + 1) * ENTRIES_PER_PAGE < questOptions.answer2.getQuests().size()) {
            ++page1;
            ++page2;
            message.editMessage(makeMessage()).queue();
        }
    }

    @Override
    public void backward() {
        if (page1 - 1 != -1) {
            --page1;
            --page2;
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
