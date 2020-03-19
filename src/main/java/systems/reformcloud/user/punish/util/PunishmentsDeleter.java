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

import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;
import systems.reformcloud.user.punish.Punishment;
import systems.reformcloud.util.Constants;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * A runner which revokes every ten seconds all expired punishments and reloads every 10 minutes the
 * next expiring punishments.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class PunishmentsDeleter {

    private PunishmentsDeleter() {
        throw new UnsupportedOperationException();
    }

    private static Collection<Punishment> expiringPunishments = new CopyOnWriteArrayList<>();

    public static void startReload() {
        for (DefaultPunishmentTypes type : DefaultPunishmentTypes.values()) {
            GlobalAPI.getDatabaseDriver().createTable("punishments_" + type);
        }

        Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            expiringPunishments.addAll(PunishmentsDatabaseReader.getExpiringPunishments(DefaultPunishmentTypes.MUTE.name()));
            expiringPunishments.addAll(PunishmentsDatabaseReader.getExpiringPunishments(DefaultPunishmentTypes.BAN.name()));
        }, 0, 1, TimeUnit.MINUTES);
    }

    public static void startExpiredHandler() {
        Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            expiringPunishments
                    .stream()
                    .filter(e -> e.getTimeoutTime() <= System.currentTimeMillis())
                    .forEach(e -> {
                        e.revoke();
                        expiringPunishments.remove(e);
                    });
        }, 0, 1, TimeUnit.SECONDS);
    }
}
