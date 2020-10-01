package apple.questing;

import apple.questing.data.answer.FinalQuestOptions;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.reaction.ChoiceArguments;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetAnswers {
    public static final double DEFAULT_PERCENTAGE_AMOUNT = 0.5;

    @NotNull
    public static FinalQuestOptionsAll getAllSpecificAnswers(ClassChoiceMessage classChoiceMessage, WynncraftClass wynncraftClass, String name) {
        return getAllFullAnswers(new WynncraftPlayer(Collections.singletonList(wynncraftClass), name), classChoiceMessage);
    }

    public static FinalQuestOptionsAll getAllFullAnswers(WynncraftPlayer player, ChoiceArguments choiceArguments) {
        Pair<FinalQuestCombo, FinalQuestCombo> a = QuestAlgorithm.whichGivenTime(player, choiceArguments.isXpDesired, choiceArguments.timeToSpend, choiceArguments.classLevel, choiceArguments.isCollection);
        // get the results
        List<FinalQuestOptions> questOptionsList = new ArrayList<>();
        List<FinalQuestCombo> questComboListAPT = new ArrayList<>();
        List<FinalQuestCombo> questComboListTime = new ArrayList<>();
        List<FinalQuestCombo> questComboListAmount = new ArrayList<>();

//         get the percentage results
        Pair<FinalQuestCombo, FinalQuestCombo> questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, false,
                DEFAULT_PERCENTAGE_AMOUNT, choiceArguments.classLevel, false);
        questComboListAPT.add(questOptions.getKey());
        questComboListTime.add(questOptions.getValue());
        questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, false,
                DEFAULT_PERCENTAGE_AMOUNT, choiceArguments.classLevel, true);
        questComboListAPT.add(questOptions.getKey());
        questComboListTime.add(questOptions.getValue());
        questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, true,
                DEFAULT_PERCENTAGE_AMOUNT, choiceArguments.classLevel, false);
        questComboListAPT.add(questOptions.getKey());
        questComboListTime.add(questOptions.getValue());
        questOptions = QuestAlgorithm.whichGivenPercentageAmount(player, true,
                DEFAULT_PERCENTAGE_AMOUNT, choiceArguments.classLevel, true);
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
        if (choiceArguments.amountDesired != -1) {
            questOptions = QuestAlgorithm.whichGivenRawAmount(player,  choiceArguments.amountDesired,false,
                    choiceArguments.classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenRawAmount(player,  choiceArguments.amountDesired,false,
                    choiceArguments.classLevel, true);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenRawAmount(player,  choiceArguments.amountDesired,true,
                    choiceArguments.classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListTime.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenRawAmount(player, choiceArguments.amountDesired, true,
                    choiceArguments.classLevel, true);
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
        if (choiceArguments.timeToSpend != -1) {
            questOptions = QuestAlgorithm.whichGivenTime(player, false,
                    choiceArguments.timeToSpend, choiceArguments.classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListAmount.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenTime(player, false,
                    choiceArguments.timeToSpend, choiceArguments.classLevel, true);
            questComboListAPT.add(questOptions.getKey());
            questComboListAmount.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenTime(player, true,
                    choiceArguments.timeToSpend, choiceArguments.classLevel, false);
            questComboListAPT.add(questOptions.getKey());
            questComboListAmount.add(questOptions.getValue());
            questOptions = QuestAlgorithm.whichGivenTime(player, true,
                    choiceArguments.timeToSpend, choiceArguments.classLevel, true);
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
}
