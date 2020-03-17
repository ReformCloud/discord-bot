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
package systems.reformcloud.bot;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.user.UserManagement;

/**
 * Represents a bot
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface Bot<T> {

    /**
     * Connects the bot and sets the instance for the remote handling
     *
     * @param connectionHandler The connection handler which is associated with the current bot backend
     */
    void doConnect(@NotNull BotConnectionHandler<T> connectionHandler);

    /**
     * Inits the event handlers all commands associated to this bot. Should be called after the
     * {@link #doConnect(BotConnectionHandler)} method to ensure the parent handlers are created.
     *
     * @throws IllegalStateException If the bot is not initialized yet or already stopped
     */
    void init();

    /**
     * @return The user management which is associated with the current bot instance
     */
    @NotNull
    UserManagement getAssociatedUserManagement();

    /**
     * @return If the bot is online and connected
     */
    boolean isConnected();

    /**
     * Shuts the bot immediately down
     */
    void shutdownNow();
}
