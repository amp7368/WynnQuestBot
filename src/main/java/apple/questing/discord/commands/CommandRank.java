package apple.questing.discord.commands;

import apple.questing.GetAnswers;
import apple.questing.data.answer.FinalQuestOptionsAll;
import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.quest.Quest;
import apple.questing.discord.DiscordBot;
import apple.questing.discord.reactables.class_choice.ChoiceArguments;
import apple.questing.discord.reactables.reccomendation.QuestReccomendationMessageClass;
import apple.questing.discord.reactables.reccomendation.QuestRecommendationMessagePlayer;
import apple.questing.sheets.SheetsQuery;
import apple.questing.sheets.SheetsWrite;
import apple.questing.wynncraft.GetPlayerStats;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static apple.questing.discord.reactables.class_choice.ClassChoiceMessage.WORKING_EMOJI;

public class CommandRank implements DoCommand {

    /**
     * q!rank [-x] [-c] [-t <how much that wants to be spent questing> | -e <how many emeralds or raw xp that player wants>]
     * <p>
     * player_name - name of the player who you're trying to decide which quests to do
     * -x - optional flag to specify that the player wants to maximize xp from quests
     * -c - optional flag to specify that the player doesn't want to include collection time
     * -t # - default is not used. this optional argument overrides how much time the player wants to spend doing quests
     * -e # - default is 75% of the max the player can earn
     * -l # - default is that class's level. overrides what level the player is
     *
     * @param event the discord message event
     */
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        List<String> contentSplit = new ArrayList<>(Arrays.asList(event.getMessage().getContentStripped().split(" ")));

        // remove the command part of the message
        contentSplit.remove(0);

        final TextChannel channel = event.getTextChannel();
        boolean isXpDesired = DetermineArguments.determineIsXpDesired(contentSplit);
        boolean isCollection = DetermineArguments.determineIsCollection(contentSplit);
        long timeToSpend = DetermineArguments.determineTimeToSpend(contentSplit, channel);
        int classLevel = DetermineArguments.determineClassLevel(contentSplit, channel);
        long amountDesired = DetermineArguments.determineAmountDesired(contentSplit, channel);
        double percentageDesired = DetermineArguments.determinePercentageDesired(contentSplit, channel);
        // be done if something bad was found
        if (timeToSpend == -2 || classLevel == -2 || amountDesired == -2 || percentageDesired == -2)
            return;

        // tell the user we're working on the answer
        event.getMessage().addReaction(WORKING_EMOJI).queue();

        WynncraftClass wynncraftClass = new WynncraftClass(WynncraftClass.MAX_LEVEL, event.getAuthor().getName(), WynncraftClass.MAX_LEVEL, 0, SheetsQuery.allQuests);
        List<String> classNames = Collections.singletonList(wynncraftClass.name);
        final WynncraftPlayer player = new WynncraftPlayer(Collections.singletonList(wynncraftClass), "Ranking");
        ChoiceArguments choiceArguments = new ChoiceArguments(
                isXpDesired, isCollection, timeToSpend, amountDesired, percentageDesired, classLevel, classNames,
                player, true);

        long xpDesiredGivenPerc = 0;
        long emeraldDesiredGivenPerc = 0;
        for (Quest quest : wynncraftClass.questsNotCompleted) {
            if (quest.levelMinimum <= (classLevel == -1 ? wynncraftClass.combatLevel : classLevel)) {
                xpDesiredGivenPerc += quest.xp;
                emeraldDesiredGivenPerc += quest.emerald;
            }
        }
        xpDesiredGivenPerc *= choiceArguments.percentageDesired == -1 ? GetAnswers.DEFAULT_PERCENTAGE_AMOUNT : choiceArguments.percentageDesired;
        emeraldDesiredGivenPerc *= choiceArguments.percentageDesired == -1 ? GetAnswers.DEFAULT_PERCENTAGE_AMOUNT : choiceArguments.percentageDesired;

        FinalQuestOptionsAll finalQuestOptionsAll = GetAnswers.getAllFullAnswers(player, choiceArguments);
        String spreadsheetId = SheetsWrite.writeSheet(finalQuestOptionsAll, event.getAuthor().getIdLong(), player.name, true);
        if (spreadsheetId == null) return;

        new QuestReccomendationMessageClass(spreadsheetId, wynncraftClass, finalQuestOptionsAll, event.getChannel(), choiceArguments, xpDesiredGivenPerc, emeraldDesiredGivenPerc);

        event.getMessage().removeReaction(WORKING_EMOJI, DiscordBot.client.getSelfUser()).queue();
    }
}
