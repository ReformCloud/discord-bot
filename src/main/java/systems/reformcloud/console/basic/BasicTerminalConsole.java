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
package systems.reformcloud.console.basic;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.console.TerminalConsole;
import systems.reformcloud.console.events.ConsoleLogMessageSentEvent;
import systems.reformcloud.console.formatter.basic.BasicLogFormatter;
import systems.reformcloud.console.handler.AbstractHandler;
import systems.reformcloud.console.handler.basic.BasicHandler;
import systems.reformcloud.console.stream.BasicLoggingOutputStream;
import systems.reformcloud.console.util.TerminalUtil;
import systems.reformcloud.util.FileUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Level;

/**
 * Represents a basic terminal console implementation.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public class BasicTerminalConsole extends TerminalConsole {

    public BasicTerminalConsole() throws IOException {
        Terminal terminal = TerminalUtil.createNewTerminal();
        this.lineReader = TerminalUtil.newLineReader(terminal);

        FileUtils.createDirectories(Paths.get("logs"));

        FileHandler fileHandler = new FileHandler("logs/cloud.log", 70000000, 8, true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new BasicLogFormatter(this));
        addHandler(fileHandler);

        AbstractHandler consoleHandler = new BasicHandler(this);
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(new BasicLogFormatter(this));
        addHandler(consoleHandler);

        System.setOut(new PrintStream(new BasicLoggingOutputStream(this, Level.INFO), true));
        System.setErr(new PrintStream(new BasicLoggingOutputStream(this, Level.SEVERE), true));
    }

    private final LineReader lineReader;

    @Override
    public @NotNull
    LineReader getLineReader() {
        return this.lineReader;
    }

    @Override
    public @NotNull
    String readLine(@Nullable String prompt) {
        return TerminalUtil.readLine(this.lineReader, prompt);
    }

    @Override
    public void log(@NotNull String message) {
        ConsoleLogMessageSentEvent event = new ConsoleLogMessageSentEvent(message);
        GlobalAPI.getEventManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        message = event.getLogMessage();

        this.lineReader.getTerminal().puts(InfoCmp.Capability.carriage_return);
        this.lineReader.getTerminal().writer().print(message);
        this.lineReader.getTerminal().flush();

        TerminalUtil.tryRedisplay(this.lineReader);
    }

    @Override
    public void clearScreen() {
        this.lineReader.getTerminal().puts(InfoCmp.Capability.clear_screen);
        this.lineReader.getTerminal().flush();
    }
}
