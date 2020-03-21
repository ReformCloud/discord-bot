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
package systems.reformcloud.docs.objects;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a method in an documented class
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class Method {

    public Method(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public final String name;

    public String returnType;

    public String returnValue = "";

    public final Map<String, Pair<String, Collection<String>>> parameters = new HashMap<>();

    public final Collection<String> annotations = new ArrayList<>();

    public final Collection<String> throwable = new ArrayList<>();

    public final String url;

    private boolean staticMethod = false;

    public void setStaticMethod() {
        this.staticMethod = true;
    }

    public boolean isStaticMethod() {
        return staticMethod;
    }

    public String getName() {
        return name;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public Map<String, Pair<String, Collection<String>>> getParameters() {
        return parameters;
    }

    public Collection<String> getAnnotations() {
        return annotations;
    }

    public Collection<String> getThrowable() {
        return throwable;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Method)) {
            return false;
        }

        var method = (Method) o;
        return method.getUrl().equals(this.url);
    }
}
