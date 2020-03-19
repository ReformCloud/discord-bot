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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.discord.event.DiscordUserWarnEvent;
import systems.reformcloud.user.User;
import systems.reformcloud.user.warn.basic.BasicWarn;

import java.util.Arrays;

/**
 * A discord command for the warns
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class WarnCommand extends BasicDiscordCommand {

    public WarnCommand(@NotNull Bot<JDA> parent) {
        super("warn", new String[0], "Warns an user");
        this.parent = parent;
    }

    private final Bot<JDA> parent;

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

        if (strings.length < 2) {
            source.sendMessage("Invalid command syntax! Use: `warn <user id | @mention> <reason>`");
            return;
        }

        User user;
        if (strings[0].startsWith("<@!") && strings[0].endsWith(">")) {
            strings[0] = strings[0].replaceFirst("<@!", "").replace(">", "");
        }

        long id;
        try {
            id = Long.parseLong(strings[0]);
        } catch (final NumberFormatException ex) {
            source.sendMessage("Please give an user id as first parameter");
            return;
        }

        var member = DiscordUtil.getGuild().retrieveMemberById(id, true).submit().join();
        if (member == null && !source.hasPermission(Permission.ADMINISTRATOR)) {
            source.sendMessage("You can only warn a member which is on the discord");
            return;
        }

        if (member != null && (member.hasPermission(Permission.MANAGE_CHANNEL)
                || member.hasPermission(Permission.ADMINISTRATOR)
                && !source.hasPermission(Permission.ADMINISTRATOR))) {
            source.sendMessage("Unable to warn user " + id);
            return;
        }

        user = this.parent.getAssociatedUserManagement().getUserOrCreate(id);

        var warn = new BasicWarn(
                System.currentTimeMillis(),
                source.getId(),
                source.getName(),
                String.join(" ", Arrays.copyOfRange(strings, 1, strings.length))
        );

        user.getWarns().add(warn);
        this.parent.getAssociatedUserManagement().updateUser(user);

        GlobalAPI.getEventManager().callEvent(new DiscordUserWarnEvent(warn, parent, source, user));

        source.sendMessage("Warned user " + strings[0] + "! Reason: " + String.join(" ", Arrays.copyOfRange(strings, 1, strings.length)));
    }
}
