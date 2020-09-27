package apple.questing.discord.reactions;

import apple.questing.discord.pageable.PageableMessages;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionLeft implements DoReaction {
    @Override
    public void dealWithReaction(MessageReactionAddEvent event) {
        if (PageableMessages.backward(event.getMessageIdLong())) {
            User user = event.getUser();
            if (user != null)
                event.getReaction().removeReaction(user).queue();
        }
    }
}
