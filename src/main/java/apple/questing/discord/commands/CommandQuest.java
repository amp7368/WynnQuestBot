package apple.questing.discord.commands;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.reaction.ChoiceArguments;
import apple.questing.discord.pageable.QuestRecommendationMessagePlayer;
import apple.questing.sheets.SheetsWrite;
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
     * -l # - default is that class's level. overrides what level the player is
     *
     * @param event the discord message event
     */
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        List<String> contentSplit = new ArrayList<>(Arrays.asList(event.getMessage().getContentStripped().split(" ")));

        // remove the command part of the message
        contentSplit.remove(0);
        boolean isXpDesired = DetermineArguments.determineIsXpDesired(contentSplit);
        boolean isCollection = DetermineArguments.determineIsCollection(contentSplit);
        long timeToSpend = DetermineArguments.determineTimeToSpend(contentSplit, event.getTextChannel());
        int classLevel = DetermineArguments.determineClassLevel(contentSplit, event.getTextChannel());
        long amountDesired = DetermineArguments.determineAmountDesired(contentSplit, event.getTextChannel());
        if (contentSplit.isEmpty()) {
            // user did not specify what player they want
            event.getChannel().sendMessage("Specify what player you want to analyze.").queue();
            return;
        }
        String username = contentSplit.get(0);
        WynncraftPlayer player = GetPlayerStats.get(username);
        if (player == null) {
            event.getChannel().sendMessage("Either the api is down, or '" + username + "' is not a player.").queue();
            return;
        }
        List<String> classNames = new ArrayList<>();
        for (WynncraftClass playerClass : player.classes) {
            classNames.add(playerClass.name);
        }
        ChoiceArguments choiceArguments = new ChoiceArguments(
                isXpDesired, isCollection, timeToSpend, amountDesired, classLevel, classNames, player);
        FinalQuestOptionsAll finalQuestOptionsAll = GetAnswers.getAllFullAnswers(player, isXpDesired, isCollection, classLevel, timeToSpend, amountDesired);
        new QuestRecommendationMessagePlayer(player, finalQuestOptionsAll, event.getChannel(), choiceArguments);
        SheetsWrite.writeSheet(finalQuestOptionsAll, event.getAuthor().getIdLong());

    }
}
