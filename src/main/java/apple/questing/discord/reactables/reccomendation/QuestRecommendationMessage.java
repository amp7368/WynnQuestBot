package apple.questing.discord.reactables.reccomendation;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.quest.Quest;
import apple.questing.data.answer.FinalQuestCombo;
import apple.questing.data.quest.QuestLinked;
import apple.questing.discord.reactables.AllReactables;
import apple.questing.discord.reactables.ReactableMessage;
import apple.questing.discord.reactables.class_choice.ChoiceArguments;
import apple.questing.utils.Pretty;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static apple.questing.discord.reactables.AllReactables.Reactable.*;

public abstract class QuestRecommendationMessage implements ReactableMessage {

    private static final int ENTRIES_PER_PAGE = 10;
    private final long lastUpdated;
    private final FinalQuestOptionsAll finalQuestOptionsAll;
    private Message message;
    private final ChoiceArguments choiceArguments;
    private final MessageChannel channel;

    @Nullable
    private FinalQuestCombo answer1;
    @Nullable
    private FinalQuestCombo answer2;

    private int page = 0;

    private boolean isAnswer1 = true;
    private QuestRequest questRequest;
    private final long xpDesiredGivenPerc;
    private final long emeraldDesiredGivenPerc;

    /**
     * creates a QuestRecommendationMessage with a gui to change the results that are shown
     * NOTE: CALL initialize() IMMEDIATELY AFTER SORTING EVERYTHING WITH THE SUBCLASS
     *
     * @param finalQuestOptionsAll the results from all our queries
     * @param channel              the channel to send the message
     * @param choiceArguments      the arguments the user supplied earlier
     */
    public QuestRecommendationMessage(FinalQuestOptionsAll finalQuestOptionsAll, MessageChannel channel, ChoiceArguments choiceArguments, long xpDesiredGivenPerc, long emeraldDesiredGivenPerc) {
        this.lastUpdated = System.currentTimeMillis();
        this.finalQuestOptionsAll = finalQuestOptionsAll;
        this.choiceArguments = choiceArguments;
        this.channel = channel;
        this.xpDesiredGivenPerc = xpDesiredGivenPerc;
        this.emeraldDesiredGivenPerc = emeraldDesiredGivenPerc;

        if (choiceArguments.timeToSpend == -1 && choiceArguments.amountDesired == -1)
            questRequest = QuestRequest.PERC;
        else if (choiceArguments.timeToSpend != -1)
            questRequest = QuestRequest.TIME;
        else
            questRequest = QuestRequest.AMOUNT;

        updateAnswers();
    }

    /**
     * this should be called immediately after everything is sorted with the subclass
     */
    public void initialize() {
        // print the results
        message = channel.sendMessage(makeMessage()).complete();
        AllReactables.add(this); // we need message to be not null before we add it
        message.addReaction(LEFT.getFirstEmoji()).queue();
        message.addReaction(RIGHT.getFirstEmoji()).queue();
        message.addReaction(TOP.getFirstEmoji()).queue();
        message.addReaction(GEM.getFirstEmoji()).queue();
        message.addReaction(BASKET.getFirstEmoji()).queue();
        message.addReaction(CLOCK.getFirstEmoji()).queue();
        message.addReaction(AMOUNT.getFirstEmoji()).queue();
        message.addReaction(PERCENTAGE.getFirstEmoji()).queue();
        message.addReaction(SWITCH.getFirstEmoji()).queue();
    }

