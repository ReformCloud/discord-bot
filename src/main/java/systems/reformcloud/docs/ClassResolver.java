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

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.docs.cache.ClassesCache;
import systems.reformcloud.docs.objects.Class;
import systems.reformcloud.docs.objects.Field;
import systems.reformcloud.docs.objects.Method;
import systems.reformcloud.docs.util.LeftRightHolder;
import systems.reformcloud.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class ClassResolver {

    private ClassResolver() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static List<Pair<String, String>> searchForClasses(@NotNull String classToSearch, @NotNull String baseUrl) {
        List<Pair<String, String>> docLinks = new ArrayList<>();
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "allclasses-noframe.html").openConnection();
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );

            if (classToSearch.contains("#")) {
                String[] split = classToSearch.split("#");
                classToSearch = split.length == 2 ? split[0] : classToSearch;
            }

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                while (scanner.hasNext()) {
                    String raw = scanner.next();

                    if (!raw.contains("href=")) {
                        continue;
                    }

                    int index = raw.lastIndexOf('/');
                    if (index == -1) {
                        continue;
                    }

                    String className = StringUtils.replaceLast(raw.substring(index + 1), ".html\"", "");
                    String url = baseUrl + raw.replace("href=", "").replace("\"", "");

                    if (classToSearch.toLowerCase().startsWith(className.toLowerCase())) {
                        docLinks.add(new LeftRightHolder<>(className, url));
                    }

                    if (classToSearch.toLowerCase().equals(className.toLowerCase())) {
                        docLinks.clear();
                        docLinks.add(new LeftRightHolder<>(className, url));
                        break;
                    }
                }
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return docLinks;
    }

    @Nullable
    public static Class resolveClass(@NotNull String baseUrl, @NotNull String classUrl, @NotNull String className) {
        var cached = ClassesCache.getCachedClass(className);
        if (cached.size() == 1) {
            return cached.get(0);
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(classUrl).openConnection();
            connection.setConnectTimeout(1500);
            connection.setReadTimeout(1500);
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11"
            );
            connection.setUseCaches(true);

            if (connection.getResponseCode() != 200) {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                Collection<String> htmlLines = reader.lines().collect(Collectors.toList());

                Collection<Method> methods = MethodResolver.parseMethods(new ArrayList<>(htmlLines), baseUrl);
                Collection<Field> fields = FieldResolver.parseFields(new ArrayList<>(htmlLines), baseUrl, classUrl);

                var classToCache = new Class(
                        JavaDocParserUtil.getPackageFromClassUrl(classUrl, baseUrl) + "." + className,
                        className,
                        classUrl,
                        fields,
                        methods
                );
                updateClassInformation(htmlLines, classToCache);
                ClassesCache.cache(classToCache);
                return classToCache;
            }
        } catch (final IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static void updateClassInformation(@NotNull Collection<String> allLines, @NotNull Class classToUpdate) {
        for (String allLine : allLines) {
            if (allLine.startsWith("<h2 title=\"")) {
                classToUpdate.setType(allLine.replaceFirst("<h2 title=\"", "").split("\\h")[0]);
                break;
            }
        }
    }
}
