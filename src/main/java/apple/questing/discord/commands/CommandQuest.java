package apple.questing.discord.commands;

import apple.questing.data.player.WynncraftPlayer;
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
     * -l # - default is that class's level. overrides what level the player is
     * </p>
     * @param event the discord message event
     */
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {

    }
}
