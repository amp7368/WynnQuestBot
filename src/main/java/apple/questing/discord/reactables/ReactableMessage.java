package apple.questing.discord.reactables;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

public interface ReactableMessage {
    void dealWithReaction(AllReactables.Reactable reaction, String s, @NotNull MessageReactionAddEvent event);
    Long getId();
    long getLastUpdated();
}
