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
package systems.reformcloud.commands.source;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.util.Nameable;

/**
 * Represents any source of a command, for example the console source.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface CommandSource extends Nameable {

    /**
     * Sends a message to the command sender
     *
     * @param message The message which should get sent
     */
    void sendMessage(@NotNull String message);

    /**
     * Checks if the current command source has the given permission
     *
     * @param permission The permission which has to be present
     * @return If the user has the given permission
     */
    boolean hasPermission(@NotNull Object permission);

    /**
     * @return The id of the command source
     */
    long getId();

    /**
     * @return The id of the source channel
     */
    long getSourceChannel();
}
