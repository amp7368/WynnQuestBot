package apple.questing.discord.reactions;

import apple.questing.data.reaction.AllReactableClassChoices;
import apple.questing.data.reaction.ClassChoiceMessage;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public class ReactionClassChoice implements DoReaction {
    @Override
    public void dealWithReaction(MessageReactionAddEvent event) {
        ClassChoiceMessage arguments;
        if ((arguments = AllReactableClassChoices.getMessage(event.getMessageId())) != null) {
            event.getChannel().sendMessage("nice").queue();
        }
    }
}
