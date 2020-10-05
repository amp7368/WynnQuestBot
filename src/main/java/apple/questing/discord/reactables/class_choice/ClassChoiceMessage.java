package apple.questing.discord.reactables.class_choice;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.quest.Quest;
import apple.questing.discord.DiscordBot;
import apple.questing.discord.reactables.AllReactables;
import apple.questing.discord.reactables.ReactableMessage;
import apple.questing.discord.reactables.book.QuestBookMessage;
import apple.questing.discord.reactables.reccomendation.QuestReccomendationMessageClass;
import apple.questing.sheets.SheetsWrite;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClassChoiceMessage extends ChoiceArguments implements ReactableMessage {
    public static final String WORKING_EMOJI = "\uD83D\uDEE0";
    public final long id;

    public final static List<String> emojiAlphabet = Arrays.asList("\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED",
            "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA"
            , "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF");
    private final ChoiceMessageType choiceMessageType;
    private final long lastUpdated = System.currentTimeMillis();

    public ClassChoiceMessage(long id, boolean isXpDesired, boolean isCollection, long timeToSpend, long amountDesired,
                              int classLevel, List<String> classNames, WynncraftPlayer player, ChoiceMessageType choiceMessageType) {
        super(isXpDesired, isCollection, timeToSpend, amountDesired, classLevel, classNames, player, false);
        this.id = id;
        this.choiceMessageType = choiceMessageType;
        AllReactables.add(this);
    }

    public WynncraftClass getClassFromName(String className) {
        for (WynncraftClass wynncraftClass : player.classes) {
            if (wynncraftClass.name.equals(className)) {
                return wynncraftClass;
            }
        }
        return null;
    }

    @Override
    public void dealWithReaction(AllReactables.Reactable reaction, String s, @NotNull MessageReactionAddEvent event) {
        if (reaction == AllReactables.Reactable.CLASS_CHOICE) {
            String reacted = event.getReactionEmote().getName();
            int classDesiredNum = -1;
            for (int i = 0; i < ClassChoiceMessage.emojiAlphabet.size(); i++) {
                if (ClassChoiceMessage.emojiAlphabet.get(i).equals(reacted)) {
                    classDesiredNum = i;
                    break;
                }
            }
            if (classDesiredNum >= classNames.size()) {
                event.getChannel().sendMessage("I don't have a class corresponding to that reaction").queue();
                return;
            }
            String className = classNames.get(classDesiredNum);
            WynncraftClass wynncraftClass = getClassFromName(className);
            if (wynncraftClass == null) {
                event.getChannel().sendMessage("There was an internal error on ClassChoiceMessage.dealWithReaction").queue();
                return;
            }

            AllReactables.remove(this.id);
            final Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();

            // tell the user we're working on the answer
            message.addReaction(WORKING_EMOJI).queue();

            if (choiceMessageType == ChoiceMessageType.BOOK) {
                new QuestBookMessage(player, wynncraftClass, event.getChannel(), classLevel, isCollection);
            } else if (choiceMessageType == ChoiceMessageType.SPECIFIC) {
                long xpDesiredGivenPerc = 0;
                long emeraldDesiredGivenPerc = 0;
                for (Quest quest : wynncraftClass.questsNotCompleted) {
                    if (quest.levelMinimum <= (classLevel == -1 ? wynncraftClass.combatLevel : classLevel)) {
                        xpDesiredGivenPerc += quest.xp;
                        emeraldDesiredGivenPerc += quest.emerald;
                    }
                }
                xpDesiredGivenPerc *= GetAnswers.DEFAULT_PERCENTAGE_AMOUNT;
                emeraldDesiredGivenPerc *= GetAnswers.DEFAULT_PERCENTAGE_AMOUNT;

                FinalQuestOptionsAll finalQuestOptionsAll = GetAnswers.getAllSpecificAnswers(this, wynncraftClass, player.name);
                String spreadsheetId = SheetsWrite.writeSheet(finalQuestOptionsAll, event.getUserIdLong(), player.name, false);
                if (spreadsheetId == null) return;
                new QuestReccomendationMessageClass(spreadsheetId, wynncraftClass, finalQuestOptionsAll, event.getChannel(), this, xpDesiredGivenPerc, emeraldDesiredGivenPerc);
            }
            message.clearReactions().queue();
            message.removeReaction(WORKING_EMOJI, DiscordBot.client.getSelfUser()).queue();

        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }

    @NotNull
    public static String getSingleClassMessage(WynncraftClass wynncraftClass) {
        String name = wynncraftClass.name;
        if (!Character.isAlphabetic(name.charAt(name.length() - 1))) {
            name = name.substring(0, name.length() - 1);
        }
        if (!Character.isAlphabetic(name.charAt(name.length() - 1))) {
            name = name.substring(0, name.length() - 1);
        }
        return String.format("Class: %-11s | Combat/Total: %-10s | Dungeons: %-4d", name, wynncraftClass.combatLevel + "/" + wynncraftClass.totalLevel, wynncraftClass.dungeonsWon);
    }

    public enum ChoiceMessageType {
        BOOK,
        SPECIFIC
    }
}
