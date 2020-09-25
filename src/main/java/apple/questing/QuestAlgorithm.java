package apple.questing;

import apple.questing.data.*;
import apple.questing.utils.Pair;

import java.util.*;

import static apple.questing.sheets.SheetsQuery.allQuests;
import static apple.questing.sheets.SheetsQuery.nameToQuest;

public class QuestAlgorithm {
    public static List<Quest> which(WynncraftPlayer player, boolean isXpDesired, long timeToSpend, long amountDesired) {
        for (Quest quest : allQuests) {

        }
        return null;
    }

    public static List<Quest> whichGivenTime(WynncraftClass playerClass, boolean isXpDesired, long timeToSpend,
                                             int classLevel, boolean isIncludeCollection) {
        // if class level is not specified, specify it
        if (classLevel == -1) classLevel = playerClass.level;

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

        List<Collection<QuestLinked>> questCombinations = new ArrayList<>();
        // find all quests that start quest chains
        for (QuestLinked quest : nameToQuestLinked.values()) {
            // if I don't require anyone, and at least somebody requires me and this quest isn't too long
            if (quest.immediateRequirements.isEmpty() && !quest.reqMe.isEmpty() &&
                    timeToSpend >= (isIncludeCollection ? quest.quest.time + quest.quest.collectionTime : quest.quest.time)) {
                questCombinations.add(Collections.singletonList(quest));
            }
        }
        ArrayList<Collection<QuestLinked>> finalList = new ArrayList<>(questCombinations);
        addQuest(questCombinations, nameToQuestLinked, finalList, timeToSpend, isIncludeCollection);
        printCombo(finalList);



        return null;
    }

    private static void printCombo(List<Collection<QuestLinked>> questCombinations) {
        for (Collection<QuestLinked> questCombination : questCombinations) {
            for (QuestLinked quest : questCombination) {
                System.out.print(quest.quest.name + " ");
            }
            System.out.println();
        }
    }

    private static void addQuest(List<Collection<QuestLinked>> questCombinations, HashMap<String, QuestLinked> nameToQuestLinked,
                                 List<Collection<QuestLinked>> finalList, long timeToSpend, boolean isIncludeCollection) {
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
            finalList.addAll(subQuestCombinations);
        }
    }
}
