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
package systems.reformcloud.discord.event;

import net.dv8tion.jda.api.JDA;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.events.Event;
import systems.reformcloud.user.User;
import systems.reformcloud.user.warn.Warn;

/**
 * This event gets called when an user gets warned
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordUserWarnEvent extends Event {

    public DiscordUserWarnEvent(Warn warn, Bot<JDA> discordBot, CommandSource warner, User user) {
        this.warn = warn;
        this.discordBot = discordBot;
        this.warner = warner;
        this.user = user;
    }

    private final Warn warn;

    private final Bot<JDA> discordBot;

    private final CommandSource warner;

    private final User user;

    public Warn getWarn() {
        return warn;
    }

    public Bot<JDA> getDiscordBot() {
        return discordBot;
    }

    public CommandSource getWarner() {
        return warner;
    }

    public User getUser() {
        return user;
    }
}
