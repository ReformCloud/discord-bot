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
package systems.reformcloud.util;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.config.ConfigUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Holds the current cloud version and updates it every minute
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class VersionChecker {

    private VersionChecker() {
        throw new UnsupportedOperationException();
    }

    private static String currentVersion = "unknown";

    public static void init() {
        Constants.SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(() -> openConnection(
                checkNotNull(ConfigUtil.parseProperties().getProperty("version-update-url")),
                stream -> {
                    try {
                        Properties properties = new Properties();
                        properties.load(stream);

                        VersionChecker.currentVersion = properties.getProperty("version");
                    } catch (final IOException ex) {
                        ex.printStackTrace();
                    }
                }
        ), 0, 1, TimeUnit.MINUTES);
    }

    @NotNull
    public static String getCurrentVersion() {
        return currentVersion;
    }

    public static void openConnection(@NotNull String url, @NotNull Consumer<InputStream> inputStreamConsumer) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                inputStreamConsumer.accept(inputStream);
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
