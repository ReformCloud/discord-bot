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
package systems.reformcloud.discord;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

/**
 * The discord punishment util
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DiscordUtil {

    private DiscordUtil() {
        throw new UnsupportedOperationException();
    }

    private static Role punishedRole;

    private static Guild guild;

    public static void init(@NotNull JDA parent) {
        var roleId = Preconditions.checkNotNull(System.getProperty("discord-punish-role"), "Unable to load discord punish role");
        punishedRole = Preconditions.checkNotNull(parent.getRoleById(roleId), "Unable to find role with id %s", roleId);

        var guildID = Preconditions.checkNotNull(System.getProperty("discord-guild"), "Unable to load discord guild id");
        guild = Preconditions.checkNotNull(parent.getGuildById(guildID), "Unable to find guild with id %s", roleId);
    }

    @NotNull
    public static Role getPunishedRole() {
        return punishedRole;
    }

    @NotNull
    public static Guild getGuild() {
        return guild;
    }
}