    public void updateAnswers() {
        // figure out which options the player has
        switch (questRequest) {
            case PERC:
                if (choiceArguments.isXpDesired) {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.answerPercAPT == null ? null : finalQuestOptionsAll.answerPercAPT.cx;
                        answer2 = finalQuestOptionsAll.answerPercTime == null ? null : finalQuestOptionsAll.answerPercTime.cx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerPercAPT == null ? null : finalQuestOptionsAll.answerPercAPT.ncx;
                        answer2 = finalQuestOptionsAll.answerPercTime == null ? null : finalQuestOptionsAll.answerPercTime.ncx;
                    }
                } else {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.answerPercAPT == null ? null : finalQuestOptionsAll.answerPercAPT.cnx;
                        answer2 = finalQuestOptionsAll.answerPercTime == null ? null : finalQuestOptionsAll.answerPercTime.cnx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerPercAPT == null ? null : finalQuestOptionsAll.answerPercAPT.ncnx;
                        answer2 = finalQuestOptionsAll.answerPercTime == null ? null : finalQuestOptionsAll.answerPercTime.ncnx;
                    }
                }
                break;
            case TIME:
                if (choiceArguments.isXpDesired) {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.answerTimeAPT == null ? null : finalQuestOptionsAll.answerTimeAPT.cx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount == null ? null : finalQuestOptionsAll.answerTimeAmount.cx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerTimeAPT == null ? null : finalQuestOptionsAll.answerTimeAPT.ncx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount == null ? null : finalQuestOptionsAll.answerTimeAmount.ncx;
                    }
                } else {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.answerTimeAPT == null ? null : finalQuestOptionsAll.answerTimeAPT.cnx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount == null ? null : finalQuestOptionsAll.answerTimeAmount.cnx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerTimeAPT == null ? null : finalQuestOptionsAll.answerTimeAPT.ncnx;
                        answer2 = finalQuestOptionsAll.answerTimeAmount == null ? null : finalQuestOptionsAll.answerTimeAmount.ncnx;
                    }
                }
                break;
            case AMOUNT:
                if (choiceArguments.isXpDesired) {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.answerAmountAPT == null ? null : finalQuestOptionsAll.answerAmountAPT.cx;
                        answer2 = finalQuestOptionsAll.answerAmountTime == null ? null : finalQuestOptionsAll.answerAmountTime.cx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerAmountAPT == null ? null : finalQuestOptionsAll.answerAmountAPT.ncx;
                        answer2 = finalQuestOptionsAll.answerAmountTime == null ? null : finalQuestOptionsAll.answerAmountTime.ncx;
                    }
                } else {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.answerAmountAPT == null ? null : finalQuestOptionsAll.answerAmountAPT.cnx;
                        answer2 = finalQuestOptionsAll.answerAmountTime == null ? null : finalQuestOptionsAll.answerAmountTime.cnx;
                    } else {
                        answer1 = finalQuestOptionsAll.answerAmountAPT == null ? null : finalQuestOptionsAll.answerAmountAPT.ncnx;
                        answer2 = finalQuestOptionsAll.answerAmountTime == null ? null : finalQuestOptionsAll.answerAmountTime.ncnx;
                    }
                }
        }
    }

    protected String makeBodyMessage() {
        StringBuilder messageText = new StringBuilder();
        messageText.append("```md\n");
        messageText.append("# Options\n");
        messageText.append(String.format("| [Include Collection][%b]\n", choiceArguments.isCollection));
        messageText.append(String.format("| [Emeralds Or Xp][%s]\n", choiceArguments.isXpDesired ? "Xp" : "Emeralds"));
        messageText.append(String.format("| [Request Type][%s]\n", questRequest.getName()));
        switch (questRequest) {
            case PERC:
                if (choiceArguments.isXpDesired) {
                    messageText.append(String.format("| [Xp Desired][%s]\n", Pretty.commas(xpDesiredGivenPerc)));
                } else {
                    messageText.append(String.format("| [Emeralds Desired][%s]\n", Pretty.getMon(emeraldDesiredGivenPerc)));
                }
                messageText.append(String.format(
                        "# Requesting %s will request that you earn %d%% of the emeralds that is possible for you to earn\n",
                        questRequest.name, (int) (GetAnswers.DEFAULT_PERCENTAGE_AMOUNT * 100)));
                break;
            case AMOUNT:
                if (choiceArguments.isXpDesired) {
                    messageText.append(String.format("| [Xp Desired][%s]\n", Pretty.commas(choiceArguments.amountDesired)));
                } else {
                    messageText.append(String.format("| [Emeralds Desired][%s]\n", Pretty.getMon(choiceArguments.amountDesired)));
                }
                messageText.append(String.format(
                        "# Requesting %s will request that you earn the specified amount of the emeralds or xp\n",
                        questRequest.name));
                break;
            case TIME:
                messageText.append(String.format("| [Time to Spend][%d mins]\n", choiceArguments.timeToSpend));
                messageText.append(String.format(
                        "# Requesting %s will request that you earn %d%% of the emeralds that is possible for you to earn\n",
                        questRequest.name, (int) (GetAnswers.DEFAULT_PERCENTAGE_AMOUNT * 100)));
                break;
        }
        messageText.append("\n");
        FinalQuestCombo answer = isAnswer1 ? answer1 : answer2;
        if (answer == null) {
            messageText.append("# Enter more arguments for this answer\n```");
            return messageText.toString();
        }
        messageText.append(String.format("#%-56s\n", "# header"));

        messageText.append(String.format("|%-56s",
                String.format(" [Total Amount][%s]",
                        answer.getAmountPretty())));
        messageText.append("|");
        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Time][%s]",
                        answer.getTimePretty())));
        messageText.append("|");
        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Quests][%d]",
                        answer.getQuests().size())));
        messageText.append("|");

        messageText.append("\n|");

        messageText.append(String.format("%-56s",
                String.format(" [Total Amount/minute][%s]",
                        answer.getAmountPerTimePretty())));
        messageText.append("|");

        messageText.append("\n\n");
        messageText.append(String.format("##    %-26s| <Amount>  | %-11s|\n", "Quests to do", "<Time>"));

        List<QuestLinked> quests = answer.getQuests();
        int lower = page * ENTRIES_PER_PAGE;
        for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
            final Quest quest1 = quests.size() > lower ? quests.get(lower++) : null;
            if (quest1 == null) {
                messageText.append(String.format("|%-31s  %-10s  %-11s", "", "", ""));
            } else {
                final String name = quest1.name;
                messageText.append(String.format("|%-31s| %-10s| %-11s",
                        String.format("<%-3s %s>", lower + ".", name.length() > 25 ? name.substring(0, 22) + "..." : name),
                        String.format("<%d>", choiceArguments.isXpDesired ? quest1.xp : quest1.emerald),
                        String.format("<%d mins>", (int) (Math.ceil(choiceArguments.isCollection ? quest1.time + quest1.collectionTime : quest1.time)))));

            }
            messageText.append("|");
            messageText.append("\n");
        }
        messageText.append("\n```");
        return messageText.toString();
    }

    public void forward() {
        if (isAnswer1) {
            if (answer1 == null)
                return;
            if ((page + 1) * ENTRIES_PER_PAGE < answer1.getQuests().size()) {
                ++page;
                message.editMessage(makeMessage()).queue();
            }
        } else {
            if (answer2 == null)
                return;
            if ((page + 1) * ENTRIES_PER_PAGE < answer2.getQuests().size()) {
                ++page;
                message.editMessage(makeMessage()).queue();
            }
        }
    }

    public void backward() {
        if (page - 1 != -1) {
            --page;
            message.editMessage(makeMessage()).queue();
        }
    }

    public void top() {
        page = 0;
        message.editMessage(makeMessage()).queue();
    }

    public void switchIsXpDesired() {
        choiceArguments.isXpDesired = !choiceArguments.isXpDesired;
        updateAnswers();
        message.editMessage(makeMessage()).queue();
    }

    private void switchIsCollection() {
        choiceArguments.isXpDesired = !choiceArguments.isXpDesired;
        updateAnswers();
        message.editMessage(makeMessage()).queue();
    }

    private void switchIsAnswer1() {
        isAnswer1 = !isAnswer1;
        message.editMessage(makeMessage()).queue();
    }

    private void setQuestRequest(QuestRequest questRequest) {
        this.questRequest = questRequest;
        isAnswer1 = !isAnswer1;
        updateAnswers();
        message.editMessage(makeMessage()).queue();
    }

    @Override
    public void dealWithReaction(AllReactables.Reactable reaction, String s, @NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        if (user == null) return;

        switch (reaction) {
            case LEFT:
                backward();
                event.getReaction().removeReaction(user).queue();
                break;
            case RIGHT:
                forward();
                event.getReaction().removeReaction(user).queue();
                break;
            case TOP:
                top();
                event.getReaction().removeReaction(user).queue();
                break;
            case GEM:
                switchIsXpDesired();
                event.getReaction().removeReaction(user).queue();
                break;
            case BASKET:
                switchIsCollection();
                event.getReaction().removeReaction(user).queue();
                break;
            case CLOCK:
                setQuestRequest(QuestRequest.TIME);
                event.getReaction().removeReaction(user).queue();
                break;
            case AMOUNT:
                setQuestRequest(QuestRequest.AMOUNT);
                event.getReaction().removeReaction(user).queue();
                break;
            case PERCENTAGE:
                setQuestRequest(QuestRequest.PERC);
                event.getReaction().removeReaction(user).queue();
                break;
            case SWITCH:
                switchIsAnswer1();
                event.getReaction().removeReaction(user).queue();
                break;
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

    public abstract String makeMessage();

    private enum QuestRequest {
        PERC("Percentage"),
        AMOUNT("Amount"),
        TIME("Time");

        private final String name;

        QuestRequest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
