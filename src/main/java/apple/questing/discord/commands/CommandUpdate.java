package apple.questing.discord.commands;

import apple.questing.sheets.SheetsQuery;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandUpdate implements DoCommand {
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        try {
            List<Integer> failsInt = SheetsQuery.update();
            List<String> fails = new ArrayList<>();
            failsInt.forEach(integer -> fails.add(String.valueOf(integer)));
            if (fails.isEmpty()) {
                event.getChannel().sendMessage("Updating was successful").queue();
            } else {
                event.getChannel().sendMessage("Updating was not successful. Fails: " + String.join(", " , fails)).queue();
            }
        } catch (IOException e) {
            event.getChannel().sendMessage("Updating was not successful").queue();
        }
    }
}
