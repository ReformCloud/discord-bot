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
package systems.reformcloud.util.build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.util.VersionChecker;

/**
 * A simple helper for maven builds
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class MavenUtil {

    private MavenUtil() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static String formatItem(@Nullable String... items) {
        return "```xml\n" +
                getDependencies("", items) +
                "```";
    }

    @NotNull
    public static String getDependencies(@NotNull String intent, @Nullable String... extraItems) {
        StringBuilder builder = new StringBuilder();

        var format = intent + "    <dependency>\n" +
                intent + "        <groupId>systems.reformcloud.reformcloud2</groupId>\n" +
                intent + "        <artifactId>%s</artifactId>\n" +
                intent + "        <version>%s</version>\n" +
                intent + "    </dependency>";

        builder
                .append(intent)
                .append("<dependencies>\n")
                .append(String.format(format, "reformcloud2-executor-api", VersionChecker.getCurrentVersion()))
                .append("\n");

        if (extraItems != null) {
            for (String extraItem : extraItems) {
                builder
                        .append(String.format(format, extraItem, VersionChecker.getCurrentVersion()))
                        .append("\n");
            }
        }

        return builder.append(intent).append("</dependencies>").toString();
    }

    @NotNull
    public static String getFullMavenBuildConfig(@Nullable String... extra) {
        return "```xml\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n\n" +
                "    <groupId>com.example</groupId>\n" +
                "    <artifactId>example</artifactId>\n" +
                "    <version>1.0</version>\n\n" +
                getDependencies("    ", extra) +
                "\n\n" +
                "    <build>\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.apache.maven.plugins</groupId>\n" +
                "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                "                <version>3.8.1</version>\n" +
                "                <configuration>\n" +
                "                    <source>8</source>\n" +
                "                    <target>8</target>\n" +
                "                </configuration>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "    </build>\n" +
                "</project>\n" +
                "```";
    }
}
