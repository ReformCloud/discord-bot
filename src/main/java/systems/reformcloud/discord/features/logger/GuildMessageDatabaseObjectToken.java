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
package systems.reformcloud.discord.features.logger;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.database.object.DatabaseObjectToken;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.function.Function;

/**
 * The object token for guild messages
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class GuildMessageDatabaseObjectToken extends DatabaseObjectToken<GuildMessage> {

    static final Function<byte[], GuildMessage> MAPPER = bytes -> {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream stream = new ObjectInputStream(byteArrayInputStream)) {
            var messageID = stream.readLong();
            var userID = stream.readLong();
            var creationTime = stream.readLong();
            var message = stream.readUTF();

            return new GuildMessage(messageID, userID, creationTime, message);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    };

    public GuildMessageDatabaseObjectToken(long messageId) {
        this.messageId = messageId;
    }

    private final long messageId;

    @NotNull
    @Override
    public GuildMessage deserialize(@NotNull ObjectInputStream stream) {
        try {
            var messageID = stream.readLong();
            var userID = stream.readLong();
            var creationTime = stream.readLong();
            var message = stream.readUTF();

            return new GuildMessage(messageID, userID, creationTime, message);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return new GuildMessage(0, 0, "");
    }

    @NotNull
    @Override
    public String getTable() {
        return "discord_messages";
    }

    @NotNull
    @Override
    public String getKey() {
        return Long.toString(messageId);
    }
}
