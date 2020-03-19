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

import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.event.DiscordUserWarnEvent;
import systems.reformcloud.discord.punishments.DiscordPunishment;
import systems.reformcloud.events.annotations.Subscribe;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;

import java.util.concurrent.TimeUnit;

/**
 * Listens to the discord warn create event
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordWarnCreateListener {

    @Subscribe
    public void handle(final DiscordUserWarnEvent event) {
        var member = DiscordUtil.getGuild().retrieveMemberById(event.getUser().getId(), true).submit().join();
        if (member == null) {
            return;
        }

        member.getUser().openPrivateChannel().queue(channel -> {
            channel.sendMessage("You have been warned in " + DiscordUtil.getGuild().getName()).queue();
            channel.sendMessage("Reason: " + event.getWarn().getReason()).queue();
        }, error -> {
        });

        if (!event.getUser().getPunishments().isEmpty()) {
            return;
        }

        if (event.getUser().getWarns().size() == DiscordUtil.getFirstAutoMuteWarnCount()) {
            event.getUser().getPunishments().add(new DiscordPunishment(
                    member.getIdLong(),
                    System.currentTimeMillis(),
                    event.getWarner().getId(),
                    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1),
                    event.getWarner().getName(),
                    DefaultPunishmentTypes.MUTE.name(),
                    "Auto punish for one day because of 5 warns"
            ));
            event.getDiscordBot().getAssociatedUserManagement().updateUser(event.getUser());
            return;
        }

        if (event.getUser().getWarns().size() == DiscordUtil.getSecondAutoMuteWarnCount()) {
            event.getUser().getPunishments().add(new DiscordPunishment(
                    member.getIdLong(),
                    System.currentTimeMillis(),
                    event.getWarner().getId(),
                    System.currentTimeMillis() + TimeUnit.DAYS.toMillis(5),
                    event.getWarner().getName(),
                    DefaultPunishmentTypes.MUTE.name(),
                    "Auto punish for five days because of 10 warns"
            ));
            event.getDiscordBot().getAssociatedUserManagement().updateUser(event.getUser());
            return;
        }

        if (event.getUser().getWarns().size() == DiscordUtil.getAutoBanWarnCount()) {
            event.getUser().getPunishments().add(new DiscordPunishment(
                    member.getIdLong(),
                    System.currentTimeMillis(),
                    event.getWarner().getId(),
                    -1,
                    event.getWarner().getName(),
                    DefaultPunishmentTypes.BAN.name(),
                    "Auto permanent punish because of 15 warns"
            ));
            event.getDiscordBot().getAssociatedUserManagement().updateUser(event.getUser());
        }
    }
}
