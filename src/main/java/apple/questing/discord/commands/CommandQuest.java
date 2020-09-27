package apple.questing.discord.commands;

import apple.questing.SpecificQuestAlgorithm;
import apple.questing.data.FinalQuestOptions;
import apple.questing.data.WynncraftPlayer;
import apple.questing.wynncraft.GetPlayerStats;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandQuest implements DoCommand {
    /**
     * q!quest <player_name> [-x] [-c] [-t <how much that wants to be spent questing> | -e <how many emeralds or raw xp that player wants>]
     * <p>
     * player_name - name of the player who you're trying to decide which quests to do
     * -x - optional flag to specify that the player wants to maximize xp from quests
     * -c - optional flag to specify that the player doesn't want to include collection time
     * -t # - default is not used. this optional argument overrides how much time the player wants to spend doing quests
     * -e # - default is 75% of the max the player can earn
     * <p>
     * -l # - (only for squest) default is that class's level. overrides what level the player is
     *
     * @param event the discord message event
     */
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        boolean isXpDesired = false;
        boolean isCollection = true;
        long timeToSpend = -1;
        long amountDesired = -1;
        String username;
        List<String> contentSplit = new ArrayList<>(Arrays.asList(event.getMessage().getContentStripped().split(" ")));

        // remove the command part of the message
        contentSplit.remove(0);

        //find -x if it exists
        if (contentSplit.remove("-x")) {
            isXpDesired = true;
        }
        //find -c if it exists
        if (contentSplit.remove("-c")) {
            isCollection = false;
        }
        int size = contentSplit.size();
        for (int i = 0; i < size; i++) {
            if (contentSplit.get(i).equals("-t")) {
                if (size == i + 1) {
                    // user did -t at the end of their message without an argument
                    event.getChannel().sendMessage("-t requires a number after it to specify how much time you want to spend doing quests").queue();
                    return;
                } else {
                    contentSplit.remove(i);
                    String time = contentSplit.remove(i);
                    try {
                        timeToSpend = Long.parseLong(time);
                    } catch (NumberFormatException e) {
                        // user's -t argument is not a number
                        event.getChannel().sendMessage("-t requires a number after it to specify how much time you want to spend doing quests\n'" + time + "' is not a number.").queue();
                        return;
                    }
                    break;
                }
            }
        }
        size = contentSplit.size();
        for (int i = 0; i < size; i++) {
            if (contentSplit.get(i).equals("-e")) {
                if (size == i + 1) {
                    // user did -e at the end of their message without an argument
                    event.getChannel().sendMessage("-e requires a number after it to specify how many emeralds or xp you want from quests").queue();
                    return;
                } else {
                    contentSplit.remove(i);
                    String amount = contentSplit.remove(i);
                    try {
                        amountDesired = Long.parseLong(amount);
                    } catch (NumberFormatException e) {
                        // user's -e argument is not a number
                        event.getChannel().sendMessage("-e requires a number after it to specify how many emeralds or xp you want from quests\n'" + amount + "' is not a number.").queue();
                        return;
                    }
                    break;
                }
            }
        }
        if (contentSplit.isEmpty()) {
            // user did not specify what player they want
            event.getChannel().sendMessage("Specify what player you want to analyze.").queue();
            return;
        }
        username = contentSplit.get(0);
        event.getChannel().sendMessage(String.format("isXpDesired: %b\ntimeToSpend: %d\namount desired:%d\nusername: %s", isXpDesired, timeToSpend, amountDesired, username)).queue();
        WynncraftPlayer player = GetPlayerStats.get(username);
        if (player == null) {
            event.getChannel().sendMessage("Either the api is down, or '" + username + "' is not a player.").queue();
            return;
        }
        /*for (Quest quest : SheetsQuery.allQuests) {
            System.out.println("'" + quest.name + "'");
        }
        System.out.println("\n\n-----------------------------------------------\n\n");

        // quests that are in the class that aren't in all quests
        for (WynncraftClass wynncraftClass : player.classes) {
            ArrayList<String> questsNotRegistered = new ArrayList<>(wynncraftClass.questsCompleted);
            SheetsQuery.allQuests.forEach(quest -> questsNotRegistered.remove(quest.name));
            System.out.println(String.join("\n", "'" + questsNotRegistered + "'"));

            System.out.println("\n");
        }
        System.out.println("\n\n-----------------------------------------------\n\n");

        // quests the player has not completed
        for (WynncraftClass wynncraftClass : player.classes) {
            HashMap<String, Quest> questsNotDone = new HashMap<>();
            SheetsQuery.allQuests.forEach(quest -> questsNotDone.put(quest.name, quest));
            for (String quest : wynncraftClass.questsCompleted) {
                questsNotDone.remove(quest);
            }
            ArrayList<Quest> questsNotDoneList = new ArrayList<>();
            questsNotDone.forEach((questName, quest) -> questsNotDoneList.add(quest));
            questsNotDoneList.sort((o1, o2) -> o2.emerald - o1.emerald);
            System.out.println(wynncraftClass.level);
            for (Quest quest : questsNotDoneList) {
                System.out.println("'" + quest.name + "'");
            }
            System.out.println("\n");
        }*/
        //todo switch this for what the actual command does
        System.out.println("starting");
        FinalQuestOptions questsToDo = SpecificQuestAlgorithm.whichGivenTime(player.classes.get(13), isXpDesired, timeToSpend, 105, true);
        System.out.println("done");
    }
}
