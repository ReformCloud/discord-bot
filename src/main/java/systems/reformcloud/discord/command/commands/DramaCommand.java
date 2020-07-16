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
package systems.reformcloud.discord.command.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.util.Constants;

import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DramaCommand extends BasicDiscordCommand {

    public DramaCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!drama", new String[0], "Generates drama");
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        var result = this.getDramaObject();
        if (result == null || !result.has("response") || !result.has("permalink")) {
            source.sendMessage("Unable to generate drama");
            return;
        }

        var responseText = result.get("response").getAsString();
        var permalink = result.get("permalink").getAsString();

        this.parent.getCurrentInstance().ifPresent(jda -> {
            var channel = jda.getTextChannelById(source.getSourceChannel());
            if (channel == null) {
                return;
            }

            var builder = new EmbedBuilder()
                    .setTitle("Spigot Drama", permalink)
                    .setColor(Color.YELLOW)
                    .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()))
                    .addField(new MessageEmbed.Field("", responseText, true));
            channel.sendMessage(builder.build()).queue();
        });
    }

    @Nullable
    private JsonObject getDramaObject() {
        try {
            var connection = (HttpURLConnection) new URL("https://chew.pw/api/spigotdrama").openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            if (connection.getResponseCode() != 200) {
                return null;
            }

            try (var reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                var element = JsonParser.parseReader(reader);
                return element.isJsonObject() ? element.getAsJsonObject() : null;
            }
        } catch (IOException exception) {
            return null;
        }
    }
}
