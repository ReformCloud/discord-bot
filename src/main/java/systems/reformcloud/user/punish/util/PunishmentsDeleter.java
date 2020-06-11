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

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A runner which revokes every ten seconds all expired punishments and reloads every 10 minutes the
 * next expiring punishments.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class PunishmentsDeleter extends Thread {

    public PunishmentsDeleter() {
        for (DefaultPunishmentTypes type : DefaultPunishmentTypes.values()) {
            GlobalAPI.getDatabaseDriver().createTable("punishments_" + type);
        }
    }

    private static final Collection<Punishment> EXPIRING_PUNISHMENTS = new CopyOnWriteArrayList<>();

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            EXPIRING_PUNISHMENTS.addAll(PunishmentsDatabaseReader.getExpiringPunishments(DefaultPunishmentTypes.MUTE.name()));
            EXPIRING_PUNISHMENTS.addAll(PunishmentsDatabaseReader.getExpiringPunishments(DefaultPunishmentTypes.BAN.name()));

            EXPIRING_PUNISHMENTS
                    .stream()
                    .filter(e -> e.getTimeoutTime() <= System.currentTimeMillis())
                    .forEach(e -> {
                        e.revoke();
                        EXPIRING_PUNISHMENTS.remove(e);
                    });

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
