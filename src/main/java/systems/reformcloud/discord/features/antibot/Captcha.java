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

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Util for server join captcha when the discord might be under attack
 *
 * @author Pasqual Koschmieder
 * @since 1.1
 */
public final class Captcha {

    private Captcha() {
        throw new UnsupportedOperationException();
    }

    static final Map<Long, SentCaptcha> PER_USER_CAPTCHA = new ConcurrentHashMap<>();

    public static void sendCaptcha(@NotNull Member member, @NotNull Consumer<Member> onSuccess) {
        member.getUser().openPrivateChannel().queue(channel -> {
            long expectedResult = Math.round(Math.random() * (99999 - 10000));
            String toSend = "```\n" + requestFromAPI(expectedResult) + "```\n Please validate that you are a human by entering the code!";

            SentCaptcha captcha = new SentCaptcha(Long.toString(expectedResult), member, onSuccess);
            PER_USER_CAPTCHA.put(member.getIdLong(), captcha);

            channel.sendMessage(toSend).queue();
        }, error -> {
        });
    }

    @NotNull
    private static String requestFromAPI(long number) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(
                    "https://artii.herokuapp.com/make?text=" + number + "&font=starwars"
            ).openConnection();
            connection.setConnectTimeout(1000);
            connection.setUseCaches(true);
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.connect();

            if (connection.getResponseCode() != 200) {
                return Long.toString(number);
            }

            StringBuilder builder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                reader.lines().forEach(e -> builder.append(e).append("\n"));
            }

            return builder.toString();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return Long.toString(number);
    }

    public static class SentCaptcha {

        public SentCaptcha(String expected, Member member, Consumer<Member> then) {
            this.expected = expected;
            this.member = member;
            this.then = then;
        }

        private final String expected;

        private final Member member;

        private final Consumer<Member> then;

        private int tries = 0;

        public String getExpected() {
            return expected;
        }

        public Member getMember() {
            return member;
        }

        public Consumer<Member> getThen() {
            return then;
        }

        public int getTries() {
            return tries;
        }

        public void addTry() {
            this.tries++;
        }
    }
}
