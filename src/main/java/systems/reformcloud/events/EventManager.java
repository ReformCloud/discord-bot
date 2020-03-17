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
package systems.reformcloud.events;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the event manager which is used to call events to the registered listeners.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface EventManager {

    /**
     * Calls an event
     *
     * @param event The event which should be called
     */
    void callEvent(@NotNull Event event);

    /**
     * Registers a event listener
     *
     * @param listener The listener which should get registered
     */
    void registerListener(@NotNull Object listener);

    /**
     * Unregisters a specific listener
     *
     * @param listener The listener which should get unregistered
     */
    void unregisterListener(@NotNull Object listener);

    /**
     * Unregisters all listeners
     */
    void unregisterAll();
}
