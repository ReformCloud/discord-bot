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
package systems.reformcloud.discord.features.antibot;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.discord.DiscordUtil;
import systems.reformcloud.discord.features.DiscordFeature;
import systems.reformcloud.discord.features.logger.GuildMessage;
import systems.reformcloud.discord.features.logger.LoggerFeature;
import systems.reformcloud.discord.listener.DiscordUserJoinListener;
import systems.reformcloud.discord.punishments.DiscordPunishment;
import systems.reformcloud.user.punish.DefaultPunishmentTypes;
import systems.reformcloud.util.Constants;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Prevents bots and spammer from attacking the discord server
 *
 * @author Pasqual Koschmieder
 * @since 1.1
 */
public class AntiSelfBotFeature extends DiscordFeature {

    public AntiSelfBotFeature() {
        Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
            if (this.spamDetectorCount > 0 && --this.spamDetectorCount == 0) {
                DiscordUserJoinListener.giveMemberRole = true;
            }
        }, 20, 20, TimeUnit.SECONDS);
    }

    private long lastJoinTimeMillis = 0;

    private int spamDetectorCount;

    @Override
    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        if ((System.currentTimeMillis() - lastJoinTimeMillis) < 60000 && ++spamDetectorCount >= 5) {
            DiscordUserJoinListener.giveMemberRole = false;
        }

        lastJoinTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        List<GuildMessage> collect = LoggerFeature.messageCache.asMap().values()
                .stream()
                .filter(e -> e.getCreationTime() + TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis())
                .filter(e -> e.getUserId() == event.getAuthor().getIdLong())
                .collect(Collectors.toList());
        if (collect.size() <= 20) {
            return;
        }

        var user = this.getApi().getAssociatedUserManagement().getExistingUserById(event.getAuthor().getIdLong());
        if (user == null || user.getPunishments().size() == 1 || this.getApi().getCurrentInstance().isEmpty()) {
            return;
        }

        user.getPunishments().add(new DiscordPunishment(
                user.getId(),
                System.currentTimeMillis(),
                this.getApi().getCurrentInstance().get().getSelfUser().getIdLong(),
                System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1),
                this.getApi().getCurrentInstance().get().getSelfUser().getName(),
                DefaultPunishmentTypes.MUTE.name(),
                "Auto punishment because of 20+ messages in one minute! Calm down my friend"
        ));
        this.getApi().getAssociatedUserManagement().updateUser(user);
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (!event.getMessageId().equals(DiscordUtil.getCaptchaRequestMessage())
                || Captcha.PER_USER_CAPTCHA.containsKey(event.getUser().getIdLong())) {
            return;
        }

        event.getReaction().removeReaction(event.getUser()).queue();
        Captcha.sendCaptcha(event.getMember(), member -> {
            if (DiscordUtil.getGuild().getMemberById(member.getId()) == null) {
                return;
            }

            DiscordUserJoinListener.addMemberRoleAndSendGreeting(member);
        });
    }

    @Override
    public void onPrivateMessageReceived(@Nonnull PrivateMessageReceivedEvent event) {
        Captcha.SentCaptcha captcha = Captcha.PER_USER_CAPTCHA.get(event.getAuthor().getIdLong());
        if (captcha == null) {
            return;
        }

        String result = event.getMessage().getContentRaw().trim();
        if (!result.equals(captcha.getExpected())) {
            captcha.addTry();
            if (captcha.getTries() >= 5) {
                event.getChannel().sendMessage("Please try again:").queue();
                Captcha.sendCaptcha(captcha.getMember(), captcha.getThen());
                return;
            }

            event.getChannel().sendMessage("Wrong captcha!").queue();
            return;
        }

        Captcha.PER_USER_CAPTCHA.remove(event.getAuthor().getIdLong());
        event.getChannel().sendMessage("Thank you!").queue();
        captcha.getThen().accept(captcha.getMember());
    }

    @Override
    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        Captcha.PER_USER_CAPTCHA.remove(event.getUser().getIdLong());
    }

    @NotNull
    @Override
    public String getName() {
        return "AntiSelfBot";
    }
}
