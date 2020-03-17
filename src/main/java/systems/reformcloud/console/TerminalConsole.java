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
package systems.reformcloud.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * The console which is used for the default read of the console input and for the print of console
 * outputs.
 * <p>
 * To read a line with no prompt you can use:
 * <pre>{@code
 * public static void main(String... args) {
 *     String line = TerminalConsole#readLine(null);
 * }
 * }</pre>
 * <p>
 * If you need a command line prompt to show the user for example what he has to type:
 * <pre>{@code
 * public static void main(String... args) {
 *     String line = TerminalConsole#readLine("> ");
 * }
 * }</pre>
 * Here we have used the command prompt {@code >}.
 *
 * @author Pasqual Koschmieder
 * @see systems.reformcloud.console.basic.BasicTerminalConsole
 * @since 1.0
 */
public abstract class TerminalConsole extends Logger {

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @throws java.util.MissingResourceException if the resourceBundleName is non-null and
     *                                            no corresponding resource can be found.
     */
    public TerminalConsole() {
        super("SimpleTerminalLogger", null);
    }

    /**
     * @return The current line reader used by this console
     */
    @NotNull
    public abstract LineReader getLineReader();

    /**
     * Reads a line from the line using the given console prompt
     *
     * @param prompt The console prompt which should get used left from the cursor
     * @return The line read in the console
     */
    @NotNull
    public abstract String readLine(@Nullable String prompt);

    /**
     * Logs a message through the line reader into the console
     *
     * @param message The message which should get sent
     * @see #getLineReader()
     */
    public abstract void log(@NotNull String message);

    /**
     * Clears the screen of the current console completely
     */
    public abstract void clearScreen();

    /**
     * Closes the current terminal
     */
    public void close() {
        try {
            getLineReader().getTerminal().flush();
            getLineReader().getTerminal().close();
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
