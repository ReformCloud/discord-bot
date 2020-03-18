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
package systems.reformcloud.discord.features;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.bot.feature.BotFeature;

/**
 * Represents a feature as listener for the discord env.
 *
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public abstract class DiscordFeature extends ListenerAdapter implements BotFeature<JDA> {

    private Bot<JDA> parent;

    @Override
    public void handleStart(@NotNull Bot<JDA> bot) {
        if (this.isInitialized()) {
            throw new IllegalStateException("The feature is already initialized");
        }

        this.parent = bot;
        bot.getCurrentInstance().ifPresent(e -> e.addEventListener(this));
        System.out.println("Registered discord feature " + this.getName());
    }

    @Override
    public void handleStop() {
        if (this.isInitialized()) {
            this.getApi().getCurrentInstance().ifPresent(e -> e.removeEventListener(this));
            System.out.println("Closed discord feature " + this.getName());
        }
    }

    @Override
    public Bot<JDA> getApi() {
        return this.parent;
    }

    @Override
    public boolean isInitialized() {
        return this.parent != null;
    }

    @Override
    public boolean isApplicableTo(@NotNull Bot<?> bot) {
        return bot.getCurrentInstance().isPresent() && bot.getCurrentInstance().get() instanceof JDA;
    }
}
