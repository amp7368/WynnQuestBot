package apple.questing;

import apple.questing.data.combo.FinalQuestCombo;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.quest.Quest;
import apple.questing.data.quest.QuestLinked;
import apple.questing.utils.Pair;

import java.util.*;

import static apple.questing.sheets.SheetsQuery.allQuests;

public class QuestAlgorithm {
    public static Pair<FinalQuestCombo, FinalQuestCombo> whichGivenTime(
            WynncraftPlayer player, boolean isXpDesired, long timeToSpend, int classLevel, boolean isIncludeCollection) {
        // if classLevel was overridden
        if (classLevel != -1) {
            // update all the classes to have the overridden classLevel
            for (WynncraftClass wynncraftClass : player.classes) {
                wynncraftClass.combatLevel = classLevel;
            }
        }

        // make a map of classes to a map of all the quest names to the quest requirements and quests that require itself
        Map<String, Map<String, QuestLinked>> classToNameToQuestLinked = new HashMap<>();
        for (WynncraftClass playerClass : player.classes) {
            Map<String, QuestLinked> nameToQuestLinked = new HashMap<>();
            for (Quest quest : allQuests) {
                // if the player can do this quest
                if (quest.levelMinimum <= playerClass.combatLevel && !playerClass.questsCompleted.contains(quest.name)) {
                    // add it to the list
                    nameToQuestLinked.put(quest.name, new QuestLinked(playerClass, quest));
                }
            }
            // add the nameToQuestLinked for this class to the map of classesToNameToQuestLinked
            classToNameToQuestLinked.put(playerClass.name, nameToQuestLinked);
        }

        classToNameToQuestLinked.forEach((className, nameToQuestLinked) -> {
            // add the reqMe's to the map one at a time (not concurrently)
            for (QuestLinked quest : nameToQuestLinked.values()) {
                for (String req : quest.immediateRequirements) {
                    QuestLinked questToAddReqMe = nameToQuestLinked.get(req);
                    if (questToAddReqMe != null) {
                        questToAddReqMe.reqMe.add(quest.name);
                    }
                }
            }
        });

        // get all the singletonQuests. they don't need to be sorted based on class
        List<QuestLinked> singletonQuests = new ArrayList<>();
        // get all the complexQuests and put them in singleton collections to add quests on later
        Map<String, List<Set<QuestLinked>>> classToQuestCombinations = new HashMap<>();

        // sort the quests
        for (WynncraftClass playerClass : player.classes) {
            List<Set<QuestLinked>> questCombinations = new ArrayList<>();
            // find all quests that start quest chains
            for (QuestLinked quest : classToNameToQuestLinked.get(playerClass.name).values()) {
                // if I don't require anyone, and at least somebody requires me
                if (quest.immediateRequirements.isEmpty()) {
                    if (!quest.reqMe.isEmpty()) {
                        Set<QuestLinked> questCombination = new HashSet<>();
                        questCombination.add(quest);
                        questCombinations.add(questCombination);
                    } else {
                        // this is a singleton quest
                        if (!isXpDesired && quest.emerald > 0)
                            singletonQuests.add(quest);
                    }
                }
            }
            classToQuestCombinations.put(playerClass.name, questCombinations);
        }


        // sort the singleton quests by order of amount/time
        singletonQuests.sort((o1, o2) -> (int) Math.round((isXpDesired ? o2.xp : o2.emerald) / (isIncludeCollection ? o2.collectionTime + o2.time : o2.time) -
                (isXpDesired ? o1.xp : o1.emerald) / (isIncludeCollection ? o1.collectionTime + o1.time : o1.time)));

        // this is a set of sets of quest names
        Set<Set<QuestLinked>> questCombinationsForAll = new HashSet<>();
        for (WynncraftClass playerClass : player.classes) {
            questCombinationsForAll.addAll(classToQuestCombinations.get(playerClass.name));
        }

        // this is the map of questNames to the questLinks that the name refers to
        // this is a mapping to a list because there may be several Quests to
        // one name because there are multiple classes
        Map<String, List<QuestLinked>> nameToQuestLinkeds = new HashMap<>();
        for (Map.Entry<String, Map<String, QuestLinked>> classAndNameToQuestLinked : classToNameToQuestLinked.entrySet()) {
            for (Map.Entry<String, QuestLinked> nameAndQuestLinked : classAndNameToQuestLinked.getValue().entrySet()) {
                List<QuestLinked> questLinkeds = nameToQuestLinkeds.get(nameAndQuestLinked.getKey());
                if (questLinkeds == null) {
                    List<QuestLinked> listOfQuestLinkeds = new ArrayList<>();
                    listOfQuestLinkeds.add(nameAndQuestLinked.getValue());
                    nameToQuestLinkeds.put(nameAndQuestLinked.getKey(), listOfQuestLinkeds);
                } else {
                    questLinkeds.add(nameAndQuestLinked.getValue());
                }

            }
        }
        // try all quest lines in varying lengths
        addQuestGivenTime(questCombinationsForAll, nameToQuestLinkeds, timeToSpend, isIncludeCollection);

        List<Set<QuestLinked>> questCombinationsForAllList = new ArrayList<>(questCombinationsForAll);
        questCombinationsForAllList.sort((o1, o2) -> {
            long time1 = 0;
            long time2 = 0;
            long reward1 = 0;
            long reward2 = 0;
            for (QuestLinked quest : o1) {
                time1 += isIncludeCollection ? quest.collectionTime + quest.time : quest.time;
                reward1 += isXpDesired ? quest.xp : quest.emerald;
            }
            for (QuestLinked quest : o2) {
                time2 += isIncludeCollection ? quest.collectionTime + quest.time : quest.time;
                reward2 += isXpDesired ? quest.xp : quest.emerald;
            }
            return (int) (reward2 / time2 - reward1 / time1);
        });

        List<Set<QuestLinked>> finalQuestCombinations = new ArrayList<>();
        finalQuestCombinations.add(new HashSet<>()); // for no questline
        // add a questLine at a time, and at each step, save that combo
        for (Set<QuestLinked> singleQuestCombination : questCombinationsForAllList) {
            Set<QuestLinked> questCombination = new HashSet<>(finalQuestCombinations.get(finalQuestCombinations.size() - 1));
            questCombination.addAll(singleQuestCombination);
            if (isTakesToLong(questCombination, timeToSpend, isIncludeCollection))
                break;
            else
                finalQuestCombinations.add(questCombination);
        }

        // add singleton quests
        for (Set<QuestLinked> questCombination : finalQuestCombinations) {
            for (QuestLinked singletonQuest : singletonQuests) {
                Set<QuestLinked> questCombinationWithSingleton = new HashSet<>(questCombination);
                questCombinationWithSingleton.add(singletonQuest);
                if (!isTakesToLong(questCombinationWithSingleton, timeToSpend, isIncludeCollection)) {
                    questCombination.add(singletonQuest);
                }
                // move on to the next combination to add singletons to
            }
        }
        finalQuestCombinations.removeIf(Set::isEmpty);
        finalQuestCombinations.sort((o1, o2) -> {
            long time1 = 0;
            long time2 = 0;
            long reward1 = 0;
            long reward2 = 0;
            for (QuestLinked quest : o1) {
                time1 += isIncludeCollection ? quest.collectionTime + quest.time : quest.time;
                reward1 += isXpDesired ? quest.xp : quest.emerald;
            }
            for (QuestLinked quest : o2) {
                time2 += isIncludeCollection ? quest.collectionTime + quest.time : quest.time;
                reward2 += isXpDesired ? quest.xp : quest.emerald;
            }
            return (int) (reward2 / time2 - reward1 / time1);
        });
        Set<QuestLinked> optimizeAPT = finalQuestCombinations.get(0);
        finalQuestCombinations.sort((o1, o2) -> {
            long reward1 = 0;
            long reward2 = 0;
            for (QuestLinked quest : o1) {
                reward1 += isXpDesired ? quest.xp : quest.emerald;
            }
            for (QuestLinked quest : o2) {
                reward2 += isXpDesired ? quest.xp : quest.emerald;
            }
            return (int) (reward2 - reward1);
        });
        Set<QuestLinked> optimizeAmount = finalQuestCombinations.get(0);

        return new Pair<>(new FinalQuestCombo(new ArrayList<>(optimizeAPT), isXpDesired, isIncludeCollection),
                new FinalQuestCombo(new ArrayList<>(optimizeAmount), isXpDesired, isIncludeCollection));
    }

