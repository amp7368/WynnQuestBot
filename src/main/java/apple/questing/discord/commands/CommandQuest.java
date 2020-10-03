package apple.questing.discord.commands;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.quest.Quest;
import apple.questing.discord.DiscordBot;
import apple.questing.discord.reactables.class_choice.ChoiceArguments;
import apple.questing.discord.reactables.reccomendation.QuestRecommendationMessagePlayer;
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

        // be done if something bad was found
        if (timeToSpend == -2 || classLevel == -2 || amountDesired == -2)
            return;

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

        // tell the user we're working on the answer
        event.getMessage().addReaction("\uD83D\uDEE0").complete();

        List<String> classNames = new ArrayList<>();
        for (WynncraftClass playerClass : player.classes) {
            classNames.add(playerClass.name);
        }
        ChoiceArguments choiceArguments = new ChoiceArguments(
                isXpDesired, isCollection, timeToSpend, amountDesired, classLevel, classNames, player, true);


        long xpDesiredGivenPerc = 0;
        long emeraldDesiredGivenPerc = 0;
        for (WynncraftClass wynncraftClass : player.classes)
            for (Quest quest : wynncraftClass.questsNotCompleted) {
                if (quest.levelMinimum <= (classLevel == -1 ? wynncraftClass.combatLevel : classLevel)) {
                    xpDesiredGivenPerc += quest.xp;
                    emeraldDesiredGivenPerc += quest.emerald;
                }
            }
        xpDesiredGivenPerc *= GetAnswers.DEFAULT_PERCENTAGE_AMOUNT;
        emeraldDesiredGivenPerc *= GetAnswers.DEFAULT_PERCENTAGE_AMOUNT;

        FinalQuestOptionsAll finalQuestOptionsAll = GetAnswers.getAllFullAnswers(player, choiceArguments);
        String spreadsheetId = SheetsWrite.writeSheet(finalQuestOptionsAll, event.getAuthor().getIdLong(), player.name, true);
        if (spreadsheetId == null) return;
        new QuestRecommendationMessagePlayer(spreadsheetId, finalQuestOptionsAll, event.getChannel(), choiceArguments, xpDesiredGivenPerc, emeraldDesiredGivenPerc);

        event.getMessage().removeReaction("\uD83D\uDEE0", DiscordBot.client.getSelfUser()).complete();
    }
}
