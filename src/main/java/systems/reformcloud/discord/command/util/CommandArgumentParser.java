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
package systems.reformcloud.discord.command.util;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.user.User;

import java.util.UUID;

/**
 * A small util to parse some command arguments
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class CommandArgumentParser {

    private CommandArgumentParser() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public static User getUserOrCreate(@NotNull String argument, @NotNull CommandSource source, @NotNull Bot<JDA> parent) {
        var id = parseId(source, argument);
        if (id == null) {
            return null;
        }

        return parent.getAssociatedUserManagement().getUserOrCreate(id);
    }

    @Nullable
    public static User getExistingUser(@NotNull String argument, @NotNull CommandSource source, @NotNull Bot<JDA> parent) {
        var id = parseId(source, argument);
        if (id == null) {
            return null;
        }

        return parent.getAssociatedUserManagement().getExistingUserById(id);
    }

    @Nullable
    public static UUID parseUniqueId(@NotNull String argument) {
        try {
            return UUID.fromString(argument);
        } catch (final IllegalArgumentException ex) {
            return null;
        }
    }

    private static Long parseId(@NotNull CommandSource source, @NotNull String argument) {
        if (argument.startsWith("<@!") && argument.endsWith(">")) {
            argument = argument.replaceFirst("<@!", "").replace(">", "");
        }

        try {
            return Long.parseLong(argument);
        } catch (final NumberFormatException ex) {
            source.sendMessage("Please give an user id as first parameter");
            return null;
        }
    }
}
