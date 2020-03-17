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
package systems.reformcloud.events.priority;

/**
 * Represents the priority of a listener.
 * <p>
 * {@link #FIRST} this listener get called before all other listeners of the event.
 * {@link #NORMAL} is the normal priority of a listener.
 * {@link #LAST} this listener get called after all other listeners of the event.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public enum ListenerPriority {

    FIRST((byte) -64),

    SECOND((byte) -32),

    MONITOR((byte) -1),

    NORMAL((byte) 0),

    PENULTIMATE((byte) 32),

    LAST((byte) 64);

    ListenerPriority(byte priorityInJava) {
        this.priority = priorityInJava;
    }

    private final byte priority;

    public byte getPriority() {
        return priority;
    }
}
