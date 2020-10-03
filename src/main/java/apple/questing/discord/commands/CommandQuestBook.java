package apple.questing.discord.commands;

import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.discord.reactables.class_choice.ClassChoiceMessage;
import apple.questing.wynncraft.GetPlayerStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandQuestBook implements DoCommand {
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        List<String> contentSplit = new ArrayList<>(Arrays.asList(event.getMessage().getContentStripped().split(" ")));
        contentSplit.remove(0);

        int classLevel = DetermineArguments.determineClassLevel(contentSplit, event.getTextChannel());
        boolean isCollection = DetermineArguments.determineIsCollection(contentSplit);
        if (classLevel == -2) return;

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
                false,
                isCollection,
                -1,
                -1,
                classLevel,
                player.classes.stream().map(wynncraftClass -> wynncraftClass.name).collect(Collectors.toList()),
                player,
                ClassChoiceMessage.ChoiceMessageType.BOOK
        );
    }
}
