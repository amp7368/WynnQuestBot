package apple.questing;

import apple.questing.data.*;
import apple.questing.data.combo.FinalQuestComboAmount;
import apple.questing.data.combo.FinalQuestComboTime;

import java.util.*;

import static apple.questing.sheets.SheetsQuery.allQuests;
import static apple.questing.sheets.SheetsQuery.nameToQuest;

public class QuestAlgorithm {
    public static FinalQuestOptions whichGivenTime(WynncraftClass playerClass, boolean isXpDesired, long timeToSpend,
                                                   int classLevel, boolean isIncludeCollection) {
        // if class level is not specified, specify it
        if (classLevel == -1) classLevel = playerClass.combatLevel;

        // make a map of all the quest names to the quest requirements and quests that require itself
        HashMap<String, QuestLinked> nameToQuestLinked = new HashMap<>();
        for (Quest quest : allQuests) {
            if (quest.levelMinimum <= classLevel && !playerClass.questsCompleted.contains(quest.name)) {
                nameToQuestLinked.put(quest.name, new QuestLinked(playerClass, quest));
            }
        }

        // add the reqMe's to the map one at a time (not concurrently)
        for (QuestLinked quest : nameToQuestLinked.values()) {
            for (String req : quest.immediateRequirements) {
                QuestLinked questToAddReqMe = nameToQuestLinked.get(req);
                if (questToAddReqMe != null) {
                    questToAddReqMe.reqMe.add(quest.quest.name);
                }
            }
        }
        List<Quest> singletonQuests = new ArrayList<>();
        List<Collection<QuestLinked>> questCombinations = new ArrayList<>();
        // find all quests that start quest chains
        for (QuestLinked quest : nameToQuestLinked.values()) {
            // if I don't require anyone, and at least somebody requires me and this quest isn't too long
            if (quest.immediateRequirements.isEmpty() && !quest.reqMe.isEmpty()) {
                if (timeToSpend >= (isIncludeCollection ? quest.quest.time + quest.quest.collectionTime : quest.quest.time)) {
                    questCombinations.add(Collections.singletonList(quest));
                }
            } else if (quest.immediateRequirements.isEmpty()) {
                // this is a singleton quest
                singletonQuests.add(quest.quest);
            }
        }
        // sort the singleton quests by order of amount/time
        singletonQuests.sort((o1, o2) -> (int) Math.round((isXpDesired ? o2.xp : o2.emerald) / (isIncludeCollection ? o2.collectionTime + o2.time : o2.time) -
                (isXpDesired ? o1.xp : o1.emerald) / (isIncludeCollection ? o1.collectionTime + o1.time : o1.time)));

        // this is a set of collections of quest names
        Set<String> finalList = new HashSet<>();
        questCombinations.forEach(questsCombo -> {
            Collection<String> questsComboString = new ArrayList<>();
            questsCombo.forEach(questLinked -> questsComboString.add(questLinked.quest.name));
            finalList.add(String.join(",", questsComboString));
        });
        addQuestGivenTime(questCombinations, nameToQuestLinked, finalList, timeToSpend, isIncludeCollection);

        // add singleton quests that stay under the limit of time
        List<FinalQuestComboTime> finalQuestCombos = new ArrayList<>();
        for (String questComboStringAll : finalList) {
            Collection<Quest> questCombo = new ArrayList<>();
            for (String questComboString : questComboStringAll.split(",")) {
                questCombo.add(nameToQuest.get(questComboString));
            }
            // time is a given
            finalQuestCombos.add(new FinalQuestComboTime(questCombo, isXpDesired, timeToSpend, isIncludeCollection));
        }
        finalQuestCombos.add(new FinalQuestComboTime(new ArrayList<>(0), isXpDesired, timeToSpend, isIncludeCollection));

        // add singleton quests to all the combos
        for (FinalQuestComboTime finalQuestCombo : finalQuestCombos) {
            for (Quest singletonQuest : singletonQuests) {
                if ((isIncludeCollection ? singletonQuest.collectionTime + singletonQuest.time : singletonQuest.time) > finalQuestCombo.getTimeToUse()) {
                    // we're done with this combo
                    if (!finalQuestCombo.isEmpty())
                        break;
                } else if (!finalQuestCombo.hasQuest(singletonQuest)) {
                    // add this quest
                    finalQuestCombo.addQuest(singletonQuest);
                }
            }
        }

        // remove any combos that are empty
        finalQuestCombos.removeIf(FinalQuestComboTime::isEmpty);

        FinalQuestComboTime bestPerTime = finalQuestCombos.stream().max((o1, o2) -> (int) Math.round((o1.amountPerTime() - o2.amountPerTime()))).orElse(null);

        // add singleton quests to all the combos
        for (FinalQuestComboTime finalQuestCombo : finalQuestCombos) {
            for (Quest singletonQuest : singletonQuests) {
                if (!finalQuestCombo.hasQuest(singletonQuest) && !((isIncludeCollection ? singletonQuest.collectionTime + singletonQuest.time : singletonQuest.time) > finalQuestCombo.getTimeToUse())) {
                    // add this quest
                    finalQuestCombo.addQuest(singletonQuest);
                }
            }
        }
        FinalQuestComboTime bestUtilization = finalQuestCombos.stream().max((o1, o2) -> (int) Math.round((o1.amountPerTime() - o2.amountPerTime()))).orElse(null);


        return new FinalQuestOptions(bestPerTime, bestUtilization);
    }

