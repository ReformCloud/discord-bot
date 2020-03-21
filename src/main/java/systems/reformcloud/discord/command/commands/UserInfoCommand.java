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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.discord.command.util.CommandArgumentParser;
import systems.reformcloud.util.Constants;

import java.awt.*;

/**
 * Displays some user related information
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class UserInfoCommand extends BasicDiscordCommand {

    public UserInfoCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!userinfo", new String[]{"!ui"}, "Shows information about the self/another user");
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        var userId = source.getId();
        if (strings.length >= 1 && !source.hasPermission(Permission.MESSAGE_MANAGE)) {
            source.sendMessage("You are not allowed to get information about another user!");
            return;
        }

        var user = CommandArgumentParser.getExistingUser(strings.length == 1 ? strings[0] : Long.toString(userId), source, this.parent);
        if (user == null) {
            source.sendMessage("Unable to find user " + (strings.length == 1 ? strings[0] : Long.toString(userId)) + " in the database");
            return;
        }

        var embed = new EmbedBuilder()
                .setAuthor(Long.toString(user.getId()))
                .setColor(Color.GREEN)
                .addField(
                        "id",
                        Long.toString(user.getId()),
                        true
                ).addField(
                        "join time",
                        Constants.DATE_FORMAT.format(user.getInformation().getJoinTimeInMillis()),
                        true
                ).addField(
                        "first join time",
                        Constants.DATE_FORMAT.format(user.getInformation().getFirstJoinTimeInMillis()),
                        true
                ).addField(
                        "total joins",
                        Long.toString(user.getInformation().getTotalJoins()),
                        true
                ).setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()))
                .build();

        this.parent.getCurrentInstance().ifPresent(e -> {
            var channel = e.getTextChannelById(source.getSourceChannel());
            if (channel == null) {
                return;
            }

            channel.sendMessage(embed).queue();
        });
    }
}
