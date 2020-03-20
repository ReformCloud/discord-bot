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
 * A simple helper for the gradle builds
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class GradleUtil {

    private GradleUtil() {
        throw new UnsupportedOperationException();
    }

    private static final String FORMAT = "    compileOnly group: 'systems.reformcloud.reformcloud2', name: '%s', version: '%s'";

    @NotNull
    public static String formatItem(@Nullable String... items) {
        return "```gradle" +
                "\n" +
                getDependencies(items) +
                "\n\n" +
                getRepository() +
                "\n" +
                "```";
    }

    @NotNull
    public static String getDependencies(@Nullable String... extraItems) {
        StringBuilder builder = new StringBuilder().append("dependencies {").append("\n");
        builder.append(String.format(FORMAT, "reformcloud2-executor-api", VersionChecker.getCurrentVersion())).append("\n");

        if (extraItems != null) {
            for (String extraItem : extraItems) {
                builder.append(String.format(FORMAT, extraItem, VersionChecker.getCurrentVersion())).append("\n");
            }
        }

        return builder.append("}").toString();
    }

    @NotNull
    public static String getFullBuildGradle(@Nullable String... extra) {
        return "```gradle\n" +
                "plugins {\n" +
                "    id 'java'\n" +
                "}\n\n" +
                "jar {\n" +
                "    manifest {\n" +
                "        attributes 'Main-Class': 'com.example.project.Main'\n" +
                "        attributes 'Implementation-Version': '1.0'\n" +
                "        attributes 'Specification-Version': 'SNAPSHOT'\n" +
                "    }\n" +
                "}\n\n" +
                "group 'com.example.project'\n" +
                "version '1.0'\n\n" +
                "sourceCompatibility = 1.8\n" +
                "targetCompatibility = 1.8\n\n" +
                getDependencies(extra) +
                "\n\n" +
                getRepository() +
                "\n\n" +
                "compileJava.options.encoding = 'UTF-8'" +
                "```";
    }

    @NotNull
    public static String getRepository() {
        return "repositories {\n    mavenCentral()\n}";
    }
}
