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
package systems.reformcloud.user.punish.util;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.user.punish.Punishment;
import systems.reformcloud.user.punish.basic.PunishmentDatabaseObjectToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A simple database reader which
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class PunishmentsDatabaseReader {

    private PunishmentsDatabaseReader() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Map<UUID, Punishment> getExpiringPunishments(@NotNull String type) {
        Map<UUID, Punishment> map = new HashMap<>();
        GlobalAPI.getDatabaseDriver().keys("punishments_" + type)
                .filter(Objects::nonNull)
                .map(e -> {
                    try {
                        return Long.parseLong(e);
                    } catch (final NumberFormatException ex) {
                        return null;
                    }
                }).filter(Objects::nonNull)
                .filter(e -> e != -1)
                .filter(e -> (e + TimeUnit.MINUTES.toMillis(10) > System.currentTimeMillis()))
                .map(e -> GlobalAPI.getDatabaseDriver().getOrDefault(new PunishmentDatabaseObjectToken(e, type), null))
                .filter(Objects::nonNull)
                .forEach(punishment -> map.putIfAbsent(punishment.getUniqueID(), punishment));
        return map;
    }
}
