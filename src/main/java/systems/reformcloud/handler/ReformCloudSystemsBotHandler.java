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
package systems.reformcloud.handler;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.api.GlobalAPI;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.CommandMap;
import systems.reformcloud.commands.basic.BasicCommandMap;
import systems.reformcloud.commands.basic.commands.HelpCommand;
import systems.reformcloud.commands.basic.commands.StopCommand;
import systems.reformcloud.database.DatabaseDriver;
import systems.reformcloud.database.basic.H2DatabaseConfig;
import systems.reformcloud.database.basic.H2DatabaseDriver;
import systems.reformcloud.discord.DiscordBot;
import systems.reformcloud.discord.DiscordConnectionHandler;
import systems.reformcloud.events.EventManager;
import systems.reformcloud.events.basic.BasicEventManager;
import systems.reformcloud.user.punish.util.PunishmentsDeleter;
import systems.reformcloud.util.VersionChecker;

/**
 * The main class handler for the reformcloud bot. Handles all database connections and console stuff.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class ReformCloudSystemsBotHandler {

    private final EventManager eventManager = new BasicEventManager();
    private final CommandMap commandMap = new BasicCommandMap();
    private final PunishmentsDeleter punishmentsDeleter;
    private final DatabaseDriver databaseDriver;
    private final Bot<JDA> discordBot;

    public ReformCloudSystemsBotHandler() {
        GlobalAPI.setParent(this);

        VersionChecker.init();

        this.commandMap.registerCommand(new HelpCommand());
        this.commandMap.registerCommand(new StopCommand());

        this.databaseDriver = new H2DatabaseDriver();
        this.databaseDriver.connect(new H2DatabaseConfig());

        this.discordBot = new DiscordBot();
        this.discordBot.doConnect(new DiscordConnectionHandler());

        this.punishmentsDeleter = new PunishmentsDeleter();
        this.punishmentsDeleter.setDaemon(true);
        this.punishmentsDeleter.start();
    }

    /**
     * @return The database driver which is currently active
     */
    @NotNull
    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }

    /**
     * @return The currently running command map
     */
    @NotNull
    public CommandMap getCommandMap() {
        return commandMap;
    }

    /**
     * @return The current event manager which is used
     */
    @NotNull
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Closes the current handler and all driver which must get closed
     */
    public void close() {
        if (this.discordBot != null) {
            this.discordBot.shutdownNow();
        }

        this.eventManager.unregisterAll();
        this.punishmentsDeleter.interrupt();

        if (this.databaseDriver != null) {
            this.databaseDriver.close();
        }
    }
}