    public static FinalQuestOptions whichGivenPercentageAmount(WynncraftClass playerClass, boolean isXpDesired, double percentageAmount, int classLevel, boolean isCollection) {
        // if class level is not specified, specify it
        if (classLevel == -1) classLevel = playerClass.combatLevel;
        long amountPossible = 0;
        for (Quest quest : playerClass.questsNotCompleted) {
            if (quest.levelMinimum <= classLevel)
                amountPossible += isXpDesired ? quest.xp : quest.emerald;
        }
        return whichGivenRawAmount(playerClass, isXpDesired, (int) (amountPossible * percentageAmount), classLevel, isCollection);
    }

    public static FinalQuestOptions whichGivenRawAmount(WynncraftClass playerClass, boolean isXpDesired, long amountDesired, int classLevel, boolean isIncludeCollection) {
        // if class level is not specified, specify it
        if (classLevel == -1) classLevel = playerClass.combatLevel;

        // make a map of all the quest names to the quest requirements and quests that require itself
        HashMap<String, QuestLinked> nameToQuestLinked = new HashMap<>();
        for (Quest quest : allQuests) {
            if (quest.levelMinimum <= classLevel && !playerClass.questsCompleted.contains(quest.name)) {
                nameToQuestLinked.put(quest.name, new QuestLinked(playerClass, quest));
            }
        }

        // add the reqMe's to the map one at a time (not concurrently)
        for (QuestLinked quest : nameToQuestLinked.values()) {
            for (String req : quest.immediateRequirements) {
                QuestLinked questToAddReqMe = nameToQuestLinked.get(req);
                if (questToAddReqMe != null) {
                    questToAddReqMe.reqMe.add(quest.quest.name);
                }
            }
        }
        List<Quest> singletonQuests = new ArrayList<>();
        List<Collection<QuestLinked>> questCombinations = new ArrayList<>();
        // find all quests that start quest chains
        for (QuestLinked quest : nameToQuestLinked.values()) {
            // if I don't require anyone, and at least somebody requires me
            if (quest.immediateRequirements.isEmpty()) {
                if (!quest.reqMe.isEmpty()) {
                    questCombinations.add(Collections.singletonList(quest));
                } else {
                    // this is a singleton quest
                    singletonQuests.add(quest.quest);
                }
            }
        }
        // sort the singleton quests by order of amount/time
        singletonQuests.sort((o1, o2) -> (int) Math.round((isXpDesired ? o2.xp : o2.emerald) / (isIncludeCollection ? o2.collectionTime + o2.time : o2.time) -
                (isXpDesired ? o1.xp : o1.emerald) / (isIncludeCollection ? o1.collectionTime + o1.time : o1.time)));

        // this is a set of collections of quest names
        Set<String> finalList = new HashSet<>();
        questCombinations.forEach(questsCombo -> {
            Collection<String> questsComboString = new ArrayList<>();
            questsCombo.forEach(questLinked -> questsComboString.add(questLinked.quest.name));
            finalList.add(String.join(",", questsComboString));
        });
        addQuestGivenAmount(questCombinations, nameToQuestLinked, finalList, amountDesired, isXpDesired, isIncludeCollection);

        // turn the questCombos into Objects
        List<FinalQuestComboAmount> finalQuestCombos = new ArrayList<>();
        for (String questComboStringAll : finalList) {
            Collection<Quest> questCombo = new ArrayList<>();
            for (String questComboString : questComboStringAll.split(",")) {
                questCombo.add(nameToQuest.get(questComboString));
            }
            // time is a given
            finalQuestCombos.add(new FinalQuestComboAmount(questCombo, isXpDesired, amountDesired, isIncludeCollection));
        }
        finalQuestCombos.add(new FinalQuestComboAmount(new ArrayList<>(0), isXpDesired, amountDesired, isIncludeCollection));

        // add singleton quests to all the combos
        for (FinalQuestComboAmount finalQuestCombo : finalQuestCombos) {
            for (Quest singletonQuest : singletonQuests) {
                if (finalQuestCombo.getAmountLeft() <= 0) {
                    // we're done with this combo
                    break;
                } else if (!finalQuestCombo.hasQuest(singletonQuest)) {
                    // add this quest
                    finalQuestCombo.addQuest(singletonQuest);
                }
            }
        }

        // remove any combos that are empty
        finalQuestCombos.removeIf(FinalQuestComboAmount::isEmpty);

        FinalQuestComboAmount bestPerTime = finalQuestCombos.stream().max((o1, o2) -> (int) Math.round((o1.amountPerTime() - o2.amountPerTime()))).orElse(null);
        FinalQuestComboAmount bestUtilization = finalQuestCombos.stream().max((o1, o2) -> (int) Math.round((o1.getTime() - o2.getTime()))).orElse(null);

        return new FinalQuestOptions(bestPerTime, bestUtilization);
    }

