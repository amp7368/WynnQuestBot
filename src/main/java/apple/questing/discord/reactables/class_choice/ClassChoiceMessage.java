package apple.questing.discord.reactables.class_choice;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.discord.reactables.AllReactables;
import apple.questing.discord.reactables.ReactableMessage;
import apple.questing.discord.reactables.reccomendation.QuestReccomendationMessageClass;
import apple.questing.sheets.SheetsWrite;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClassChoiceMessage extends ChoiceArguments implements ReactableMessage {
    public final long id;

    public final static List<String> emojiAlphabet = Arrays.asList("\uD83C\uDDE6", "\uD83C\uDDE7", "\uD83C\uDDE8", "\uD83C\uDDE9", "\uD83C\uDDEA", "\uD83C\uDDEB", "\uD83C\uDDEC", "\uD83C\uDDED",
            "\uD83C\uDDEE", "\uD83C\uDDEF", "\uD83C\uDDF0", "\uD83C\uDDF1", "\uD83C\uDDF2", "\uD83C\uDDF3", "\uD83C\uDDF4", "\uD83C\uDDF5", "\uD83C\uDDF6", "\uD83C\uDDF7", "\uD83C\uDDF8", "\uD83C\uDDF9", "\uD83C\uDDFA"
            , "\uD83C\uDDFB", "\uD83C\uDDFC", "\uD83C\uDDFD", "\uD83C\uDDFE", "\uD83C\uDDFF");

    public ClassChoiceMessage(long id, boolean isXpDesired, boolean isCollection, long timeToSpend, long amountDesired, int classLevel, List<String> classNames, WynncraftPlayer player) {
        super(isXpDesired, isCollection, timeToSpend, amountDesired, classLevel, classNames, player);
        this.id = id;
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
                //todo send error message about how they reacted too high
                return;
            }
            String className = classNames.get(classDesiredNum);
            WynncraftClass wynncraftClass = getClassFromName(className);
            if (wynncraftClass == null) {
                // todo send an error message about how there was an internal error
                return;
            }

            AllReactables.remove(this.id);
            event.getTextChannel().retrieveMessageById(event.getMessageId()).complete().clearReactions().queue();
            FinalQuestOptionsAll finalQuestOptionsAll = GetAnswers.getAllSpecificAnswers(this, wynncraftClass, player.name);
            new QuestReccomendationMessageClass(wynncraftClass, finalQuestOptionsAll, event.getChannel(), this);
            SheetsWrite.writeSheet(finalQuestOptionsAll, event.getUserIdLong(), false);
        }
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getLastUpdated() {
        return 0;
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
}
