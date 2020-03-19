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
package systems.reformcloud.discord.user;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.user.User;
import systems.reformcloud.user.UserManagement;

import javax.annotation.Nonnull;
import java.time.Duration;

/**
 * Represents a {@link UserManagement} for the discord env.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordUserManagement implements UserManagement {

    static {
        GlobalAPI.getDatabaseDriver().createTable("DISCORD_USERS");
    }

    private final Cache<Long, User> userCache = CacheBuilder
            .newBuilder()
            .initialCapacity(10000)
            .maximumSize(10000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .ticker(Ticker.systemTicker())
            .build();

    @Override
    public @Nullable
    User getExistingUserById(long id) {
        User present = userCache.getIfPresent(id);
        if (present != null) {
            return present;
        }

        var user = GlobalAPI.getDatabaseDriver().getOrDefault(new DiscordUserDatabaseObjectToken(id), null);
        if (user == null) {
            return null;
        }

        userCache.put(id, user);
        return user;
    }

    @Override
    public @NotNull
    User getUserOrCreate(long id) {
        User alreadyCreatedUser = this.getExistingUserById(id);
        if (alreadyCreatedUser != null) {
            return alreadyCreatedUser;
        }

        var user = new DiscordUser(id);
        GlobalAPI.getDatabaseDriver().insert(user);
        userCache.put(id, user);

        return user;
    }

    @Override
    public void updateUser(@NotNull User user) {
        this.userCache.invalidate(user.getId());
        this.userCache.put(user.getId(), user);

        GlobalAPI.getDatabaseDriver().update(user);
    }

    @Override
    public void invalidate(@Nonnull User user) {
        this.userCache.invalidate(user.getId());
    }

    @Override
    public void flushAndClose() {
        userCache.invalidateAll();
        userCache.cleanUp();
    }
}
