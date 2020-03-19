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
package systems.reformcloud.discord.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.basic.shared.SharedCommand;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.source.DiscordCommandSource;

/**
 * Represents a command which can get executed from the discord server
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public abstract class BasicDiscordCommand extends SharedCommand<Object> {

    public BasicDiscordCommand(@NotNull Bot<JDA> parent, String commandName, String[] aliases, String description) {
        super(commandName, aliases, description);
        this.parent = parent;
    }

    protected final Bot<JDA> parent;

    @Nullable
    @Override
    public Permission getPermission() {
        return null;
    }

    @Override
    public boolean isAccessibleFrom(@NotNull CommandSource commandSource) {
        return commandSource instanceof DiscordCommandSource;
    }
}
