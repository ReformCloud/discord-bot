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
import systems.reformcloud.util.build.GradleUtil;

import java.awt.*;

/**
 * Sends the gradle dependencies for more or exact one dependency
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class GradleCommand extends BasicDiscordCommand {

    public GradleCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!gradle", new String[0], "Shows a gradle configuration");
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        this.parent.getCurrentInstance().ifPresent(e -> {
            var channel = e.getTextChannelById(source.getSourceChannel());
            if (channel == null) {
                return;
            }

            var description = "If you don't know how to configure your build.gradle use !gbuild\n"
                    + GradleUtil.formatItem(strings.length == 0 ? null : strings);

            channel.sendMessage(new EmbedBuilder()
                    .setColor(Color.BLUE)
                    .setAuthor("Gradle dependencie(s)")
                    .setDescription(description)
                    .build()
            ).queue();
        });
    }
}