    /**
     * recursively add a single quest to quest lines
     *
     * @param finalSet            the final set of combinations of quests that we add to
     * @param nameToQuestLinkeds  the map of questNames to the questLinks that the name refers to
     *                            this is a mapping to a list because there may be several Quests to
     *                            one name because there are multiple classes
     * @param timeToSpend         the time that we have to spend doing quests
     * @param isIncludeCollection whether we include collection in our decision
     */
    private static void addQuestGivenTime(Set<Set<QuestLinked>> finalSet, Map<String, List<QuestLinked>> nameToQuestLinkeds,
                                          long timeToSpend, boolean isIncludeCollection) {
        Set<Set<QuestLinked>> questsToAdd = new HashSet<>();

        // for every combination we have, try to add a new one as a new entry
        for (Set<QuestLinked> questCombination : finalSet) {
            // for every quest in the questCombination add all the reqMe's
            for (QuestLinked questInCombination : questCombination) {
                for (String reqMe : questInCombination.reqMe) {
                    // find the corresponding reqMeQuestLinked
                    List<QuestLinked> questLinkeds = nameToQuestLinkeds.get(reqMe);
                    if (questLinkeds == null) {
                        // there is no corresponding reqMeQuestLinked
                        break;
                    }
                    for (QuestLinked questLinked : questLinkeds) {
                        if (questInCombination.playerClass.name.equals(questLinked.playerClass.name)) {
                            // add this corresponding reqMeQuestLinked
                            Set<QuestLinked> newQuestCombination = new HashSet<>(questCombination);
                            newQuestCombination.add(questLinked);
                            if (!isTakesToLong(newQuestCombination, timeToSpend, isIncludeCollection) && !finalSet.contains(newQuestCombination)) {
                                questsToAdd.add(newQuestCombination); // this won't always succeed, and that's the key to keeping the numbers low
                            }
                            break;
                        }
                    }
                    // otherwise we didn't find a corresponding reqMeQuestLinked, and that's fine
                }
            }
        }
        if (questsToAdd.size() == 0)
            return;
        addQuestGivenTime(questsToAdd, nameToQuestLinkeds, timeToSpend, isIncludeCollection);
        finalSet.addAll(questsToAdd);
    }

    private static boolean isTakesToLong(Set<QuestLinked> quests, long timeToSpend, boolean isIncludeCollection) {
        double timeSpent = 0;
        for (QuestLinked quest : quests)
            timeSpent += isIncludeCollection ? quest.collectionTime + quest.time : quest.time;
        return timeSpent > timeToSpend;
    }
}
