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
package systems.reformcloud.commands;

import org.jetbrains.annotations.NotNull;
import systems.reformcloud.commands.source.CommandSource;

import java.util.Collection;

/**
 * Represents a command map which holds all commands and dispatches them when a user typed a command
 * into the console.
 * <p>
 * If you want to register a command you may simply use:
 * <pre>{@code
 * public static void main(String... args) {
 *     Command command = ...; // create your command here
 *     CommandMap#registerCommand(command);
 * }
 * }</pre>
 * If you now type a command line in the console starting with the command name or any alias of it
 * the command will get executed by the command map.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public interface CommandMap {

    /**
     * Registers a command in the command map
     *
     * @param command The command which should get registered
     */
    void registerCommand(@NotNull Command<Object> command);

    /**
     * Unregisters a command from the command map
     *
     * @param commandName The name of the command which should get unregistered
     */
    void unregisterCommand(@NotNull String commandName);

    /**
     * Dispatches a specific command
     *
     * @param source      The command source from which the command gets called
     * @param commandLine The command line which was typed by the user into the console
     * @return If the map contained any command which matches to the command line
     */
    boolean dispatchCommand(@NotNull CommandSource source, @NotNull String commandLine);

    /**
     * Get all currently registered commands
     *
     * @return All registered commands
     */
    @NotNull
    Collection<Command<Object>> getRegisteredCommands();
}
