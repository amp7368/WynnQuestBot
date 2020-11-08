package apple.questing.discord.commands;

import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.discord.reactables.class_choice.ClassChoiceMessage;
import apple.questing.wynncraft.GetPlayerStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
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
     * -l # - default is that class's level. overrides what level the player is
     *
     * @param event the discord message event
     */
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        List<String> contentSplit = new ArrayList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));

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

        EmbedBuilder embedClassChoice = new EmbedBuilder();
        StringBuilder classChoiceDescription = new StringBuilder();
        List<String> classes = new ArrayList<>();
        int countClass = 0;
        for (WynncraftClass wynncraftClass : player.classes) {
            classes.add(wynncraftClass.name);
            classChoiceDescription.append(ClassChoiceMessage.emojiAlphabet.get(countClass++));
            classChoiceDescription.append('`');
            classChoiceDescription.append(ClassChoiceMessage.getSingleClassMessage(wynncraftClass));
            classChoiceDescription.append("`\n");
        }

        embedClassChoice.setTitle("React according to which class you want analyzed");
        embedClassChoice.setDescription(classChoiceDescription);

        Message message = event.getChannel().sendMessage(embedClassChoice.build()).complete();
        for (int i = 0; i < countClass; i++)
            message.addReaction(ClassChoiceMessage.emojiAlphabet.get(i)).queue();

        new ClassChoiceMessage(
                message.getIdLong(),
                isXpDesired,
                isCollection,
                timeToSpend,
                amountDesired,
                percentageDesired,
                classLevel,
                classes,
                player,
                ClassChoiceMessage.ChoiceMessageType.SPECIFIC
        );
    }

}
