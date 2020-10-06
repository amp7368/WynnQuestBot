package apple.questing.discord.reactables.book;

import apple.questing.data.quest.Quest;
import apple.questing.discord.reactables.AllReactables;
import apple.questing.discord.reactables.ReactableMessage;
import apple.questing.utils.Pretty;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static apple.questing.sheets.SheetsQuery.allQuests;

public class QuestLookupMessage implements ReactableMessage {
    private static final int ENTRIES_PER_PAGE = 10;
    int page = 0;
    private final long id;
    private final List<Quest> quests;
    private long lastUpdated = System.currentTimeMillis();
    private final Message message;

    public QuestLookupMessage(String questName, TextChannel channel) {
        quests = new ArrayList<>();
        String name = questName.toLowerCase();
        for (Quest quest : allQuests) {
            if (quest.name.toLowerCase().contains(name))
                quests.add(quest);
        }

        message = channel.sendMessage(makeMessage()).complete();
        this.id = message.getIdLong();
        message.addReaction(AllReactables.Reactable.LEFT.getFirstEmoji()).queue();
        message.addReaction(AllReactables.Reactable.RIGHT.getFirstEmoji()).queue();
        message.addReaction(AllReactables.Reactable.TOP.getFirstEmoji()).queue();
    }

    private String makeMessage() {
        StringBuilder messageText = new StringBuilder();
        messageText.append("```md\n");
        messageText.append(String.format("#     %-26s| %-15s| %-15s| %-11s| %-11s|\n", "Quest Name", "<Xp>", "<Emeralds>", "<Time>", "Collection"));
        int lower = page * ENTRIES_PER_PAGE;
        int max = Math.min(ENTRIES_PER_PAGE, quests.size() - lower);
        for (int i = 0; i < max; i++) {
            Quest quest = quests.get(lower);
            messageText.append(String.format("|%-31s| %-15s| %-15s| %-11s| %-11s|\n",
                    String.format("<%-3s %s>", ++lower + ".", quest.name.length() > 25 ? quest.name.substring(0, 14) + "..." +
                            quest.name.substring(quest.name.length() - 8) : quest.name),
                    String.format("<%s>", Pretty.commasXp(quest.xp)),
                    String.format("<%s>", Pretty.getMon(quest.emerald)),
                    String.format("<%s>", Pretty.time(quest.time)),
                    String.format("<%s>", Pretty.time(quest.collectionTime))));
        }
        messageText.append("```");
        return messageText.toString();
    }

    @Override
    public void dealWithReaction(AllReactables.Reactable reaction, String emoji, @NotNull MessageReactionAddEvent event) {
        switch (reaction) {
            case LEFT:
                backward();
                break;
            case RIGHT:
                forward();
                break;
            case TOP:
                top();
                break;
        }
    }

    private void backward() {
        if (page != 0) {
            --page;
            message.editMessage(makeMessage()).queue();
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    private void forward() {
        if ((page + 1) * ENTRIES_PER_PAGE < quests.size()) {
            ++page;
            message.editMessage(makeMessage()).queue();
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    private void top() {
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
