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
package systems.reformcloud.docs;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.config.ConfigUtil;
import systems.reformcloud.docs.objects.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Some util methods for the javadoc parser
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class JavaDocParserUtil {

    private JavaDocParserUtil() {
        throw new UnsupportedOperationException();
    }

    public static final String BASE_URL = Preconditions.checkNotNull(ConfigUtil.parseProperties().getProperty("docs-base-url"));

    private static final Pattern URL_PATTERN = Pattern.compile(".*(<a href=\"(.*)\" title=.*>(.*))");

    @NotNull
    public static String replaceLinks(@NotNull String complete, @NotNull String baseUrl) {
        String[] split = complete.split("</a>");
        for (String s : split) {
            Matcher matcher = URL_PATTERN.matcher(s);
            if (!matcher.find() || matcher.groupCount() != 3) {
                continue;
            }

            String url = getUrlFromMainPage(matcher.group(2), baseUrl);
            complete = complete.replace(matcher.group(1), "[" + matcher.group(3) + "](" + url + ")");
        }

        return complete.replace("</a>", "");
    }

    public static void resolveAnnotations(@NotNull Method method, @NotNull String s) {
        String[] split = s.split("\\h");
        if (split.length != 0) {
            String name = split[split.length - 1];
            if (split.length > 1 && method.getParameters().containsKey(name)) {
                method
                        .getParameters()
                        .get(name)
                        .getRight()
                        .addAll(getAllAnnotations(split));
            }
        }
    }

    @NotNull
    public static Collection<String> getAllAnnotations(@NotNull String[] strings) {
        Collection<String> out = new ArrayList<>();
        for (String string : strings) {
            if (string.startsWith("@")) {
                out.add(string);
            }
        }

        return out;
    }

    public static String getFromTo(@NotNull String base, @NotNull String from, @NotNull String to) {
        int index = base.indexOf(from);
        int end = base.indexOf(to);

        if (index != -1 && end != -1) {
            base = base.substring(index, base.length() < end ? end : end + 1);
        }

        return base;
    }

    @NotNull
    public static String getClassName(@NotNull String fullName) {
        int index = fullName.lastIndexOf('.');
        if (index != -1) {
            fullName = fullName.substring(index + 1);
        }

        return fullName;
    }

    @NotNull
    public static String getUrlFromMainPage(@NotNull String from, @NotNull String baseUrl) {
        while (from.startsWith("../")) {
            from = from.replaceFirst("../", "");
        }

        return baseUrl + from;
    }

    @NotNull
    public static Collection<String> getLinesUntil(@NotNull Collection<String> lines, @NotNull String from, @NotNull String to) {
        Collection<String> out = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(from)) {
                out.add(line);
                continue;
            }

            if (out.size() > 0) {
                if (line.replaceAll("\\h", "").equals(to) || line.replaceAll("\\h", "").endsWith(to)) {
                    out.add(line);
                    break;
                }

                out.add(line);
            }
        }

        return out;
    }

    @NotNull
    public static String getPackageFromClassUrl(@NotNull String url, @NotNull String baseUrl) {
        if (url.lastIndexOf('/') == -1) {
            return url;
        }

        return url.substring(0, url.lastIndexOf('/')).replaceFirst(baseUrl, "").replace("/", ".");
    }
}
