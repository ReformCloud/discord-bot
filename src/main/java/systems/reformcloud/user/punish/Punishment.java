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
package systems.reformcloud.user.punish;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a punish of an user
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface Punishment extends Serializable {

    /**
     * @return The unique id of the current punishment
     */
    @NotNull
    UUID getUniqueID();

    /**
     * @return The time in milliseconds the punishment came from
     */
    long getMilliTime();

    /**
     * @return The id of the user which warned the user
     */
    long getWarner();

    /**
     * @return The time in millis when the punishment ends
     */
    long getTimeoutTime();

    /**
     * @return If the current punishment is permanent
     */
    default boolean isPermanent() {
        return this.getTimeoutTime() == -1;
    }

    /**
     * @return The name of the user which warned the person
     */
    @NotNull
    String getWarnerName();

    /**
     * @return The type by name of the punishment
     * @see DefaultPunishmentTypes
     */
    @NotNull
    String getPunishmentType();

    /**
     * @return The reason of the punishment
     */
    @NotNull
    String getReason();
}
