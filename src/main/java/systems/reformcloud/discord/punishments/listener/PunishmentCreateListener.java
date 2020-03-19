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
package systems.reformcloud.discord.punishments.listener;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.features.logger.LoggerFeature;
import systems.reformcloud.events.annotations.Subscribe;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;
import systems.reformcloud.user.punish.event.PunishmentCreateEvent;
import systems.reformcloud.util.Constants;
import systems.reformcloud.util.KeyValueHolder;
import systems.reformcloud.util.ThreadSupport;

import java.util.concurrent.TimeUnit;

/**
 * Represents a listener which hears to the punishment creates of the discord provider
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class PunishmentCreateListener {

    public PunishmentCreateListener(@NotNull Bot<JDA> bot) {
        this.bot = bot;
    }

    private final Bot<JDA> bot;

    @Subscribe
    public void handle(final PunishmentCreateEvent event) {
        if (!event.getPunishment().getProvider().equals("discord")
                || bot.getCurrentInstance().isEmpty()) {
            return;
        }

        var user = bot.getAssociatedUserManagement().getExistingUserById(event.getPunishment().getUserID());
        if (user == null) {
            return;
        }

        DiscordUtil.getGuild().retrieveMemberById(user.getId()).queue(discordMember -> {
            if (discordMember == null) {
                return;
            }

            LoggerFeature.log(
                    "Created punishment #" + event.getPunishment().getUniqueID() + " for user " + discordMember.getUser().getName(),
                    new KeyValueHolder<>("punish time", Constants.DATE_FORMAT.format(event.getPunishment().getMilliTime())),
                    new KeyValueHolder<>("timeout", event.getPunishment().isPermanent() ? "never"
                            : Constants.DATE_FORMAT.format(event.getPunishment().getTimeoutTime())),
                    new KeyValueHolder<>("punish type", event.getPunishment().getPunishmentType()),
                    new KeyValueHolder<>("warner", event.getPunishment().getWarnerName() + " (" + event.getPunishment().getWarner() + ")"),
                    new KeyValueHolder<>("reason", event.getPunishment().getReason())
            );

            discordMember.getUser().openPrivateChannel().queue(
                    e -> {
                        e.sendMessage("You have been punished on " + DiscordUtil.getGuild().getName()).queue();
                        e.sendMessage("This punishment will be revoked at: " + (event.getPunishment().isPermanent() ? "never"
                                : Constants.DATE_FORMAT.format(event.getPunishment().getTimeoutTime()))).queue();
                        e.sendMessage("The reason for the punishment was: " + event.getPunishment().getReason()).queue();
                    },
                    error -> {
                    }
            );

            ThreadSupport.sleep(TimeUnit.SECONDS, 5);

            if (event.getPunishment().getPunishmentType().equals(DefaultPunishmentTypes.MUTE.name())) {
                DiscordUtil.getGuild().addRoleToMember(discordMember, DiscordUtil.getPunishedRole()).queue();
            } else if (event.getPunishment().getPunishmentType().equals(DefaultPunishmentTypes.BAN.name())) {
                DiscordUtil.getGuild().ban(discordMember, 5, event.getPunishment().getReason()).queue();
            }
        }, error -> {
        });
    }
}
