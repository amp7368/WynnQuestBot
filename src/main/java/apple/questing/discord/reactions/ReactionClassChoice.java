package apple.questing.discord.reactions;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.reaction.AllReactableClassChoices;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.discord.pageable.QuestReccomendationMessageClass;
import apple.questing.sheets.SheetsWrite;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public class ReactionClassChoice implements DoReaction {

    public static final double DEFAULT_PERCENTAGE_AMOUNT = 0.5;

    @Override
    public void dealWithReaction(MessageReactionAddEvent event) {
        ClassChoiceMessage classChoiceMessage;
        if ((classChoiceMessage = AllReactableClassChoices.getMessage(event.getMessageId())) != null) {
            String reacted = event.getReaction().getReactionEmote().getEmoji();
            int classDesiredNum = -1;
            for (int i = 0; i < ClassChoiceMessage.emojiAlphabet.size(); i++) {
                if (ClassChoiceMessage.emojiAlphabet.get(i).equals(reacted)) {
                    classDesiredNum = i;
                    break;
                }
            }
            if (classDesiredNum >= classChoiceMessage.classNames.size()) {
                //todo send error message about how they reacted too high
                return;
            }
            String className = classChoiceMessage.classNames.get(classDesiredNum);
            WynncraftClass wynncraftClass = classChoiceMessage.getClassFromName(className);
            if (wynncraftClass == null) {
                // todo send an error message about how there was an internal error
                return;
            }

            AllReactableClassChoices.removeMessage(event.getMessageId());
            event.getTextChannel().retrieveMessageById(event.getMessageId()).complete().clearReactions().queue();
            FinalQuestOptionsAll finalQuestOptionsAll = GetAnswers.getAllSpecificAnswers(classChoiceMessage, wynncraftClass);
            new QuestReccomendationMessageClass(wynncraftClass, finalQuestOptionsAll, event.getChannel(), classChoiceMessage);
            SheetsWrite.writeSheet(finalQuestOptionsAll, event.getUserIdLong());
        } else {
            //todo
        }
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
