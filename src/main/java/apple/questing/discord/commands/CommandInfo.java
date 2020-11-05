package apple.questing.discord.commands;

import apple.questing.discord.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandInfo implements DoCommand {
    @Override
    public void dealWithCommand(MessageReceivedEvent event) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Quest Quince", null, DiscordBot.client.getSelfUser().getAvatarUrl());
        embed.setTitle("Info!");
        embed.addField("Invite me link", "https://discord.com/api/oauth2/authorize?client_id=763663502472118274&permissions=354368&scope=bot", false);
        embed.addField("Apple's Bots Discord Server", "https://discord.gg/XEyUWu9 (In case you need to come say hi for some reason)", false);
        event.getChannel().sendMessage(embed.build()).queue();
    }
}
