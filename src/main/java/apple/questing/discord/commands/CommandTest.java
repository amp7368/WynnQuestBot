package apple.questing.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandTest implements DoCommand {
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        event.getChannel().sendMessage("o/").queue();
    }
}
