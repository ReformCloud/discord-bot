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
package systems.reformcloud.discord;

import com.google.common.base.Preconditions;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.bot.BotConnectionHandler;
import systems.reformcloud.config.ConfigUtil;

import javax.security.auth.login.LoginException;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

/**
 * Represents a default implementation of the {@link BotConnectionHandler} for the discord env.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class DiscordConnectionHandler implements BotConnectionHandler<JDA> {

    @Nullable
    @Override
    public JDA connect() {
        try {
            return JDABuilder
                    .createDefault(Preconditions.checkNotNull(
                            ConfigUtil.parseProperties().getProperty("discord-token"),
                            "Unable to read discord token"
                    ), GatewayIntent.getIntents(ALL_INTENTS & ~getRaw(GUILD_PRESENCES, GUILD_MESSAGE_TYPING, DIRECT_MESSAGE_TYPING)))
                    .setAutoReconnect(true)
                    .setEnableShutdownHook(true)
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing(ConfigUtil.parseProperties().getProperty("discord-activity", "nothing")))
                    .build()
                    .awaitReady();
        } catch (final LoginException | InterruptedException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
