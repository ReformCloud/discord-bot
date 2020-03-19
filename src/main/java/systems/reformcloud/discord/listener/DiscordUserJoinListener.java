/*
 * This file is licensed under the MIT License (MIT).
 *
 * Copyright (c) 2020 Pasqual Koschmieder.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package systems.reformcloud.discord.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.event.DiscordUserJoinEvent;
import systems.reformcloud.events.annotations.Subscribe;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;

import java.awt.*;

/**
 * Listen if a new user has joined the discord and takes care of active punishments and sends a nice
 * welcome greeting
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordUserJoinListener {

    @Subscribe
    public void handle(final DiscordUserJoinEvent event) {
        event.getUser().getPunishments()
                .stream()
                .filter(e -> e.getPunishmentType().equals(DefaultPunishmentTypes.BAN.name()))
                .findAny()
                .ifPresentOrElse(e -> DiscordUtil.getGuild().ban(event.getEvent().getMember(), 0, e.getReason()).queue(), () -> {
                    if (event.getUser().getPunishments().size() > 0 && event.getUser().getPunishments()
                            .stream()
                            .anyMatch(e -> e.getPunishmentType().equals(DefaultPunishmentTypes.MUTE.name()))) {
                        DiscordUtil.getGuild().addRoleToMember(event.getEvent().getMember(), DiscordUtil.getPunishedRole()).queue();
                    }

                    DiscordUtil.getGuild().addRoleToMember(event.getEvent().getMember(), DiscordUtil.getMemberRole()).queue();

                    DiscordUtil.getLoggingChannel().sendMessage(new EmbedBuilder() // TODO: use terminal channel
                            .setColor(Color.BLUE)
                            .setAuthor("ReformCloudSystems", "https://reformcloud.systems",
                                    "https://cdn.discordapp.com/emojis/557188390462947358.png")
                            .setTitle(String.format("Welcome %s on the ReformCloud discord!", event.getEvent().getUser().getName()))
                            .setThumbnail(event.getEvent().getUser().getAvatarUrl())
                            .addField("", "Please read the information in " + DiscordUtil.getInformationChannel().getAsMention(), true)
                            .build()
                    ).queue();
                });
    }
}
