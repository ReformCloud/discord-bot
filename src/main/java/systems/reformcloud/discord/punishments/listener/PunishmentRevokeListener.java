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
import systems.reformcloud.user.punish.event.PunishmentRevokeEvent;
import systems.reformcloud.util.Constants;
import systems.reformcloud.util.KeyValueHolder;

/**
 * Represents the listener which handles the revoke of discord user punishments
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class PunishmentRevokeListener {

    public PunishmentRevokeListener(@NotNull Bot<JDA> bot) {
        this.bot = bot;
    }

    private final Bot<JDA> bot;

    @Subscribe
    public void handle(final PunishmentRevokeEvent event) {
        if (!event.getPunishment().getProvider().equals("discord")
                || bot.getCurrentInstance().isEmpty()) {
            return;
        }

        if (event.getPunishment().getPunishmentType().equals(DefaultPunishmentTypes.BAN.name())) {
            DiscordUtil.getGuild().unban(Long.toString(event.getPunishment().getUserID())).queue();
        }

        var user = bot.getAssociatedUserManagement().getExistingUserById(event.getPunishment().getUserID());
        if (user == null) {
            return;
        }

        user.getPunishments()
                .stream()
                .filter(e -> e.getUniqueID().equals(event.getPunishment().getUniqueID()))
                .findAny()
                .ifPresent(e -> {
                    user.getPunishments().remove(e);
                    this.bot.getAssociatedUserManagement().updateUser(user);
                });

        DiscordUtil.getGuild().retrieveMemberById(user.getId()).queue(discordMember -> {
            if (discordMember == null) {
                return;
            }

            if (event.getPunishment().getPunishmentType().equals(DefaultPunishmentTypes.MUTE.name())) {
                DiscordUtil.getGuild().removeRoleFromMember(discordMember, DiscordUtil.getPunishedRole()).queue();
            }

            LoggerFeature.log(
                    "Revoked punishment #" + event.getPunishment().getUniqueID() + " from user " + discordMember.getUser().getName(),
                    new KeyValueHolder<>("punish time", Constants.DATE_FORMAT.format(event.getPunishment().getMilliTime())),
                    new KeyValueHolder<>("punish type", event.getPunishment().getPunishmentType()),
                    new KeyValueHolder<>("warner", event.getPunishment().getWarnerName() + " (" + event.getPunishment().getWarner() + ")"),
                    new KeyValueHolder<>("reason", event.getPunishment().getReason())
            );

            discordMember.getUser().openPrivateChannel().queue(
                    e -> e.sendMessage("Your punishment on " + DiscordUtil.getGuild().getName() + " has been revoked").queue(),
                    error -> {
                    }
            );
        }, error -> {
        });
    }
}
