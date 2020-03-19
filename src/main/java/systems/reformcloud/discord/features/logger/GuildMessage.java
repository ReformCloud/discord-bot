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

import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.database.object.DatabaseObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class GuildMessage implements DatabaseObject {

    @NotNull
    public static GuildMessage fromMessage(@NotNull Message message) {
        return new GuildMessage(message.getIdLong(), message.getAuthor().getIdLong(), message.getContentRaw());
    }

    public GuildMessage(long messageId, long userId, String message) {
        this.messageId = messageId;
        this.userId = userId;
        this.creationTime = System.currentTimeMillis();
        this.message = message;
    }

    GuildMessage(long messageId, long userId, long creationTime, String message) {
        this.messageId = messageId;
        this.userId = userId;
        this.creationTime = creationTime;
        this.message = message;
    }

    private final long messageId;

    private final long userId;

    private final long creationTime;

    private final String message;

    public long getMessageId() {
        return messageId;
    }

    public long getUserId() {
        return userId;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    @Override
    public String getKey() {
        return Long.toString(this.messageId);
    }

    @NotNull
    @Override
    public String getTable() {
        return "discord_messages";
    }

    @NotNull
    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream)) {
            stream.writeLong(this.messageId);
            stream.writeLong(this.userId);
            stream.writeLong(this.creationTime);
            stream.writeUTF(this.message);

            stream.writeObject(null);

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return new byte[0];
    }
}