    private static void addQuestGivenAmount(List<Collection<QuestLinked>> questCombinations, HashMap<String, QuestLinked> nameToQuestLinked, Set<String> finalList, long amountDesired, boolean isXpDesired, boolean isIncludeCollection) {
        // for each collection, add a quest as a new combination
        for (Collection<QuestLinked> questCombination : questCombinations) {
            // if we already met out goal with this quest combination, quit adding more
            long amount = 0L;
            for (QuestLinked quest : questCombination) {
                amount += isXpDesired ? quest.quest.xp : quest.quest.emerald;
            }
            if (amount > amountDesired) {
                // we're finished with this combination
                continue;
            }


            // make a list of combinations that we will add to the final list later
            // (this is also useful for giving a list of quests to the next recursion that passed this round)
            List<Collection<QuestLinked>> subQuestCombinations = new ArrayList<>();

            // make a collection of all the quests that require one of the quests in this questCombination
            Collection<String> reqUs = new HashSet<>();
            for (QuestLinked quest : questCombination) {
                reqUs.addAll(quest.reqMe);
            }

            // for each req, add it as a new combination
            for (String req : reqUs) {

                // if the quest already exists in us, continue
                boolean notHere = true;
                for (QuestLinked quest : questCombination) {
                    if (quest.quest.name.equals(req)) {
                        notHere = false;
                        break;
                    }
                }

                if (notHere) {
                    // make the new quest combination
                    Collection<QuestLinked> newQuestCombination = new ArrayList<>(questCombination);
                    // this is the quest that is new to the quest
                    QuestLinked questToAdd = nameToQuestLinked.get(req);
                    if (questToAdd != null) {
                        newQuestCombination.add(questToAdd);
                        subQuestCombinations.add(newQuestCombination);

                    }
                }
            }
            // do the next layer of recursion
            addQuestGivenAmount(subQuestCombinations, nameToQuestLinked, finalList, amountDesired, isXpDesired, isIncludeCollection);
            subQuestCombinations.forEach(questsCombo -> {
                Collection<String> questsComboString = new ArrayList<>();
                questsCombo.forEach(questLinked -> questsComboString.add(questLinked.quest.name));
                finalList.add(String.join(",", questsComboString));
            });
        }
    }

    private static void addQuestGivenTime(List<Collection<QuestLinked>> questCombinations, HashMap<String, QuestLinked> nameToQuestLinked,
                                          Set<String> finalList, long timeToSpend, boolean isIncludeCollection) {
        // for each collection, add a quest as a new combination
        for (Collection<QuestLinked> questCombination : questCombinations) {
            // make a list of combinations that we will add to the final list later
            // (this is also useful for giving a list of quests to the next recursion that passed this round)
            List<Collection<QuestLinked>> subQuestCombinations = new ArrayList<>();

            // make a collection of all the quests that require one of the quests in this questCombination
            Collection<String> reqUs = new HashSet<>();
            for (QuestLinked quest : questCombination) {
                reqUs.addAll(quest.reqMe);
            }

            // for each req, add it as a new combination
            for (String req : reqUs) {

                // if the quest already exists in us, continue
                boolean notHere = true;
                for (QuestLinked quest : questCombination) {
                    if (quest.quest.name.equals(req)) {
                        notHere = false;
                        break;
                    }
                }

                if (notHere) {
                    // make the new quest combination
                    Collection<QuestLinked> newQuestCombination = new ArrayList<>(questCombination);
                    // this is the quest that is new to the quest
                    QuestLinked questToAdd = nameToQuestLinked.get(req);
                    if (questToAdd != null) {
                        newQuestCombination.add(questToAdd);
                        long time = 0L;
                        for (QuestLinked quest : newQuestCombination) {
                            time += isIncludeCollection ? quest.quest.time + quest.quest.collectionTime : quest.quest.time;
                        }
                        if (time <= timeToSpend)
                            subQuestCombinations.add(newQuestCombination);
                    }
                }
            }
            // do the next layer of recursion
            addQuestGivenTime(subQuestCombinations, nameToQuestLinked, finalList, timeToSpend, isIncludeCollection);
            subQuestCombinations.forEach(questsCombo -> {
                Collection<String> questsComboString = new ArrayList<>();
                questsCombo.forEach(questLinked -> questsComboString.add(questLinked.quest.name));
                finalList.add(String.join(",", questsComboString));
            });
        }
    }
}
