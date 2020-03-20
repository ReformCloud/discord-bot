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
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.discord.command.util.CommandArgumentParser;

/**
 * Command to delete punishments from an user
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DeletePunishCommand extends BasicDiscordCommand {

    public DeletePunishCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!deletepunishment", new String[]{"!dp"}, "Deletes a punishment from a user");
    }

    @NotNull
    @Override
    public Permission getPermission() {
        return Permission.ADMINISTRATOR;
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        if (!source.hasPermission(getPermission())) {
            return;
        }

        if (strings.length != 2) {
            source.sendMessage("Invalid command syntax! Use: `dp <user id | @mention> <punish unique id>`");
            return;
        }

        var user = CommandArgumentParser.getExistingUser(strings[0], source, this.parent);
        if (user == null) {
            source.sendMessage("Unable to find user " + strings[0] + " in the database");
            return;
        }

        var punishUniqueID = CommandArgumentParser.parseUniqueId(strings[1]);
        if (punishUniqueID == null) {
            source.sendMessage("Please provide an unique id as second parameter");
            return;
        }

        if (user.getPunishmentByUniqueID(punishUniqueID) == null) {
            source.sendMessage("The provided punish does not exists");
            return;
        }

        user.removePunishmentByUniqueId(punishUniqueID);
        this.parent.getAssociatedUserManagement().updateUser(user);

        source.sendMessage(String.format("Successfully removed punishment %s", punishUniqueID.toString()));
    }
}
