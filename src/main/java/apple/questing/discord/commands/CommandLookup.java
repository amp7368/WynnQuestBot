package apple.questing.discord.commands;

import apple.questing.discord.reactables.book.QuestBookMessage;
import apple.questing.discord.reactables.book.QuestLookupMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandLookup implements DoCommand {

    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        String[] contentSplit = event.getMessage().getContentRaw().split(" ");
        if (contentSplit.length < 2)
            event.getChannel().sendMessage("Please enter the questName to lookup a quest.").queue();
        new QuestLookupMessage(contentSplit[1], event.getTextChannel());
    }
}
