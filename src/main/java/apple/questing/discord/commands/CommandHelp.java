package apple.questing.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandHelp implements DoCommand {
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        StringBuilder messageText = new StringBuilder();

        event.getChannel().sendMessage(messageText.toString()).queue();
    }
}
