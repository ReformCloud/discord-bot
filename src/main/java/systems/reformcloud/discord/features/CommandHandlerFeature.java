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
package systems.reformcloud.discord.features;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.command.source.DiscordCommandSource;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * The command handler for the discord env
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class CommandHandlerFeature extends DiscordFeature {

    @NotNull
    @Override
    public String getName() {
        return "CommandHandler";
    }

    private static final Cache<Long, Long> CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .ticker(Ticker.systemTicker())
            .build();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (!event.getChannel().getId().equals(DiscordUtil.getBotCommandsChannel().getId())
                && event.getMember() != null && !event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            return;
        }

        if (CACHE.asMap().containsKey(event.getAuthor().getIdLong())) {
            return;
        }

        if (GlobalAPI.getCommandMap().dispatchCommand(new DiscordCommandSource(
                event.getChannel(),
                event.getMember()
        ), event.getMessage().getContentRaw()) && event.getMember() != null && !event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            CACHE.put(event.getAuthor().getIdLong(), -1L);
        }
    }
}
