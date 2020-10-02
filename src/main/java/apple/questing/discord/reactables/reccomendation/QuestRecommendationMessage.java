package apple.questing.discord.reactables.reccomendation;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
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
import static apple.questing.data.answer.FinalQuestOptionsAll.Answer.*;

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
        message.addReaction("\u25AA").queue();
        message.addReaction(GEM.getFirstEmoji()).queue();
        message.addReaction(BASKET.getFirstEmoji()).queue();
        message.addReaction("\u25FC").queue();
        message.addReaction(CLOCK.getFirstEmoji()).queue();
        message.addReaction(AMOUNT.getFirstEmoji()).queue();
        message.addReaction(PERCENTAGE.getFirstEmoji()).queue();
        message.addReaction("\u2B1B").queue();
        message.addReaction(SWITCH.getFirstEmoji()).queue();
    }

    public void updateAnswers() {
        // figure out which options the player has
        switch (questRequest) {
            case PERC:
                if (choiceArguments.isXpDesired) {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.get(Desired.PERC, Goal.APT, CX.CX);
                        answer2 = finalQuestOptionsAll.get(Desired.PERC, Goal.TIME, CX.CX);
                    } else {
                        answer1 = finalQuestOptionsAll.get(Desired.PERC, Goal.APT, CX.NCX);
                        answer2 = finalQuestOptionsAll.get(Desired.PERC, Goal.TIME, CX.NCX);
                    }
                } else {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.get(Desired.PERC, Goal.APT, CX.CNX);
                        answer2 = finalQuestOptionsAll.get(Desired.PERC, Goal.TIME, CX.CNX);
                    } else {
                        answer1 = finalQuestOptionsAll.get(Desired.PERC, Goal.APT, CX.NCNX);
                        answer2 = finalQuestOptionsAll.get(Desired.PERC, Goal.TIME, CX.NCNX);
                    }
                }
                break;
            case TIME:
                if (choiceArguments.isXpDesired) {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.get(Desired.TIME, Goal.APT, CX.CX);
                        answer2 = finalQuestOptionsAll.get(Desired.TIME, Goal.AMOUNT, CX.CX);
                    } else {
                        answer1 = finalQuestOptionsAll.get(Desired.TIME, Goal.APT, CX.NCX);
                        answer2 = finalQuestOptionsAll.get(Desired.TIME, Goal.AMOUNT, CX.NCX);
                    }
                } else {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.get(Desired.TIME, Goal.APT, CX.CNX);
                        answer2 = finalQuestOptionsAll.get(Desired.TIME, Goal.AMOUNT, CX.CNX);
                    } else {
                        answer1 = finalQuestOptionsAll.get(Desired.TIME, Goal.APT, CX.NCNX);
                        answer2 = finalQuestOptionsAll.get(Desired.TIME, Goal.AMOUNT, CX.NCNX);
                    }
                }
                break;
            case AMOUNT:
                if (choiceArguments.isXpDesired) {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.APT, CX.CX);
                        answer2 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.TIME, CX.CX);
                    } else {
                        answer1 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.APT, CX.NCX);
                        answer2 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.TIME, CX.NCX);
                    }
                } else {
                    if (choiceArguments.isCollection) {
                        answer1 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.APT, CX.CNX);
                        answer2 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.TIME, CX.CNX);
                    } else {
                        answer1 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.APT, CX.NCNX);
                        answer2 = finalQuestOptionsAll.get(Desired.AMOUNT, Goal.TIME, CX.NCNX);
                    }
                }
        }
    }

    protected String makeBodyMessage() {
        StringBuilder messageText = new StringBuilder();
        messageText.append("```md\n");
        messageText.append("#");
        messageText.append("=".repeat(30));
        messageText.append("#\n");
        messageText.append(String.format("# %-28s #\n",
                String.format(
                        "%s | %s", isAnswer1 ? "Cycle 1" : "Cycle 2", questRequest.getName()
                )));
        messageText.append("#");
        messageText.append("=".repeat(30));
        messageText.append("#\n");
        switch (questRequest) {
            case PERC:
                messageText.append(String.format(
                        "[!][Requesting %s will request that you earn %d%% of the emeralds that is possible for you to earn]\n",
                        questRequest.name, (int) (GetAnswers.DEFAULT_PERCENTAGE_AMOUNT * 100)));
                break;
            case AMOUNT:
                messageText.append(String.format(
                        "[!][Requesting %s will request that you earn the specified amount of the emeralds or xp]\n",
                        questRequest.name));
                break;
            case TIME:
                messageText.append(String.format(
                        "[!][Requesting %s will request that you earn as much as possible in the time given]\n",
                        questRequest.name));
                break;
        }
        messageText.append("\n");
        messageText.append(String.format("[Include Collection][%b]\n", choiceArguments.isCollection));
        messageText.append(String.format("[Emeralds Or Xp][%s]\n", choiceArguments.isXpDesired ? "Xp" : "Emeralds"));
        switch (questRequest) {
            case PERC:
                if (choiceArguments.isXpDesired) {
                    messageText.append(String.format("[Xp Desired][%s]\n", Pretty.commasXp(xpDesiredGivenPerc)));
                } else {
                    messageText.append(String.format("[Emeralds Desired][%s]\n", Pretty.getMon(emeraldDesiredGivenPerc)));
                }
                break;
            case AMOUNT:
                if (choiceArguments.isXpDesired) {
                    messageText.append(String.format("[Xp Desired][%s]\n", Pretty.commasXp(choiceArguments.amountDesired)));
                } else {
                    messageText.append(String.format("[Emeralds Desired][%s]\n", Pretty.getMon(choiceArguments.amountDesired)));
                }
                break;
            case TIME:
                messageText.append(String.format("[Time to Spend][%s]\n", Pretty.time(choiceArguments.timeToSpend)));
                break;
        }
        messageText.append("\n");
        FinalQuestCombo answer = isAnswer1 ? answer1 : answer2;
        if (answer == null) {
            messageText.append("# Enter more arguments for this answer\n```");
            return messageText.toString();
        }
        StringBuilder answer1Header = new StringBuilder(String.format("#%-56s\n", " Cycle 1"));
        StringBuilder answer2Header = new StringBuilder(String.format("#%-56s\n", " Cycle 2"));
        if (answer1 == null) {
            answer1Header.append("This results is not available");
        } else {
            answer1Header.append(String.format("[Total Amount][%s]",
                    answer1.getAmountPretty()));
            answer1Header.append("\n");

            answer1Header.append(String.format("[Total Time][%s]",
                    answer1.getTimePretty()));
            answer1Header.append("\n");

            answer1Header.append(String.format("[Total Quests][%d]",
                    answer1.getQuests().size()));

            answer1Header.append("\n");

            answer1Header.append(String.format("[Total Amount/minute][%s]",
                    answer1.getAmountPerTimePretty()));

        }
        if (answer2 == null) {
            answer2Header.append("This results is not available");
        } else {

            answer2Header.append(String.format("[Total Amount][%s]",
                    answer2.getAmountPretty()));
            answer2Header.append("\n");

            answer2Header.append(String.format("[Total Time][%s]",
                    answer2.getTimePretty()));
            answer2Header.append("\n");

            answer2Header.append(String.format("[Total Quests][%d]",
                    answer2.getQuests().size()));

            answer2Header.append("\n");

            answer2Header.append(String.format("[Total Amount/minute][%s]",
                    answer2.getAmountPerTimePretty()));
        }
        if (isAnswer1) {
            messageText.append(answer1Header);
            messageText.append("\n");
            messageText.append(answer2Header);
        } else {
            messageText.append(answer2Header);
            messageText.append("\n");
            messageText.append(answer1Header);
        }

        messageText.append("\n\n");
        messageText.append(String.format("#     %-26s| <Amount>   | %-11s| %-13s| %-10s|\n", "Quests to do", "<Time>", "<Class>", "<Level>"));

        List<QuestLinked> quests = answer.getQuests();
        int lower = page * ENTRIES_PER_PAGE;
        for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
            final QuestLinked quest = quests.size() > lower ? quests.get(lower++) : null;
            if (quest == null) {
                messageText.append(String.format("|%s", ""));
            } else {
                final String name = quest.name;
                messageText.append(String.format("|%-31s| %-11s| %-11s| %-13s| %-10s|",
                        String.format("<%-3s %s>", lower + ".", name.length() > 25 ? name.substring(0, 22) + "..." : name),
                        String.format("<%s>", choiceArguments.isXpDesired ? Pretty.commasXp(quest.xp) : Pretty.getMon(quest.emerald)),
                        String.format("<%d mins>", (int) (Math.ceil(choiceArguments.isCollection ? quest.time + quest.collectionTime : quest.time))),
                        String.format("<%s>", quest.playerClass.namePretty),
                        String.format("<%d/%d>", quest.playerClass.combatLevel, quest.playerClass.totalLevel)
                ));

            }
            messageText.append("\n");
        }
        messageText.append("\n```");
        messageText.append(BASKET.getFirstEmoji()).append(" **toggle include collection time**\n");
        messageText.append(GEM.getFirstEmoji()).append(" **toggle xp or emeralds**\n");
        messageText.append(CLOCK.getFirstEmoji()).append(" **set to time variable**\n");
        messageText.append(AMOUNT.getFirstEmoji()).append(" **set to percentage variable**\n");
        messageText.append(PERCENTAGE.getFirstEmoji()).append(" **set to emerald variable**\n");
        messageText.append(SWITCH.getFirstEmoji()).append(" **cycle answer**\n");
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
        isAnswer1 = true;
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
        PERC("Percentage Results"),
        AMOUNT("Amount Results"),
        TIME("Time Results");

        private final String name;

        QuestRequest(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
