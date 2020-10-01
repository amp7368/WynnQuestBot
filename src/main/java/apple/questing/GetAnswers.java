package apple.questing;

import apple.questing.data.answer.FinalQuestOptions;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.discord.reactions.ReactionClassChoice;
import apple.questing.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GetAnswers {
    @NotNull
    public static FinalQuestOptionsAll getAllSpecificAnswers(ClassChoiceMessage classChoiceMessage, WynncraftClass wynncraftClass) {
        // get the results
        List<FinalQuestOptions> questOptionsList = new ArrayList<>();
        List<FinalQuestCombo> questComboListAPT = new ArrayList<>();
        List<FinalQuestCombo> questComboListTime = new ArrayList<>();
        List<FinalQuestCombo> questComboListAmount = new ArrayList<>();

        // get the percentage results
        Pair<FinalQuestCombo, FinalQuestCombo> questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, false,
                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, false);
        questComboListAPT.add(questOptions.getKey());
        questComboListTime.add(questOptions.getValue());
        questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, false,
                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, true);
        questComboListAPT.add(questOptions.getKey());
        questComboListTime.add(questOptions.getValue());
        questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, true,
                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, false);
        questComboListAPT.add(questOptions.getKey());
        questComboListTime.add(questOptions.getValue());
        questOptions = SpecificQuestAlgorithm.whichGivenPercentageAmount(wynncraftClass, true,
                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classChoiceMessage.classLevel, true);
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
                    classChoiceMessage.timeToSpend, classChoiceMessage.classLevel, true);
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


        return new FinalQuestOptionsAll(
                questOptionsList.get(0),
                questOptionsList.get(1),
                questOptionsList.get(2),
                questOptionsList.get(3),
                questOptionsList.get(4),
                questOptionsList.get(5)
        );
    }

    public static FinalQuestOptionsAll getAllFullAnswers(WynncraftPlayer player, boolean isXpDesired, boolean isCollection, int classLevel, long timeToSpend, long amountDesired) {
        Pair<FinalQuestCombo, FinalQuestCombo> a = QuestAlgorithm.whichGivenTime(player, isXpDesired, timeToSpend, classLevel, isCollection);
        // get the results
        List<FinalQuestOptions> questOptionsList = new ArrayList<>();
        List<FinalQuestCombo> questComboListAPT = new ArrayList<>();
        List<FinalQuestCombo> questComboListTime = new ArrayList<>();
        List<FinalQuestCombo> questComboListAmount = new ArrayList<>();
        Pair<FinalQuestCombo, FinalQuestCombo> questOptions;
        questOptionsList.add(null);
        questOptionsList.add(null);
        questOptionsList.add(null);
        questOptionsList.add(null);

        // get the percentage results
//        Pair<FinalQuestCombo, FinalQuestCombo> questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, false,
//                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classLevel, false);
//        questComboListAPT.add(questOptions.getKey());
//        questComboListTime.add(questOptions.getValue());
//        questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, false,
//                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classLevel, true);
//        questComboListAPT.add(questOptions.getKey());
//        questComboListTime.add(questOptions.getValue());
//        questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, true,
//                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classLevel, false);
//        questComboListAPT.add(questOptions.getKey());
//        questComboListTime.add(questOptions.getValue());
//        questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, true,
//                ReactionClassChoice.DEFAULT_PERCENTAGE_AMOUNT, classLevel, true);
//        questComboListAPT.add(questOptions.getKey());
//        questComboListTime.add(questOptions.getValue());
//
//        questOptionsList.add(
//                new FinalQuestOptions(
//                        questComboListAPT.get(0),
//                        questComboListAPT.get(1),
//                        questComboListAPT.get(2),
//                        questComboListAPT.get(3))
//        );
//        questOptionsList.add(
//                new FinalQuestOptions(
//                        questComboListTime.get(0),
//                        questComboListTime.get(1),
//                        questComboListTime.get(2),
//                        questComboListTime.get(3))
//        );
//        questComboListAPT = new ArrayList<>();
//        questComboListTime = new ArrayList<>();
//
//
//        // get the amountDesiredResults
//        if (amountDesired != -1) {
//            questOptions = QuestAlgorithm.whichGivenRawAmount(player, false,
//                    amountDesired, classLevel, false);
//            questComboListAPT.add(questOptions.getKey());
//            questComboListTime.add(questOptions.getValue());
//            questOptions = QuestAlgorithm.whichGivenRawAmount(player, false,
//                    amountDesired, classLevel, true);
//            questComboListAPT.add(questOptions.getKey());
//            questComboListTime.add(questOptions.getValue());
//            questOptions = QuestAlgorithm.whichGivenRawAmount(player, true,
//                    amountDesired, classLevel, false);
//            questComboListAPT.add(questOptions.getKey());
//            questComboListTime.add(questOptions.getValue());
//            questOptions = QuestAlgorithm.whichGivenRawAmount(player, true,
//                    amountDesired, classLevel, true);
//            questComboListAPT.add(questOptions.getKey());
//            questComboListTime.add(questOptions.getValue());
//
//            questOptionsList.add(
//                    new FinalQuestOptions(
//                            questComboListAPT.get(0),
//                            questComboListAPT.get(1),
//                            questComboListAPT.get(2),
//                            questComboListAPT.get(3))
//            );
//            questOptionsList.add(
//                    new FinalQuestOptions(
//                            questComboListTime.get(0),
//                            questComboListTime.get(1),
//                            questComboListTime.get(2),
//                            questComboListTime.get(3))
//            );
//            questComboListAPT = new ArrayList<>();
//            questComboListAmount = new ArrayList<>();
//        } else {
//            questOptionsList.add(null);
//            questOptionsList.add(null);
//        }

        // get the timeToSpendResults
        if (timeToSpend != -1) {
            questOptions = QuestAlgorithm.whichGivenTime(player, false,
                    timeToSpend, classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListAmount.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenTime(player, false,
                    timeToSpend, classLevel, true);
            questComboListAPT.add(questOptions.getKey());
            questComboListAmount.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenTime(player, true,
                    timeToSpend, classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListAmount.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenTime(player, true,
                    timeToSpend, classLevel, true);
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


        FinalQuestOptionsAll finalQuestOptionsAll = new FinalQuestOptionsAll(
                questOptionsList.get(0),
                questOptionsList.get(1),
                questOptionsList.get(2),
                questOptionsList.get(3),
                questOptionsList.get(4),
                questOptionsList.get(5)
        );
        return finalQuestOptionsAll;
    }
}
