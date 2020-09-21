package apple.questing.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandQuest implements DoCommand {
    /**
     * q!quest <player_name> [-x] [-t <how much that wants to be spent questing> | -e <how many emeralds or raw xp that player wants>]</how>
     * <p>
     * player_name - name of the player who you're trying to decide which quests to do
     * -x - optional flag to specify that the player wants to maximize xp from quests
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

    }
}
