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
package systems.reformcloud.discord.config;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * A util for the discord config
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DiscordConfigUtil {

    private DiscordConfigUtil() {
        throw new UnsupportedOperationException();
    }

    private static final Path CONFIG = Paths.get("config.properties");

    private static Properties properties;

    @NotNull
    public static Properties parseProperties() {
        if (properties != null) {
            return properties;
        }

        properties = new Properties();

        if (Files.notExists(CONFIG)) {
            FileUtils.createNewFile(CONFIG);

            properties.setProperty("discord-token", "null");
            properties.setProperty("discord-guild", "null");
            properties.setProperty("discord-punish-role", "null");
            properties.setProperty("discord-log-channel", "null");
            properties.setProperty("discord-terminal-channel", "null");
            properties.setProperty("discord-info-channel", "null");

            properties.setProperty("discord-auto-mute-first", "5");
            properties.setProperty("discord-auto-mute-second", "10");
            properties.setProperty("discord-auto-ban", "15");

            try (OutputStream stream = Files.newOutputStream(CONFIG)) {
                properties.store(stream, "default configuration file");
            } catch (final IOException ex) {
                ex.printStackTrace();
            }

            return properties;
        }

        try (InputStream stream = Files.newInputStream(CONFIG)) {
            properties.load(stream);
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return properties;
    }
}
