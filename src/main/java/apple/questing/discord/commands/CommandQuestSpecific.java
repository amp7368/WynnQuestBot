package apple.questing.discord.commands;

import apple.questing.QuestAlgorithm;
import apple.questing.data.FinalQuestOptions;
import apple.questing.data.WynncraftClass;
import apple.questing.data.WynncraftPlayer;
import apple.questing.data.reaction.AllReactableClassChoices;
import apple.questing.data.reaction.ClassChoiceMessage;
import apple.questing.wynncraft.GetPlayerStats;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandQuestSpecific implements DoCommand {
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
        int classLevel = -1;
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
            if (contentSplit.get(i).equals("-l")) {
                if (size == i + 1) {
                    // user did -t at the end of their message without an argument
                    event.getChannel().sendMessage("-l requires a number after it to specify how much time you want to spend doing quests").queue();
                    return;
                } else {
                    contentSplit.remove(i);
                    String time = contentSplit.remove(i);
                    try {
                        classLevel = Integer.parseInt(time);
                    } catch (NumberFormatException e) {
                        // user's -t argument is not a number
                        event.getChannel().sendMessage("-l requires a number after it to specify how much time you want to spend doing quests\n'" + time + "' is not a number.").queue();
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
        WynncraftPlayer player = GetPlayerStats.get(username);
        if (player == null) {
            event.getChannel().sendMessage("Either the api is down, or '" + username + "' is not a player.").queue();
            return;
        }

        List<String> classes = new ArrayList<>();
        for(WynncraftClass wynncraftClass:player.classes){
            classes.add(wynncraftClass.name);
        }
        Message message = event.getChannel().sendMessage("hello").complete();
        AllReactableClassChoices.addMessage(
                new ClassChoiceMessage(
                        message.getId(),
                        System.currentTimeMillis(),
                        isXpDesired,
                        isCollection,
                        timeToSpend,
                        amountDesired,
                        classLevel,
                        classes
                )
        );
        //todo switch this for what the actual command does
        System.out.println("starting");
        if (timeToSpend != -1) {
            FinalQuestOptions questsToDo = QuestAlgorithm.whichGivenTime(player.classes.get(13), isXpDesired, timeToSpend, classLevel, isCollection);


            questsToDo.print();
        }
        System.out.println("done");
    }
}
