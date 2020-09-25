package apple.questing;

import apple.questing.data.*;

import java.util.*;

import static apple.questing.sheets.SheetsQuery.allQuests;
import static apple.questing.sheets.SheetsQuery.nameToQuest;

public class QuestAlgorithm {
    public static List<Quest> which(WynncraftPlayer player, boolean isXpDesired, long timeToSpend, long amountDesired) {
        return null;
    }

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
        addQuest(questCombinations, nameToQuestLinked, finalList, timeToSpend, isIncludeCollection);

        // add singleton quests that stay under the limit of time
        List<FinalQuestCombo> finalQuestCombos = new ArrayList<>();
        for (String questComboStringAll : finalList) {
            Collection<Quest> questCombo = new ArrayList<>();
            for (String questComboString : questComboStringAll.split(",")) {
                questCombo.add(nameToQuest.get(questComboString));
            }
            // time is a given
            finalQuestCombos.add(new FinalQuestCombo(questCombo, isXpDesired, true, timeToSpend, isIncludeCollection));
        }
        finalQuestCombos.add(new FinalQuestCombo(new ArrayList<>(0), isXpDesired, true, timeToSpend, isIncludeCollection));

        // add singleton quests to all the combos
        for (FinalQuestCombo finalQuestCombo : finalQuestCombos) {
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
        finalQuestCombos.removeIf(FinalQuestCombo::isEmpty);

        FinalQuestCombo bestPerTime = finalQuestCombos.stream().max((o1, o2) -> (int) Math.round((o1.amountPerTime() - o2.amountPerTime()))).orElse(null);

        // add singleton quests to all the combos
        for (FinalQuestCombo finalQuestCombo : finalQuestCombos) {
            for (Quest singletonQuest : singletonQuests) {
                if (!finalQuestCombo.hasQuest(singletonQuest) && !((isIncludeCollection ? singletonQuest.collectionTime + singletonQuest.time : singletonQuest.time) > finalQuestCombo.getTimeToUse())) {
                    // add this quest
                    finalQuestCombo.addQuest(singletonQuest);
                }
            }
        }
        FinalQuestCombo bestUtilization = finalQuestCombos.stream().max((o1, o2) -> (int) Math.round((o1.amountPerTime() - o2.amountPerTime()))).orElse(null);


        return new FinalQuestOptions(bestPerTime, bestUtilization);
    }

    private static void addQuest(List<Collection<QuestLinked>> questCombinations, HashMap<String, QuestLinked> nameToQuestLinked,
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
            addQuest(subQuestCombinations, nameToQuestLinked, finalList, timeToSpend, isIncludeCollection);
            subQuestCombinations.forEach(questsCombo -> {
                Collection<String> questsComboString = new ArrayList<>();
                questsCombo.forEach(questLinked -> questsComboString.add(questLinked.quest.name));
                finalList.add(String.join(",", questsComboString));
            });
        }
    }
}
