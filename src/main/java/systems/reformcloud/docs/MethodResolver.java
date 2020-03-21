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

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.docs.objects.Method;
import systems.reformcloud.docs.util.LeftRightHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static systems.reformcloud.util.StringUtils.replaceLast;

/**
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class MethodResolver {

    private MethodResolver() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static Collection<Method> parseMethods(@NotNull Collection<String> allHtmlLines, @NotNull String baseUrl) {
        Collection<Method> out = new ArrayList<>();
        Collection<String> copy = new ArrayList<>(allHtmlLines);

        for (int i = 0; i < 100; i++) {
            Collection<String> description = JavaDocParserUtil.getLinesUntil(copy, "<tr id=\"i" + i + "\" ", "</tr>");
            if (description.isEmpty()) {
                break;
            }

            String name = null;
            String url = null;

            for (String s : description) {
                if (s.startsWith("<td class=\"colLast\"><code><span class=\"memberNameLink\"><a href=\"")) {
                    s = s.replaceFirst("<td class=\"colLast\"><code><span class=\"memberNameLink\"><a href=\"", "");

                    int urlEndIndex = s.indexOf('>');
                    if (urlEndIndex != -1) {
                        url = JavaDocParserUtil.getUrlFromMainPage(replaceLast(s.substring(0, urlEndIndex), "\"", ""), baseUrl);
                        s = s.substring(urlEndIndex + 1);
                    }

                    int nextHtmlOpening = s.indexOf('<');
                    if (nextHtmlOpening != -1) {
                        name = s.substring(0, nextHtmlOpening);
                    }

                    if (name == null || url == null) {
                        continue;
                    }

                    out.add(new Method(name, url));
                }
            }
        }

        out.forEach(e -> resolveMethod(allHtmlLines, e, baseUrl));
        return out;
    }

    private static void resolveMethod(@NotNull Collection<String> allLines, @NotNull Method method, @NotNull String base) {
        int resolveNameIndex = method.getUrl().lastIndexOf('#');
        if (resolveNameIndex == -1) {
            return;
        }

        String resolveName = method.getUrl().substring(resolveNameIndex).replaceFirst("#", "");
        Collection<String> description = JavaDocParserUtil.getLinesUntil(allLines, "<a name=\"" + resolveName + "\">", "</dl>");

        String lastLine = "";
        boolean nextParams = false;

        for (String s : description) {
            boolean nowThrows = false;
            for (String s1 : s.split(" ")) {
                if (s1.equals("throws")) {
                    nowThrows = true;
                    continue;
                }

                if (nowThrows) {
                    method.getThrowable().add(JavaDocParserUtil.getClassName(s1));
                }
            }

            if (lastLine.equals("<dt><span class=\"returnLabel\">Returns:</span></dt>") && s.startsWith("<dd>")) {
                method.setReturnValue(JavaDocParserUtil.replaceLinks(replaceLast(s.replaceFirst("<dd>", "")
                                .replace("</code>", "").replace("<code>", "")
                                .replace("/code", "").replace("code", ""),
                        "</dd>", ""), base
                ));
            }

            if (s.startsWith("<dt><span class=")) {
                nextParams = false;
            }

            if (nextParams) {
                String parameterFull = replaceLast(s, "</dd>", "").replaceFirst("<dd>", "")
                        .replace("<code>", "").replaceFirst("</code>", "").replace("</dl>", "");
                String[] splitted = parameterFull.split(" - ");

                String name = parameterFull;
                String desc = "";

                if (splitted.length >= 1) {
                    name = splitted[0];
                }

                if (splitted.length >= 2) {
                    desc = String.join(" ", Arrays.copyOfRange(splitted, 1, splitted.length));
                }

                if (name.replaceAll("\\h", "").isEmpty()) {
                    continue;
                }

                method.getParameters().put(name, new LeftRightHolder<>(desc, new ArrayList<>()));
            }

            if (s.equals("<dt><span class=\"paramLabel\">Parameters:</span></dt>")) {
                nextParams = true;
            }

            lastLine = s;
        }

        String detail = String.join(" ", JavaDocParserUtil.getLinesUntil(description, "<pre>", "</pre>")).trim();
        String complete = StringEscapeUtils.unescapeHtml4(replaceLast(detail.replaceFirst("<pre>", ""), "</pre>", ""));
        complete = JavaDocParserUtil.replaceLinks(complete, base);

        for (String s : complete.split("\\h")) {
            if (s.startsWith("@")) {
                complete = complete.replaceFirst(s, "");
                method.getAnnotations().add(s);
                continue;
            }

            if ("public".equals(s) || "private".equals(s) || "protected".equals(s)) {
                continue;
            }

            if ("static".equals(s)) {
                method.setStaticMethod();
                continue;
            }

            method.setReturnType(s);
            break;
        }

        complete = complete.replace(method.getReturnType(), "")
                .replace(method.getName() + "(", "")
                .replace(")", "");

        if (!complete.contains(",")) {
            if (!complete.replaceAll("\\h", "").isEmpty()) {
                JavaDocParserUtil.resolveAnnotations(method, complete);
            }
        } else {
            for (String s : complete.split(",")) {
                JavaDocParserUtil.resolveAnnotations(method, s);
            }
        }
    }
}
