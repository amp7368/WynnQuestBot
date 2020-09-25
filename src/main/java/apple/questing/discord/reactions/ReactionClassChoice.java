package apple.questing.discord.reactions;

import apple.questing.QuestAlgorithm;
import apple.questing.data.FinalQuestOptions;
import apple.questing.data.WynncraftClass;
import apple.questing.data.reaction.AllReactableClassChoices;
import apple.questing.data.reaction.ClassChoiceMessage;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

public class ReactionClassChoice implements DoReaction {
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
            if (classChoiceMessage.timeToSpend != -1) {
                FinalQuestOptions questOptions = QuestAlgorithm.whichGivenTime(wynncraftClass, classChoiceMessage.isXpDesired,
                        classChoiceMessage.timeToSpend, classChoiceMessage.classLevel, classChoiceMessage.isCollection);
                StringBuilder messageText = new StringBuilder();
                messageText.append("Options for \n");
                messageText.append(getSingleClassMessage(wynncraftClass));
                messageText.append("\n----------------------------------------------------------------------------\n");
                messageText.append("Optimize amount/minute: ");
                messageText.append(String.format("Amount: %d | Time: %d | Quests: %d | Amount/minute: %d\n",
                        questOptions.bestAmountPerTime.getAmount(), (int) questOptions.bestAmountPerTime.getTime(),
                        questOptions.bestAmountPerTime.getQuests().size(), (int) questOptions.bestAmountPerTime.amountPerTime()));
                Collection<String> quests = new ArrayList<>();
                questOptions.bestAmountPerTime.getQuests().forEach(quest -> quests.add(quest.name));
                messageText.append(String.join(", ", quests));
                messageText.append('\n');
                messageText.append("Optimize amount given time constraint");
                event.getChannel().sendMessage(messageText.toString()).queue();
            } else {
                //todo do the other QuestAlgorithm
            }
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
        return String.format("Class: %-11s | Combat/Total: %-10s | Dungeons: %d", name, wynncraftClass.combatLevel + "/" + wynncraftClass.totalLevel, wynncraftClass.dungeonsWon);
    }
}
