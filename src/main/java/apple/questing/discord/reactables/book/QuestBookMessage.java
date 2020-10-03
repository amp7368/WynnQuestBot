package apple.questing.discord.reactables.book;

import apple.questing.data.player.WynncraftClass;
import apple.questing.data.player.WynncraftPlayer;
import apple.questing.data.quest.Quest;
import apple.questing.discord.reactables.AllReactables;
import apple.questing.discord.reactables.ReactableMessage;
import apple.questing.utils.Pretty;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static apple.questing.discord.reactables.AllReactables.Reactable.*;

public class QuestBookMessage implements ReactableMessage {
    private static final int ENTRIES_PER_PAGE = 10;
    private final Message message;
    private final long id;
    private long lastUpdated;
    private int page = 0;
    private final List<Quest> quests = new ArrayList<>();
    private final WynncraftPlayer player;
    private final WynncraftClass wynncraftClass;
    private boolean isCollection;

    public QuestBookMessage(WynncraftPlayer player, WynncraftClass wynncraftClass, MessageChannel channel, int classLevel, boolean isCollection) {
        this.player = player;
        this.wynncraftClass = wynncraftClass;

        this.isCollection = isCollection;

        for (Quest quest : wynncraftClass.questsNotCompleted) {
            if (quest.levelMinimum <= (classLevel == -1 ? wynncraftClass.combatLevel : classLevel)) {
                quests.add(quest);
            }
        }
        quests.sort((o1, o2) -> o2.levelMinimum - o1.levelMinimum);

        this.lastUpdated = System.currentTimeMillis();

        this.message = channel.sendMessage(makeMessage()).complete();
        this.id = message.getIdLong();

        message.addReaction(LEFT.getFirstEmoji()).queue();
        message.addReaction(RIGHT.getFirstEmoji()).queue();
        message.addReaction(TOP.getFirstEmoji()).queue();
        message.addReaction(BASKET.getFirstEmoji()).queue();
        AllReactables.add(this);
    }

    private String makeMessage() {
        StringBuilder messageText = new StringBuilder();
        messageText.append(String.format("**Quest Book for %s | %s, Lvl: %d/%d, Dungeons: %d**\n",
                player.name, wynncraftClass.namePretty, wynncraftClass.combatLevel, wynncraftClass.totalLevel, wynncraftClass.dungeonsWon));
        messageText.append("```md\n");
        messageText.append(String.format("#     %-26s| %-6s| %-14s| %-12s| %-18s|\n", "Quests", "<Lvl>", "<Time>", "<Emeralds>", "<Xp>"));
        int lower = page * ENTRIES_PER_PAGE;
        for (int i = 0; i < ENTRIES_PER_PAGE; i++) {
            final Quest quest = quests.size() > lower ? quests.get(lower++) : null;
            if (quest != null) {
                final String name = quest.name;
                messageText.append(String.format("|%-31s| %-6s| %-14s| %-12s| %-18s|",
                        String.format("<%-3s %s>", lower + ".", name.length() > 25 ? name.substring(0, 22) + "..." : name),
                        String.format("<%s>", quest.levelMinimum),
                        String.format("<%.1f mins>", isCollection ? quest.collectionTime + quest.time : quest.time),
                        String.format("<%s>", Pretty.getMon(quest.emerald)),
                        String.format("<%s>", Pretty.commasXp(quest.xp))
                ));

            }
            messageText.append("\n");
        }
        messageText.append("\n```");
        return messageText.toString();
    }

    @Override
    public void dealWithReaction(AllReactables.Reactable reaction, String emoji, @NotNull MessageReactionAddEvent event) {
        final User user = event.getUser();
        if (user == null) return;

        switch (reaction) {
            case LEFT:
                event.getReaction().removeReaction(user).queue();
                backward();
                break;
            case RIGHT:
                forward();
                event.getReaction().removeReaction(user).queue();
                break;
            case TOP:
                top();
                event.getReaction().removeReaction(user).queue();
                break;
            case BASKET:
                switchCollection();
                event.getReaction().removeReaction(user).queue();
                break;
        }
    }

    private void switchCollection() {
        isCollection = !isCollection;
        message.editMessage(makeMessage()).queue();
        this.lastUpdated = System.currentTimeMillis();
    }

    public void forward() {
        if ((page + 1) * ENTRIES_PER_PAGE < quests.size()) {
            ++page;
            message.editMessage(makeMessage()).queue();
        }
        this.lastUpdated = System.currentTimeMillis();
    }

    public void backward() {
        if (page - 1 != -1) {
            --page;
            message.editMessage(makeMessage()).queue();
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    public void top() {
        page = 0;
        message.editMessage(makeMessage()).queue();
        this.lastUpdated = System.currentTimeMillis();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }
}
