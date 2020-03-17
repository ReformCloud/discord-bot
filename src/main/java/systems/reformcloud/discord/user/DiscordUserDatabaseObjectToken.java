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
package systems.reformcloud.discord.user;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.database.object.DatabaseObjectToken;
import systems.reformcloud.user.User;
import systems.reformcloud.user.information.UserInformation;
import systems.reformcloud.user.punish.Punishment;
import systems.reformcloud.user.warn.Warn;

import java.io.ObjectInputStream;
import java.util.Collection;

/**
 * Represents an implementation of a {@link DatabaseObjectToken} for discord users
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordUserDatabaseObjectToken extends DatabaseObjectToken<User> {

    public DiscordUserDatabaseObjectToken(long id) {
        this.id = id;
    }

    private final long id;

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public User deserialize(@NotNull ObjectInputStream stream) {
        try {
            long id = stream.readLong();

            UserInformation information = (UserInformation) stream.readObject();
            Collection<Warn> warns = (Collection<Warn>) stream.readObject();
            Collection<Punishment> punishments = (Collection<Punishment>) stream.readObject();

            return new DiscordUser(id, information, warns, punishments);
        } catch (final Throwable throwable) {
            throwable.printStackTrace();
        }

        return new DiscordUser(this.id);
    }

    @Override
    public @NotNull
    String getTable() {
        return "discord_users";
    }

    @Override
    public @NotNull
    String getKey() {
        return Long.toString(id);
    }
}
