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
package systems.reformcloud.console.reader;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.commands.basic.source.ConsoleCommandSource;
import systems.reformcloud.console.TerminalConsole;
import systems.reformcloud.console.events.ConsoleLineReadEvent;

/**
 * Represents the console which handles all console inputs and executes the line in the command
 * manager.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class TerminalReader {

    private static final String PROMPT = System.getProperty(
            "console.prompt",
            "[" + System.getProperty("user.name") + "@" + System.getProperty("user.dir") + " ~]$ "
    );

    private TerminalReader() {
        throw new UnsupportedOperationException();
    }

    public static void start(@NotNull TerminalConsole terminalConsole) {
        String line;

        while (!Thread.interrupted()) {
            try {
                line = terminalConsole.readLine(PROMPT);
                while (!line.trim().isEmpty()) {
                    ConsoleLineReadEvent event = new ConsoleLineReadEvent(line);
                    GlobalAPI.getEventManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }

                    if (!GlobalAPI.getCommandMap().dispatchCommand(ConsoleCommandSource.INSTANCE, line)) {
                        System.out.println("Unknown command. Type \"help\" to see a list of available commands");
                    }

                    line = terminalConsole.readLine(PROMPT);
                }
            } catch (final Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }
}
