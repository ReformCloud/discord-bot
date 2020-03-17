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

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.user.User;
import systems.reformcloud.user.information.BasicUserInformation;
import systems.reformcloud.user.information.UserInformation;
import systems.reformcloud.user.punish.Punishment;
import systems.reformcloud.user.warn.Warn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents an implantation of an {@link User} for the discord env
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordUser implements User {

    public DiscordUser(long id) {
        this.id = id;
        this.userInformation = new BasicUserInformation();
        this.warns = new ArrayList<>();
        this.punishments = new ArrayList<>();
    }

    DiscordUser(long id, UserInformation userInformation, Collection<Warn> warns, Collection<Punishment> punishments) {
        this.id = id;
        this.userInformation = userInformation;
        this.warns = warns;
        this.punishments = punishments;
    }

    private final long id;

    private final UserInformation userInformation;

    private final Collection<Warn> warns;

    private final Collection<Punishment> punishments;

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public @NotNull
    UserInformation getInformation() {
        return this.userInformation;
    }

    @Override
    public @NotNull
    Collection<Warn> getWarns() {
        return this.warns;
    }

    @Override
    public @NotNull
    Collection<Punishment> getPunishments() {
        return this.punishments;
    }

    @Override
    public @NotNull
    String getKey() {
        return Long.toString(id);
    }

    @Override
    public @NotNull
    String getTable() {
        return "discord_users";
    }

    @Override
    public @NotNull
    byte[] serialize() {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream stream = new ObjectOutputStream(byteArrayOutputStream)) {
            stream.writeLong(this.id);

            stream.writeObject(this.userInformation);
            stream.writeObject(this.warns);
            stream.writeObject(this.punishments);

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return new byte[0];
    }
}
