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
package systems.reformcloud.discord.command.source;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.commands.source.CommandSource;

/**
 * Represents the discord command sender
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DiscordCommandSource implements CommandSource {

    public DiscordCommandSource(TextChannel textChannel, Member member) {
        this.textChannel = textChannel;
        this.member = member;
    }

    private final TextChannel textChannel;

    private final Member member;

    @Override
    public void sendMessage(@NotNull String message) {
        this.textChannel.sendMessage(message).queue();
    }

    @Override
    public boolean hasPermission(@NotNull Object permission) {
        return permission instanceof Permission && member.hasPermission((Permission) permission);
    }

    @Override
    public long getId() {
        return member.getIdLong();
    }

    @Override
    public long getSourceChannel() {
        return this.textChannel.getIdLong();
    }

    @NotNull
    @Override
    public String getName() {
        return member.getUser().getName();
    }
}
