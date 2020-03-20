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
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.util.build.MavenUtil;

import java.awt.*;

/**
 * Command to show a maven dependency list
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class MavenCommand extends BasicDiscordCommand {

    public MavenCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!maven", new String[0], "Shows an example maven dependency configuration");
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        this.parent.getCurrentInstance().ifPresent(e -> {
            var channel = e.getTextChannelById(source.getSourceChannel());
            if (channel == null) {
                return;
            }

            var description = "If you don't know how to configure your pom.xml use !mbuild\n"
                    + MavenUtil.formatItem(strings.length == 0 ? null : strings);

            var embed = new EmbedBuilder()
                    .setColor(Color.BLUE)
                    .setAuthor("Mavens dependenc" + (strings.length == 0 ? "y" : "ies"))
                    .setDescription(description)
                    .build();
            if (!embed.isSendable()) {
                source.sendMessage("The embed is too big to send! Please short your dependency search");
                return;
            }

            channel.sendMessage(embed).queue();
        });
    }
}
