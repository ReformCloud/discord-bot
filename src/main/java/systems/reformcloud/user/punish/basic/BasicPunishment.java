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
package systems.reformcloud.user.punish.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.user.punish.Punishment;
import systems.reformcloud.user.punish.event.PunishmentRevokeEvent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

/**
 * Represents a default implementation of a punishment
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class BasicPunishment implements Punishment {

    BasicPunishment(UUID uniqueID, long userID, long time, long warner, long timeout, String provider, String warnerName, String type, String reason) {
        this.uniqueID = uniqueID;
        this.userID = userID;
        this.time = time;
        this.warner = warner;
        this.timeout = timeout;
        this.provider = provider;
        this.warnerName = warnerName;
        this.type = type;
        this.reason = reason;
    }

    public BasicPunishment(long userID, long time, long warner, long timeout, String provider, String warnerName, String type, String reason) {
        this(UUID.randomUUID(), userID, time, warner, timeout, provider, warnerName, type, reason);

        GlobalAPI.getDatabaseDriver().createTable("punishments_" + type);
        GlobalAPI.getDatabaseDriver().insert(this);
    }

    private final UUID uniqueID;

    private final long userID;

    private final long time;

    private final long warner;

    private final long timeout;

    private final String provider;

    private final String warnerName;

    private final String type;

    private final String reason;

    @NotNull
    @Override
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    @Override
    public long getUserID() {
        return this.userID;
    }

    @Override
    public long getMilliTime() {
        return this.time;
    }

    @Override
    public long getWarner() {
        return this.warner;
    }

    @Override
    public long getTimeoutTime() {
        return this.timeout;
    }

    @NotNull
    @Override
    public String getProvider() {
        return this.provider;
    }

    @NotNull
    @Override
    public String getWarnerName() {
        return this.warnerName;
    }

    @NotNull
    @Override
    public String getPunishmentType() {
        return this.type;
    }

    @NotNull
    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public void revoke() {
        GlobalAPI.getDatabaseDriver().deleteFromTable(this);
        GlobalAPI.getEventManager().callEvent(new PunishmentRevokeEvent(this));
    }

    @NotNull
    @Override
    public String getKey() {
        return Long.toString(this.timeout);
    }

    @NotNull
    @Override
    public String getTable() {
        return "punishments_" + type;
    }

    @NotNull
    @Override
    public byte[] serialize() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream)) {
            stream.writeLong(this.uniqueID.getMostSignificantBits());
            stream.writeLong(this.uniqueID.getLeastSignificantBits());

            stream.writeLong(this.userID);
            stream.writeLong(this.time);
            stream.writeLong(this.warner);
            stream.writeLong(this.timeout);

            stream.writeUTF(this.provider);
            stream.writeUTF(this.warnerName);
            stream.writeUTF(this.type);
            stream.writeUTF(this.reason);

            stream.writeObject(null);

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return new byte[0];
    }

    @NotNull
    static Punishment deserialize(@NotNull ObjectInputStream stream) throws IOException {
        var uniqueID = new UUID(stream.readLong(), stream.readLong());

        long userID = stream.readLong();
        long time = stream.readLong();
        long warner = stream.readLong();
        long timeOut = stream.readLong();

        var provider = stream.readUTF();
        var warnerName = stream.readUTF();
        var type = stream.readUTF();
        var reason = stream.readUTF();

        return new BasicPunishment(uniqueID, userID, time, warner, timeOut, provider, warnerName, type, reason);
    }
}
