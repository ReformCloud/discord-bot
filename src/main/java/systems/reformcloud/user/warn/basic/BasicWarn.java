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
package systems.reformcloud.user.warn.basic;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.user.warn.Warn;

import java.util.UUID;

/**
 * Represents a basic implementation of a warn
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class BasicWarn implements Warn {

    public BasicWarn(long time, long warner, String warnerName, String reason) {
        this.uniqueID = UUID.randomUUID();
        this.time = time;
        this.warner = warner;
        this.warnerName = warnerName;
        this.reason = reason;
    }

    private final UUID uniqueID;

    private final long time;

    private final long warner;

    private final String warnerName;

    private final String reason;

    @NotNull
    @Override
    public UUID getUniqueID() {
        return this.uniqueID;
    }

    @Override
    public long getMilliTime() {
        return this.time;
    }

    @Override
    public long getWarner() {
        return this.warner;
    }

    @NotNull
    @Override
    public String getWarnerName() {
        return this.warnerName;
    }

    @NotNull
    @Override
    public String getReason() {
        return this.reason;
    }
}
