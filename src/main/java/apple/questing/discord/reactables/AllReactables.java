package apple.questing.discord.reactables;

import apple.questing.discord.reactables.class_choice.ClassChoiceMessage;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AllReactables {
    private static final long STOP_WATCHING_DIFFERENCE = 1000 * 60 * 20; // 20 minutes
    private static final Map<Long, ReactableMessage> pageableMessages = new HashMap<>();
    private static final Object mapSyncObject = new Object();

    public static void add(ReactableMessage message) {
        synchronized (mapSyncObject) {
            pageableMessages.put(message.getId(), message);
        }
    }

    public static void remove(long id) {
        synchronized (mapSyncObject) {
            pageableMessages.remove(id);
        }
    }

    public static void dealWithReaction(@NotNull MessageReactionAddEvent event) {
        String reaction = event.getReactionEmote().getName();
        for (Reactable reactable : Reactable.values()) {
            if (reactable.isEmoji(reaction)) {
                ReactableMessage message = pageableMessages.get(event.getMessageIdLong());
                if (message != null) {
                    message.dealWithReaction(reactable, reaction, event);
                    trimOldMessages();
                    return;
                }
                trimOldMessages();

            }
        }
    }

    private static void trimOldMessages() {
        synchronized (mapSyncObject) {
            pageableMessages.values().removeIf(msg -> System.currentTimeMillis() - msg.getLastUpdated() > STOP_WATCHING_DIFFERENCE);
        }
    }

    public enum Reactable {
        LEFT(Collections.singletonList("\u2B05")),
        RIGHT(Collections.singletonList("\u27A1")),
        TOP(Collections.singletonList("\u21A9")),
        BASKET(Collections.singletonList("\uD83E\uDDFA")),
        GEM(Collections.singletonList("\uD83D\uDC8E")),
        CLASS_CHOICE(ClassChoiceMessage.emojiAlphabet),
        CLOCK(Collections.singletonList("\uD83D\uDD53")),
        AMOUNT(Collections.singletonList("\uD83D\uDCB5")),
        PERCENTAGE(Collections.singletonList("\uD83D\uDD22")),
        SWITCH(Collections.singletonList("\uD83D\uDD03")),
        HELP(Collections.singletonList("\u2753")),
        GREEN(Collections.singletonList("\uD83D\uDFE9")),
        NUMBER(Collections.singletonList("\u0023")),
        LEVEL(Collections.singletonList("\uD83C\uDDF1"));

        private final List<String> emojis;

        Reactable(List<String> emojis) {
            this.emojis = emojis;
        }

        public boolean isEmoji(String reaction) {
            return emojis.contains(reaction);
        }

        public String getFirstEmoji() {
            return emojis.get(0); // it should always have at least one emoji. otherwise it would be useless
        }
    }
}
