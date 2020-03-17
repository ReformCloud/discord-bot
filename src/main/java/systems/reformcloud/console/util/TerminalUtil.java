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
package systems.reformcloud.console.util;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Util class for console reading. It's allows you to build a new terminal and the LineReader for
 * a console without any big messy code:
 * <p>
 * Creating a line reader with this class looks like this:
 * <pre>{@code
 * public static void main(String... args) {
 *     try {
 *         Terminal terminal = TerminalBuilderUtil.createNewTerminal();
 *         LineReader reader = TerminalBuilderUtil.newLineReader(terminal);
 *     } catch (final IOException ex) {
 *         ex.printStackTrace();
 *     }
 * }
 * }</pre>
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class TerminalUtil {

    /**
     * We let nobody see the constructor.
     *
     * @throws UnsupportedOperationException If someone tries to instantiate this class using reflections
     */
    private TerminalUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new system terminal for the basic application usage
     *
     * @return A new system terminal with some default values already set
     * @throws IOException If an i/o error occurs during the create of the terminal
     */
    public static Terminal createNewTerminal() throws IOException {
        return TerminalBuilder
                .builder()
                .system(true)
                .encoding(StandardCharsets.UTF_8)
                .build();
    }

    /**
     * Creates a new line reader for the given terminal
     *
     * @param terminal The terminal for which the reader is built
     * @return A new line reader which has some defaults options already set
     */
    public static LineReader newLineReader(@NotNull Terminal terminal) {
        return LineReaderBuilder
                .builder()
                .terminal(terminal)
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .build();
    }

    /**
     * Reads a line from the current line reader
     *
     * @param reader The line reader from which the console should get read from
     * @param prompt The prompt which should get used to read the console input
     * @return The user typed line
     */
    @Nonnull
    public static String readLine(@Nonnull LineReader reader, @Nullable String prompt) {
        try {
            return reader.readLine(prompt);
        } catch (final EndOfFileException ignored) {
        } catch (final UserInterruptException ex) {
            System.exit(-1);
        }

        return "";
    }

    /**
     * Re-displays the console prompt if the given reader is currently reader to prevent prompt dismiss
     *
     * @param lineReader The line reader from which the prompt may be buggy
     */
    public static void tryRedisplay(@Nonnull LineReader lineReader) {
        if (!lineReader.isReading()) {
            // We cannot call widgets while the line reader is not reading
            return;
        }

        lineReader.callWidget(LineReader.REDRAW_LINE);
        lineReader.callWidget(LineReader.REDISPLAY);
    }
}
