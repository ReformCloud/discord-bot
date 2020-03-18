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
package systems.reformcloud.bot.feature;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.annotations.UndefinedNullability;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.util.Nameable;

/**
 * Represents a feature a bot can have
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface BotFeature<T> extends Nameable {

    /**
     * Handles the start of the bot for which the feature is made
     *
     * @param bot The bot for which the feature is made
     * @throws IllegalStateException If the feature is already initialized
     * @see #isApplicableTo(Bot)
     */
    void handleStart(@NotNull Bot<T> bot);

    /**
     * Handles the stop of the parent bot
     */
    void handleStop();

    /**
     * @return The current api instance or {@code null} if the feature is not loaded or already stopped
     */
    @UndefinedNullability
    Bot<T> getApi();

    /**
     * @return If the current feature is initialized and running
     */
    boolean isInitialized();

    /**
     * Checks if the current feature is made for the given bot
     *
     * @param bot The bot which should get checked
     * @return If the feature is made for the bot
     */
    boolean isApplicableTo(@NotNull Bot<?> bot);
}
