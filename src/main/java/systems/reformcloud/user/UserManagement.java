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
package systems.reformcloud.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Represents any user management for for example discord or teamspeak.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface UserManagement {

    /**
     * Retrieves a specific user from the database by the given id
     *
     * @param id The id of the user
     * @return The user which is in the database or {@code null} if the user does not exists
     */
    @Nullable
    User getExistingUserById(long id);

    /**
     * Retrieves a specific user from the database by the given id or creates a new one if the user
     * does not exists yet
     *
     * @param id The id of the user
     * @return The user which is in the database or an newly created one
     */
    @NotNull
    User getUserOrCreate(long id);

    /**
     * Updates the given user in the database
     *
     * @param user The user which should get updated
     */
    void updateUser(@NotNull User user);

    /**
     * Invalidates the given user from the local cache
     *
     * @param user The user which should get invalidated from the cache
     */
    void invalidate(@Nonnull User user);

    /**
     * Flushes the cache into the database and closes the user management
     */
    void flushAndClose();
}
