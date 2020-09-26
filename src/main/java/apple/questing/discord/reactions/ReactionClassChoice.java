package apple.questing.discord.reactions;

import apple.questing.QuestAlgorithm;
import apple.questing.data.FinalQuestOptions;
import apple.questing.data.Quest;
import apple.questing.data.WynncraftClass;
import apple.questing.data.reaction.AllReactableClassChoices;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.discord.pageable.QuestRecommendationMessage;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ReactionClassChoice implements DoReaction {

    public static final double DEFAULT_PERCENTAGE_AMOUNT = 0.8;

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

            // get the results
            FinalQuestOptions questOptions;
            if (classChoiceMessage.timeToSpend != -1) {
                questOptions = QuestAlgorithm.whichGivenTime(wynncraftClass, classChoiceMessage.isXpDesired,
                        classChoiceMessage.timeToSpend, classChoiceMessage.classLevel, classChoiceMessage.isCollection);
            } else if (classChoiceMessage.amountDesired != -1) {
                questOptions = QuestAlgorithm.whichGivenRawAmount(wynncraftClass, classChoiceMessage.isXpDesired,
                        classChoiceMessage.amountDesired, classChoiceMessage.classLevel, classChoiceMessage.isCollection);

            } else {
                questOptions = QuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, classChoiceMessage.isXpDesired,
                        DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, classChoiceMessage.isCollection);
            }
            new QuestRecommendationMessage(wynncraftClass, questOptions, event.getChannel(),classChoiceMessage);

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
