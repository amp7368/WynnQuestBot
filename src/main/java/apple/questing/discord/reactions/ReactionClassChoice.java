package apple.questing.discord.reactions;

import apple.questing.SpecificQuestAlgorithm;
import apple.questing.data.FinalQuestOptions;
import apple.questing.data.FinalQuestOptionsAll;
import apple.questing.data.WynncraftClass;
import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.reaction.AllReactableClassChoices;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.discord.pageable.QuestRecommendationMessage;
import apple.questing.sheets.SheetsWrite;
import apple.questing.utils.Pair;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

            // get the results
            List<FinalQuestOptions> questOptionsList = new ArrayList<>();
            List<FinalQuestCombo> questComboListAPT = new ArrayList<>();
            List<FinalQuestCombo> questComboListTime = new ArrayList<>();
            List<FinalQuestCombo> questComboListAmount = new ArrayList<>();

            // get the percentage results
            Pair<FinalQuestCombo, FinalQuestCombo> questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, false,
                    DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());
            questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, false,
                    DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, true);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());
            questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, true,
                    DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());
            questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, true,
                    DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, true);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());

            questOptionsList.add(
                    new FinalQuestOptions(
                            questComboListAPT.get(0),
                            questComboListAPT.get(1),
                            questComboListAPT.get(2),
                            questComboListAPT.get(3))
            );
            questOptionsList.add(
                    new FinalQuestOptions(
                            questComboListTime.get(0),
                            questComboListTime.get(1),
                            questComboListTime.get(2),
                            questComboListTime.get(3))
            );
            questComboListAPT = new ArrayList<>();
            questComboListTime = new ArrayList<>();


            // get the amountDesiredResults
            if (classChoiceMessage.amountDesired != -1) {
                questOptions = SpecificQuestAlgorithm.whichGivenRawAmount(wynncraftClass, false,
                        classChoiceMessage.amountDesired, classChoiceMessage.classLevel, false);
                questComboListAPT.add(questOptions.getKey());
                questComboListTime.add(questOptions.getValue());
                questOptions = SpecificQuestAlgorithm.whichGivenRawAmount(wynncraftClass, false,
                        classChoiceMessage.amountDesired, classChoiceMessage.classLevel, true);
                questComboListAPT.add(questOptions.getKey());
                questComboListTime.add(questOptions.getValue());
                questOptions = SpecificQuestAlgorithm.whichGivenRawAmount(wynncraftClass, true,
                        classChoiceMessage.amountDesired, classChoiceMessage.classLevel, false);
                questComboListAPT.add(questOptions.getKey());
                questComboListTime.add(questOptions.getValue());
                questOptions = SpecificQuestAlgorithm.whichGivenRawAmount(wynncraftClass, true,
                        classChoiceMessage.amountDesired, classChoiceMessage.classLevel, true);
                questComboListAPT.add(questOptions.getKey());
                questComboListTime.add(questOptions.getValue());

                questOptionsList.add(
                        new FinalQuestOptions(
                                questComboListAPT.get(0),
                                questComboListAPT.get(1),
                                questComboListAPT.get(2),
                                questComboListAPT.get(3))
                );
                questOptionsList.add(
                        new FinalQuestOptions(
                                questComboListTime.get(0),
                                questComboListTime.get(1),
                                questComboListTime.get(2),
                                questComboListTime.get(3))
                );
                questComboListAPT = new ArrayList<>();
                questComboListAmount = new ArrayList<>();
            } else {
                questOptionsList.add(null);
                questOptionsList.add(null);
            }

            // get the timeToSpendResults
            if (classChoiceMessage.timeToSpend != -1) {
                questOptions = SpecificQuestAlgorithm.whichGivenTime(wynncraftClass, false,
                        classChoiceMessage.timeToSpend, classChoiceMessage.classLevel, false);
                questComboListAPT.add(questOptions.getKey());
                questComboListAmount.add(questOptions.getValue());
                questOptions = SpecificQuestAlgorithm.whichGivenTime(wynncraftClass, false,
                        classChoiceMessage.timeToSpend, classChoiceMessage.classLevel, true);
                questComboListAPT.add(questOptions.getKey());
                questComboListAmount.add(questOptions.getValue());
                questOptions = SpecificQuestAlgorithm.whichGivenTime(wynncraftClass, true,
                        classChoiceMessage.timeToSpend, classChoiceMessage.classLevel, false);
                questComboListAPT.add(questOptions.getKey());
                questComboListAmount.add(questOptions.getValue());
                questOptions = SpecificQuestAlgorithm.whichGivenTime(wynncraftClass, true,
                        classChoiceMessage.timeToSpend, classChoiceMessage.classLevel,true);
                questComboListAPT.add(questOptions.getKey());
                questComboListAmount.add(questOptions.getValue());
                questOptionsList.add(
                        new FinalQuestOptions(
                                questComboListAPT.get(0),
                                questComboListAPT.get(1),
                                questComboListAPT.get(2),
                                questComboListAPT.get(3))
                );
                questOptionsList.add(
                        new FinalQuestOptions(
                                questComboListAmount.get(0),
                                questComboListAmount.get(1),
                                questComboListAmount.get(2),
                                questComboListAmount.get(3))
                );
            } else {
                questOptionsList.add(null);
                questOptionsList.add(null);
            }


            event.getTextChannel().retrieveMessageById(event.getMessageId()).complete().clearReactions().queue();
            AllReactableClassChoices.removeMessage(event.getMessageId());
            FinalQuestOptionsAll finalQuestOptionsAll = new FinalQuestOptionsAll(
                    questOptionsList.get(0),
                    questOptionsList.get(1),
                    questOptionsList.get(2),
                    questOptionsList.get(3),
                    questOptionsList.get(4),
                    questOptionsList.get(5)
            );
            new QuestRecommendationMessage(wynncraftClass, finalQuestOptionsAll, event.getChannel(), classChoiceMessage);
            SheetsWrite.writeSheet(finalQuestOptionsAll, wynncraftClass, classChoiceMessage, event.getUserIdLong());
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
