package apple.questing.discord.commands;

import apple.questing.GetAnswers;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static apple.questing.discord.DiscordBot.*;

public class CommandHelp implements DoCommand {
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        @SuppressWarnings("StringBufferReplaceableByString") StringBuilder messageText = new StringBuilder();
        messageText.append("```md\n");
        messageText.append("# " + PREFIX + QUEST_SPECIFIC_COMMAND + " (playerName) [-x] [-c] [-t (time spent questing)] [-e (amount desired)]\n");
        messageText.append("<. does a quest analysis on a particular class that a player has>\n\n");
        messageText.append("# " + PREFIX + QUEST_COMMAND + " (playerName) [-x] [-c] [-t (time spent questing)] [-e (amount desired)]\n");
        messageText.append("<. does a quest analysis on all classes that a player has as a whole>\n\n");
        messageText.append("# " + PREFIX + RANK_COMMAND +"\n");
        messageText.append("<. does a quest analysis assuming the player is max level and has one class>\n\n");
        messageText.append("# arguments for " + PREFIX + QUEST_COMMAND + ", " + PREFIX + QUEST_SPECIFIC_COMMAND +", and " + PREFIX + RANK_COMMAND + "]\n");
        messageText.append(String.format(".%13s", "[playerName]")).append("[name of the player who you're trying to decide which quests to do]\n");
        messageText.append(String.format(".%13s", "[-x]"));
        messageText.append("[optional flag to specify that the player wants to maximize xp from quests]\n");
        messageText.append(String.format(".%13s", "[-c]"));
        messageText.append("[optional flag to specify that the player doesn't want to include collection time]\n");
        messageText.append(String.format(".%13s", "[-t #]"));
        messageText.append("[this optional argument specifies how much time the player wants to spend doing quests]\n");
        messageText.append(String.format(".%13s", "[-e #]"));
        messageText.append("[this optional argument specifies how many emeralds/xp that the player wants to earn]\n");
        messageText.append(String.format(".%13s", "[-p #]"));
        messageText.append("[default is " + GetAnswers.DEFAULT_PERCENTAGE_AMOUNT + ". Overrides what percentage of the total reward possible]\n");
        messageText.append(String.format(".%13s", "[-l #]"));
        messageText.append("[default is that class's level. Overrides what level the player is]\n\n");
        messageText.append("# " + PREFIX + BOOK_COMMAND + " (playerName)\n");
        messageText.append("<. gives the quest book of a player's class>\n\n");
        messageText.append("# " + PREFIX + LOOKUP_COMMAND + " (questName)\n");
        messageText.append("<. gives any quests that contain the name questName>\n\n");
        messageText.append("# " + PREFIX + HELP_COMMAND + "\n");
        messageText.append("<. shows this help message>\n");
        messageText.append("```");
        event.getChannel().sendMessage(messageText.toString()).queue();
    }
}
