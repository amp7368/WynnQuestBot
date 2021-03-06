package apple.questing.discord;


import apple.questing.QuestMain;
import apple.questing.discord.reactables.AllReactables;
import apple.questing.discord.commands.*;
import apple.questing.sheets.SheetsQuery;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DiscordBot extends ListenerAdapter {


    private static final HashMap<String, DoCommand> commandMap = new HashMap<>();
    public static String discordToken; // my bot
    public static JDA client;

    public static final String PREFIX = "q!";
    public static final String BOOK = "book";
    public static final String HELP = "help";
    public static final String UPDATE_COMMAND = "update";
    public static final String QUEST_COMMAND = "quest";
    public static final String QUEST_SPECIFIC_COMMAND = "squest";

    public DiscordBot() {
        List<String> list = Arrays.asList(QuestMain.class.getProtectionDomain().getCodeSource().getLocation().getPath().split("/"));
        String BOT_TOKEN_FILE_PATH = String.join("/", list.subList(0, list.size() - 1)) + "/data/discordToken.data";

        File file = new File(BOT_TOKEN_FILE_PATH);
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException ignored) {
            }
            System.err.println("Please fill in the token for the discord bot in '" + BOT_TOKEN_FILE_PATH + "'");
            System.exit(1);
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            discordToken = reader.readLine();
            reader.close();
        } catch (IOException e) {
            System.err.println("Please fill in the token for the discord bot in '" + BOT_TOKEN_FILE_PATH + "'");
            System.exit(1);
        }

    }

    public void enableDiscord() throws LoginException {
        JDABuilder builder = new JDABuilder(discordToken);
        builder.addEventListeners(this);
        client = builder.build();
        client.getPresence().setPresence(Activity.playing("Quest bot soon? O.o"), true);
        client.getPresence().setStatus(OnlineStatus.IDLE);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        commandMap.put(PREFIX + BOOK, new CommandQuestBook());
        commandMap.put(PREFIX + HELP, new CommandHelp());
        commandMap.put(PREFIX + UPDATE_COMMAND, new CommandUpdate());
        commandMap.put(PREFIX + QUEST_COMMAND, new CommandQuest());
        commandMap.put(PREFIX + QUEST_SPECIFIC_COMMAND, new CommandQuestSpecific());

        try {
            SheetsQuery.update();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) {
            return;
        }
        // the author is not a bot

        String messageContent = event.getMessage().getContentStripped();
        final Member member = event.getMember();
        if (member == null) return;

        // deal with the different commands
        for (String command : commandMap.keySet()) {
            if (messageContent.startsWith(command)) {
                commandMap.get(command).dealWithCommand(event);
                break;
            }
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        if (user == null || user.isBot()) {
            return;
        }
        AllReactables.dealWithReaction(event);
    }
}