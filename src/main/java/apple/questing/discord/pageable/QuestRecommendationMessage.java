package apple.questing.discord.pageable;

import apple.questing.data.FinalQuestOptionsAll;
import apple.questing.data.Quest;
import apple.questing.data.WynncraftClass;
import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.reaction.ClassChoiceMessage;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import javax.annotation.Nullable;
import java.util.List;

public class QuestRecommendationMessage implements Pageable {

    private static final int ENTRIES_PER_PAGE = 10;
    private final WynncraftClass wynncraftClass;
    private final long lastUpdated;
    private final FinalQuestOptionsAll finalQuestOptionsAll;
    private final Message message;

    @Nullable
    private FinalQuestCombo answer1;
    @Nullable
    private FinalQuestCombo answer2;

    private int page1 = 0; // separate pages in case it changes where we want to scroll individually
    private int page2 = 0;
    private QuestRequest questRequest;
    private boolean isXpDesired;
    private boolean isCollection;

    public QuestRecommendationMessage(WynncraftClass wynncraftClass, FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ClassChoiceMessage classChoiceMessage) {
        this.lastUpdated = System.currentTimeMillis();
        this.wynncraftClass = wynncraftClass;
        this.finalQuestOptionsAll = finalQuestOptionsAll;
        this.isXpDesired = classChoiceMessage.isXpDesired;
        this.isCollection = classChoiceMessage.isCollection;

        if (classChoiceMessage.timeToSpend == -1 && classChoiceMessage.amountDesired == -1)
            questRequest = QuestRequest.PERC;
        else if (classChoiceMessage.timeToSpend != -1)
            questRequest = QuestRequest.TIME;
        else
            questRequest = QuestRequest.AMOUNT;

        // figure out which options the player has
        switch (questRequest) {
            case PERC:
                if (isXpDesired) {
                    if (isCollection) {
                        answer1 = finalQuestOptionsAll.answerPercAPT.cx;
                        answer2 = finalQuestOptionsAll.answerPercTime.cx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerPercAPT.ncx;
                        answer2 = finalQuestOptionsAll.answerPercTime.ncx;
                    }
                } else {
                    if (isCollection) {
                        answer1 = finalQuestOptionsAll.answerPercAPT.cnx;
                        answer2 = finalQuestOptionsAll.answerPercTime.cnx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerPercAPT.ncnx;
                        answer2 = finalQuestOptionsAll.answerPercTime.ncnx;
                    }
                }
                break;
            case TIME:
                if (isXpDesired) {
                    if (isCollection) {
                        answer1 = finalQuestOptionsAll.answerTimeAPT.cx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount.cx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerTimeAPT.ncx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount.ncx;
                    }
                } else {
                    if (isCollection) {
                        answer1 = finalQuestOptionsAll.answerTimeAPT.cnx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount.cnx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerTimeAPT.ncnx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount.ncnx;
                    }
                }
                break;
            case AMOUNT:
                if (isXpDesired) {
                    if (isCollection) {
                        answer1 = finalQuestOptionsAll.answerAmountAPT.cx;
                        answer2 = finalQuestOptionsAll.answerAmountTime.cx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerAmountAPT.ncx;
                        answer2 = finalQuestOptionsAll.answerAmountTime.ncx;
                    }
                } else {
                    if (isCollection) {
                        answer1 = finalQuestOptionsAll.answerAmountAPT.cnx;
                        answer2 = finalQuestOptionsAll.answerAmountTime.cnx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerAmountAPT.ncnx;
                        answer2 = finalQuestOptionsAll.answerAmountTime.ncnx;
                    }
                }
        }


        // print the results
        message = channel.sendMessage(makeMessage()).complete();
        PageableMessages.add(this);
        message.addReaction("\u2B05").queue();
        message.addReaction("\u27A1").queue();
        message.addReaction("\u21A9").queue();
    }

    private String makeMessage() {
        if (answer1 == null || answer2 == null) {
            return "Enter more arguments for this answer";
        }
        StringBuilder messageText = new StringBuilder();
        messageText.append(String.format("**Options for %s, Lvl: %d/%d, Dungeons: %d**", wynncraftClass.name, wynncraftClass.combatLevel, wynncraftClass.totalLevel, wynncraftClass.dungeonsWon));
        messageText.append("```md\n");
        messageText.append(String.format("#%-56s", "# header"));
        messageText.append(String.format("|%-56s\n", "# header"));


        messageText.append(String.format("|%-56s",
                String.format(" [Total Amount][%s]",
                        answer1.getAmountPretty())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Amount][%s]",
                        answer2.getAmountPretty())));

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Time][%s]",
                        answer1.getTimePretty())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Time][%s]",
                        answer2.getTimePretty())));

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Quests][%d]",
                        answer1.getQuests().size())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Quests][%d]",
                        answer2.getQuests().size())));

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Amount/minute][%s]",
                        answer1.getAmountPerTimePretty())));
        messageText.append("|");
        messageText.append(String.format("%-56s",
                String.format(" [Total Amount/minute][%s]",
                        answer2.getAmountPerTimePretty())));

        messageText.append("\n\n");
        messageText.append(String.format("##    %-26s| <Amount>  | %-11s|", "Quests to do", "<Time>"));
        messageText.append(String.format("#    %-26s| <Amount>  | %-11s|\n", "Quests to do", "<Time>"));

        List<Quest> quests1 = answer1.getQuests();
        List<Quest> quests2 = answer2.getQuests();
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
                        String.format("<%d>", isXpDesired ? quest1.xp : quest1.emerald),
                        String.format("<%d mins>", (int) (Math.ceil(isCollection ? quest1.time + quest1.collectionTime : quest1.time)))));

            }
            messageText.append("|");
            if (quest2 == null) {
                messageText.append(String.format("%-31s  %-10s  %-11s", "", "", ""));
            } else {
                final String name2 = quest2.name;
                messageText.append(String.format("%-31s| %-10s| %-11s",
                        String.format("<%-3s %s>", lower2 + ".", name2.length() > 25 ? name2.substring(0, 22) + "..." : name2),
                        String.format("<%d>", isXpDesired ? quest2.xp : quest2.emerald),
                        String.format("<%d mins>", (int) (Math.ceil(isCollection ? quest2.time + quest2.collectionTime : quest2.time)))));
            }
            messageText.append("|");
            messageText.append("\n");
        }
        messageText.append("\n```");
        return messageText.toString();
    }

    @Override
    public void forward() {
        if ((page1 + 1) * ENTRIES_PER_PAGE < answer1.getQuests().size() ||
                (page2 + 1) * ENTRIES_PER_PAGE < answer2.getQuests().size()) {
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
    public void top() {
        page1 = 0;
        page2 = 0;
        message.editMessage(makeMessage()).queue();
    }

    @Override
    public Long getId() {
        return message.getIdLong();
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }

    private enum QuestRequest {
        PERC,
        AMOUNT,
        TIME
    }
}
