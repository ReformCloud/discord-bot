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
package systems.reformcloud.api;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.commands.CommandMap;
import systems.reformcloud.database.DatabaseDriver;
import systems.reformcloud.events.EventManager;
import systems.reformcloud.handler.ReformCloudSystemsBotHandler;

/**
 * The global api which holds the only existing instance of the {@link ReformCloudSystemsBotHandler}
 * and because of this it's the only class with access to the running sub modules.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class GlobalAPI {

    private GlobalAPI() {
        throw new UnsupportedOperationException();
    }

    private static ReformCloudSystemsBotHandler parent;

    /**
     * Sets the current handler of all actions running in the system
     *
     * @param parent The parent handler of this current api
     */
    public static void setParent(@NotNull ReformCloudSystemsBotHandler parent) {
        Preconditions.checkArgument(GlobalAPI.parent == null, "Cannot redefine singleton parent");
        GlobalAPI.parent = parent;
    }

    /**
     * @return The current event manager which is used
     */
    @NotNull
    public static EventManager getEventManager() {
        return GlobalAPI.parent.getEventManager();
    }

    /**
     * @return The currently running command map
     */
    @NotNull
    public static CommandMap getCommandMap() {
        return GlobalAPI.parent.getCommandMap();
    }

    /**
     * @return The database driver which is currently active
     */
    @NotNull
    public static DatabaseDriver getDatabaseDriver() {
        return GlobalAPI.parent.getDatabaseDriver();
    }
}
