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
package systems.reformcloud.discord.features.logger;

import com.google.common.base.Ticker;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.event.DiscordUserJoinEvent;
import systems.reformcloud.discord.features.DiscordFeature;
import systems.reformcloud.util.Constants;
import systems.reformcloud.util.KeyValueHolder;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents the logger as a feature of the discord bot
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class LoggerFeature extends DiscordFeature {

    public static final Cache<Long, GuildMessage> messageCache = CacheBuilder
            .newBuilder()
            .initialCapacity(10000)
            .maximumSize(10000)
            .expireAfterAccess(Duration.ofMinutes(30))
            .ticker(Ticker.systemTicker())
            .build();

    @Override
    public void handleStart(@NotNull Bot<JDA> bot) {
        GlobalAPI.getDatabaseDriver().createTable("discord_messages");

        bot.getCurrentInstance().ifPresent(e -> {
            Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
                GlobalAPI.getDatabaseDriver().getAll(
                        "discord_messages",
                        GuildMessageDatabaseObjectToken.MAPPER
                )
                        .stream()
                        .filter(m -> (m.getCreationTime() + TimeUnit.DAYS.toMillis(30)) < System.currentTimeMillis())
                        .forEach(message -> {
                            GlobalAPI.getDatabaseDriver().deleteFromTable("discord_messages", Long.toString(message.getMessageId()));
                            System.out.println("Cleaned message " + message.getMessageId() + " from database");
                        });
            }, 0, 12, TimeUnit.HOURS);

            super.handleStart(bot);
        });
    }

    @NotNull
    @Override
    public String getName() {
        return "Logger";
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            if (event.getChannel().getId().equals(DiscordUtil.getLoggingChannel().getId())
                    || event.getChannel().getId().equals(DiscordUtil.getTerminalChannel().getId())) {
                return;
            }

            event.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
            return;
        }

        Constants.EXECUTOR_SERVICE.execute(() -> {
            var message = GuildMessage.fromMessage(event.getMessage());

            messageCache.put(event.getMessageIdLong(), message);
            GlobalAPI.getDatabaseDriver().insert(message);
        });
    }

    @Override
    public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        Constants.EXECUTOR_SERVICE.execute(() -> {
            var oldMessage = this.getMessageFromCacheOrDatabase(event);
            var message = GuildMessage.fromMessage(event.getMessage());
            if (oldMessage == null) {
                this.onGuildMessageReceived(new GuildMessageReceivedEvent(event.getJDA(), event.getResponseNumber(), event.getMessage()));
                return;
            }

            messageCache.invalidate(event.getMessageIdLong());
            messageCache.put(event.getMessageIdLong(), message);
            GlobalAPI.getDatabaseDriver().update(message);

            log(
                    event.getMessage().getAuthor().getName() + " updated message from "
                            + Constants.DATE_FORMAT.format(oldMessage.getCreationTime()),
                    new KeyValueHolder<>("Message before", oldMessage.getMessage()),
                    new KeyValueHolder<>("Message now", message.getMessage())
            );
        });
    }

    @Override
    public void onGuildMessageDelete(@Nonnull GuildMessageDeleteEvent event) {
        Constants.EXECUTOR_SERVICE.execute(() -> {
            var message = this.getMessageFromCacheOrDatabase(event);
            if (message == null) {
                return;
            }

            event.getJDA().retrieveUserById(message.getUserId()).queue(user -> {
                log(
                        "User " + user.getName() + " deleted a message",
                        new KeyValueHolder<>("last edited", Constants.DATE_FORMAT.format(message.getCreationTime())),
                        new KeyValueHolder<>("message", message.getMessage())
                );
            }, error -> {
                log(
                        "Unknown user deleted a message",
                        new KeyValueHolder<>("last edited", Constants.DATE_FORMAT.format(message.getCreationTime())),
                        new KeyValueHolder<>("message", message.getMessage())
                );
            });

            messageCache.invalidate(message.getMessageId());
            GlobalAPI.getDatabaseDriver().deleteFromTable(message);
        });
    }

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        Constants.EXECUTOR_SERVICE.execute(() -> {
            var systemUser = this.getApi().getAssociatedUserManagement().getExistingUserById(event.getMember().getIdLong());
            if (systemUser == null) {
                systemUser = this.getApi().getAssociatedUserManagement().getUserOrCreate(event.getMember().getIdLong());

                systemUser.getInformation().addJoin();
                systemUser.getInformation().setJoinTime();
                this.getApi().getAssociatedUserManagement().updateUser(systemUser);

                log(
                        "A new user joined the discord server (" + event.getMember().getUser().getName() + ")",
                        new KeyValueHolder<>("id", Long.toString(systemUser.getId()))
                );

                GlobalAPI.getEventManager().callEvent(new DiscordUserJoinEvent(event, systemUser));
                return;
            }

            systemUser.getInformation().addJoin();
            systemUser.getInformation().setJoinTime();
            this.getApi().getAssociatedUserManagement().updateUser(systemUser);

            log(
                    "An old user joined the discord again (" + event.getMember().getUser().getName() + ")",
                    new KeyValueHolder<>("id", Long.toString(systemUser.getId())),
                    new KeyValueHolder<>("first join", Constants.DATE_FORMAT.format(systemUser.getInformation().getFirstJoinTimeInMillis())),
                    new KeyValueHolder<>("total joins", Long.toString(systemUser.getInformation().getTotalJoins())),
                    new KeyValueHolder<>("warns", Integer.toString(systemUser.getWarns().size())),
                    new KeyValueHolder<>("punishments", Integer.toString(systemUser.getPunishments().size()))
            );

            GlobalAPI.getEventManager().callEvent(new DiscordUserJoinEvent(event, systemUser));
        });
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        Constants.EXECUTOR_SERVICE.execute(() -> {
            var systemUser = this.getApi().getAssociatedUserManagement().getExistingUserById(event.getUser().getIdLong());
            if (systemUser == null) {
                return;
            }

            log(
                    "The user " + event.getUser().getName() + " left the discord",
                    new KeyValueHolder<>("join time", Constants.DATE_FORMAT.format(systemUser.getInformation().getJoinTimeInMillis())),
                    new KeyValueHolder<>("total joins", Long.toString(systemUser.getInformation().getTotalJoins()))
            );
            this.getApi().getAssociatedUserManagement().invalidate(systemUser);
        });
    }

    @Override
    public void onGuildBan(@Nonnull GuildBanEvent event) {
        Constants.EXECUTOR_SERVICE.execute(() -> {
            var systemUser = this.getApi().getAssociatedUserManagement().getExistingUserById(event.getUser().getIdLong());
            if (systemUser == null) {
                return;
            }

            log(
                    "The user " + event.getUser().getName() + " got banned from the discord",
                    new KeyValueHolder<>("join time", Constants.DATE_FORMAT.format(systemUser.getInformation().getJoinTimeInMillis())),
                    new KeyValueHolder<>("total joins", Long.toString(systemUser.getInformation().getTotalJoins()))
            );
            this.getApi().getAssociatedUserManagement().invalidate(systemUser);
        });
    }

    @Override
    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        Constants.EXECUTOR_SERVICE.execute(() -> {
            var systemUser = this.getApi().getAssociatedUserManagement().getExistingUserById(event.getUser().getIdLong());
            if (systemUser == null) {
                return;
            }

            log(
                    "The user " + event.getUser().getName() + " got unbanned from the discord",
                    new KeyValueHolder<>("last join time", Constants.DATE_FORMAT.format(systemUser.getInformation().getJoinTimeInMillis())),
                    new KeyValueHolder<>("total joins", Long.toString(systemUser.getInformation().getTotalJoins()))
            );
            this.getApi().getAssociatedUserManagement().invalidate(systemUser);
        });
    }

    @Nullable
    private GuildMessage getMessageFromCacheOrDatabase(@NotNull GenericGuildMessageEvent event) {
        var message = messageCache.getIfPresent(event.getMessageIdLong());
        if (message == null) {
            message = GlobalAPI.getDatabaseDriver().getOrDefault(new GuildMessageDatabaseObjectToken(event.getMessageIdLong()), null);
        }

        return message;
    }

    @SafeVarargs
    public static void log(@NotNull String message, @NotNull Map.Entry<String, String>... fieldMessages) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor("ReformCloudSystems")
                .setDescription("ReformCloud | Logger")
                .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()))
                .setTitle(message.length() > 256 ? message.substring(0, 253) + "..." : message);
        for (Map.Entry<String, String> fieldMessage : fieldMessages) {
            builder.addField(
                    fieldMessage.getKey().length() > 256 ? fieldMessage.getKey().substring(0, 253) + "..." : fieldMessage.getKey(),
                    fieldMessage.getValue().length() > 1024 ? fieldMessage.getValue().substring(0, 1021) + "..." : fieldMessage.getValue(),
                    false
            );
        }

        DiscordUtil.getLoggingChannel().sendMessage(builder.build()).queue();
    }
}
