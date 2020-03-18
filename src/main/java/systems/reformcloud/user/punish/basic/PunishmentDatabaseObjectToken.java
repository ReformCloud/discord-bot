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
import systems.reformcloud.database.object.DatabaseObjectToken;
import systems.reformcloud.user.punish.Punishment;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Represents a database token for a punishment
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class PunishmentDatabaseObjectToken extends DatabaseObjectToken<Punishment> {

    public PunishmentDatabaseObjectToken(long timeOut, String type) {
        this.timeOut = timeOut;
        this.type = type;
    }

    private final long timeOut;

    private final String type;

    @NotNull
    @Override
    public Punishment deserialize(@NotNull ObjectInputStream stream) {
        try {
            return BasicPunishment.deserialize(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return new BasicPunishment(0, 0, 0, 0, "", "", "", "");
    }

    @NotNull
    @Override
    public String getTable() {
        return "punishments_" + type;
    }

    @NotNull
    @Override
    public String getKey() {
        return Long.toString(timeOut);
    }
}
