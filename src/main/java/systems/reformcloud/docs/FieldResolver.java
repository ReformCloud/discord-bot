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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import systems.reformcloud.docs.objects.Field;
import systems.reformcloud.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class FieldResolver {

    private FieldResolver() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Collection<Field> parseFields(@NotNull Collection<String> all, @NotNull String base, @NotNull String classUrl) {
        Collection<Field> out = new ArrayList<>();
        Collection<String> linesUntil = JavaDocParserUtil.getLinesUntil(all, "<h3>Field Detail</h3>", "</h3>");

        linesUntil.remove("<h3>Field Detail</h3>");

        Field field;
        while (!linesUntil.isEmpty()) {
            Collection<String> fieldLines = JavaDocParserUtil.getLinesUntil(linesUntil, "<a name=\"", "</pre>");
            linesUntil.removeAll(fieldLines);

            if ((field = resolveField(fieldLines, base, classUrl)) != null) {
                out.add(field);
            } else {
                break;
            }
        }

        return out;
    }

    @Nullable
    private static Field resolveField(@NotNull Collection<String> field, @NotNull String baseUrl, @NotNull String classUrl) {
        String name = "";
        String url = "";
        String returnType = "";

        for (String s : field) {
            if (s.startsWith("<h4>")) {
                name = s.replace("<h4>", "").replace("</h4>", "");
                url = classUrl + "#" + name;
                continue;
            }

            if (s.startsWith("<pre>")) {
                if (!s.endsWith(name + "</pre>")) {
                    break;
                }

                s = s.replaceFirst("<pre>", "");
                s = s.replace("</a> " + name + "</pre>", "");

                int index = s.indexOf('<');
                if (index != -1) {
                    s = s.substring(index);
                }

                String replace = JavaDocParserUtil.getFromTo(s, "title=\"", "\">");
                s = s.replace(" " + replace, "");

                int indexOfTypeName = s.lastIndexOf('>');
                if (indexOfTypeName != -1) {
                    String typeName = s.substring(indexOfTypeName).replace(">", "");
                    s = StringUtils.replaceLast(s.replace(">" + typeName, "").replaceFirst("<a href=\"", ""), "\"", "");

                    returnType = "[" + typeName + "](" + JavaDocParserUtil.getUrlFromMainPage(s, baseUrl) + ")";
                }
            }
        }

        if (name.isEmpty() || url.isEmpty() || returnType.isEmpty()) {
            System.out.println(String.join(" ", field));
            return null;
        }

        return new Field(returnType, url, name);
    }
}
