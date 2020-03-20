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
package systems.reformcloud.discord.command.commands;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.discord.command.util.CommandArgumentParser;
import systems.reformcloud.discord.punishments.DiscordPunishment;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Command to punish users
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class PunishCommand extends BasicDiscordCommand {

    public PunishCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!punish", new String[0], "Punishes an user");
    }

    private final Cache<Long, Integer> coolDown = CacheBuilder
            .newBuilder()
            .expireAfterWrite(20, TimeUnit.SECONDS)
            .ticker(Ticker.systemTicker())
            .build();

    @NotNull
    @Override
    public Permission getPermission() {
        return Permission.MESSAGE_MANAGE;
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        if (!source.hasPermission(getPermission())) {
            return;
        }

        if (strings.length < 5) {
            source.sendMessage("Invalid command syntax! Use: `punish <user id | @mention> <timeout> <timeout unit> <type> <reason>`");
            return;
        }

        if (coolDown.getIfPresent(source.getId()) != null) {
            source.sendMessage("Cool down...");
            return;
        }

        var user = CommandArgumentParser.getExistingUser(strings[0], source, this.parent);
        if (user == null) {
            source.sendMessage("Unable to find user " + strings[0] + " in the database");
            return;
        }

        var timeout = CommandArgumentParser.parseLong(strings[1]);
        if (timeout == null) {
            source.sendMessage("Please provide a valid timeout time");
            return;
        }

        if (timeout == -1 && !source.hasPermission(Permission.ADMINISTRATOR)) {
            source.sendMessage("You are not allowed to ban longer than 10 days");
            return;
        }

        if (timeout < -1 || timeout == 0) {
            source.sendMessage("Please provide a timeout bigger than 0");
            return;
        }

        var unit = CommandArgumentParser.parseTimeUnit(strings[2]);
        if (unit == null && timeout != -1) {
            source.sendMessage("Please use a valid time unit (`h`: HOURS, `d`: DAYS)");
            return;
        }

        if (timeout != -1) {
            timeout = System.currentTimeMillis() + unit.toMillis(timeout);
        }

        if (Arrays.stream(DefaultPunishmentTypes.values()).noneMatch(e -> e.name().equalsIgnoreCase(strings[3]))) {
            source.sendMessage("Invalid punishment type " + strings[3] + "!");
            return;
        }

        user.getPunishments().stream().filter(e -> e.getPunishmentType().equalsIgnoreCase(strings[3])).findFirst().ifPresent(activePunishment -> {
            user.removePunishmentByUniqueId(activePunishment.getUniqueID());
            source.sendMessage("Overriding current punishment of type " + strings[3]);
        });

        if (!source.hasPermission(Permission.ADMINISTRATOR)) {
            coolDown.put(source.getId(), 0);
        }

        user.getPunishments().add(new DiscordPunishment(
                user.getId(),
                System.currentTimeMillis(),
                source.getId(),
                timeout,
                source.getName(),
                strings[3].toUpperCase(),
                String.join(" ", Arrays.copyOfRange(strings, 4, strings.length))
        ));
        this.parent.getAssociatedUserManagement().updateUser(user);

        source.sendMessage("Punished user " + strings[0] + " with reason: "
                + String.join(" ", Arrays.copyOfRange(strings, 4, strings.length))
        );
    }
}
