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
package systems.reformcloud.discord.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import systems.reformcloud.bot.Bot;
import systems.reformcloud.commands.source.CommandSource;
import systems.reformcloud.discord.command.BasicDiscordCommand;
import systems.reformcloud.docs.ClassResolver;
import systems.reformcloud.docs.JavaDocParserUtil;
import systems.reformcloud.docs.objects.Class;
import systems.reformcloud.docs.objects.Field;
import systems.reformcloud.docs.objects.Method;
import systems.reformcloud.util.Constants;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Pasqual Koschmieder
 * @since 1.0
 */
public final class DocsCommand extends BasicDiscordCommand {

    public DocsCommand(@NotNull Bot<JDA> parent) {
        super(parent, "!docs", new String[0], "See the docs url or load docs...");
    }

    @Override
    public void execute(@NotNull CommandSource source, @NotNull String commandLine, @NotNull String[] strings) {
        if (strings.length == 1 && strings[0].equalsIgnoreCase("help")) {
            source.sendMessage(
                    "`!docs` ....................................... Shows the online docs url\n" +
                            "`!docs <class>` ..................... Shows the information about a specific class\n" +
                            "`!docs <class>#<method>` . Shows information about a specific method in a class\n" +
                            "`!docs <class>#<field>` ... Shows information about a specific field in a class"
            );

            return;
        }

        this.parent.getCurrentInstance().ifPresent(e -> {
            var channel = e.getTextChannelById(source.getSourceChannel());
            if (channel == null) {
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(Color.BLUE)
                    .setAuthor("Docs")
                    .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()));

            if (strings.length == 0) {
                embed.setDescription("See the docs [here](" + JavaDocParserUtil.BASE_URL + ")");
                channel.sendMessage(embed.build()).queue();
                return;
            }

            if (strings.length == 1) {
                embed.setDescription("Waiting for search task to complete...");
                channel.sendMessage(embed.build()).queue(success -> {
                    List<Pair<String, String>> classes = ClassResolver.searchForClasses(strings[0], JavaDocParserUtil.BASE_URL);
                    if (classes.isEmpty()) {
                        success.delete().queue();
                        channel.sendMessage("Unable to find a result. Use a better query or use the online docs").queue();
                        return;
                    }

                    if (classes.size() == 1) {
                        Pair<String, String> aClassPair = classes.get(0);
                        Class aClass = ClassResolver.resolveClass(JavaDocParserUtil.BASE_URL, aClassPair.getRight(), aClassPair.getLeft());

                        if (aClass == null) {
                            success.delete().queue();
                            channel.sendMessage("Unable to find class " + strings[0] + "!").queue();
                            return;
                        }

                        if (strings[0].contains("#")) {
                            String[] split = strings[0].split("#");
                            if (split.length == 2) {
                                String fieldOrMethod = split[1];

                                List<Field> fields = aClass.getFields()
                                        .stream()
                                        .filter(f -> f.getName().toLowerCase().startsWith(fieldOrMethod.toLowerCase()))
                                        .collect(Collectors.toList());
                                if (!fields.isEmpty()) {
                                    var edit = formatFields(fields, aClass.getName());
                                    if (!edit.isSendable()) {
                                        success.delete().queue();
                                        channel.sendMessage("I found too many results. Use a better query or use the online docs").queue();
                                        return;
                                    }

                                    success.editMessage(edit).queue();
                                    return;
                                }

                                List<Method> collect = aClass.getMethods()
                                        .stream()
                                        .filter(m -> m.getName().toLowerCase().startsWith(fieldOrMethod.toLowerCase()))
                                        .collect(Collectors.toList());
                                if (!collect.isEmpty()) {
                                    MessageEmbed edit;
                                    if (collect.size() == 1) {
                                        edit = formatMethod(collect.get(0), aClass.getName());
                                    } else {
                                        edit = formatMethods(collect, aClass.getName());
                                    }

                                    if (!edit.isSendable()) {
                                        success.delete().queue();
                                        channel.sendMessage("I found too many results. Use a better query or use the online docs").queue();
                                        return;
                                    }

                                    success.editMessage(edit).queue();
                                    return;
                                }

                                success.delete().queue();
                                channel.sendMessage("Unable to find any field or method which equals to your search query").queue();
                            } else {
                                var edit = formatClass(aClass);
                                if (!edit.isSendable()) {
                                    success.delete().queue();
                                    channel.sendMessage("I found too many results. Use a better query or use the online docs").queue();
                                    return;
                                }

                                success.editMessage(edit).queue();
                            }
                        } else {
                            var edit = formatClass(aClass);
                            if (!edit.isSendable()) {
                                success.delete().queue();
                                channel.sendMessage("I found too many results. Use a better query or use the online docs").queue();
                                return;
                            }

                            success.editMessage(edit).queue();
                        }
                    } else {
                        var edit = formatAll(classes);
                        if (!edit.isSendable()) {
                            success.delete().queue();
                            channel.sendMessage("I found too many results. Use a better query or use the online docs").queue();
                            return;
                        }

                        success.editMessage(edit).queue();
                    }
                }, error -> {
                });
            }
        });
    }

    @NotNull
    private static MessageEmbed formatAll(@NotNull List<Pair<String, String>> classes) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor("Docs")
                .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()))
                .setDescription("I found the following:");
        for (Pair<String, String> aClass : classes) {
            embed.addField("", "[" + aClass.getLeft() + "](" + aClass.getRight() + ")", true);
        }

        return embed.build();
    }

    @NotNull
    private static MessageEmbed formatClass(@NotNull Class aClass) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor("Docs")
                .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()))
                .setTitle(aClass.getType() + ": " + aClass.getName(), aClass.getUrl());
        for (Field field : aClass.getFields()) {
            builder.addField(
                    field.getReturnType(),
                    "[" + aClass.getName() + "#" + field.getName() + "](" + field.getUrl() + ")",
                    true
            );
        }

        for (Method method : aClass.getMethods()) {
            builder.addField(
                    (method.isStaticMethod() ? "static " : "") + method.getName(),
                    method.getReturnType(),
                    true
            );
        }

        return builder.build();
    }

    @NotNull
    private static MessageEmbed formatFields(@NotNull Collection<Field> fields, @NotNull String className) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor("Docs")
                .setTitle("Fields in class " + className + " for search query")
                .setDescription("Found total " + fields.size() + " fields")
                .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()));
        for (Field field : fields) {
            builder.addField(field.getUrl(), field.getReturnType(), true);
        }

        return builder.build();
    }

    @NotNull
    private static MessageEmbed formatMethod(@NotNull Method method, @NotNull String className) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor("Docs")
                .setTitle(String.join(", ", method.getAnnotations()) + " " + className + " : " + method.getName(), method.getUrl())
                .setDescription(method.getReturnValue());
        for (Map.Entry<String, Pair<String, Collection<String>>> stringPairEntry : method.getParameters().entrySet()) {
            builder.addField(
                    "parameter: " + stringPairEntry.getKey() + " : " + stringPairEntry.getValue().getLeft(),
                    String.join(", ", stringPairEntry.getValue().getRight()),
                    false
            );
        }

        for (String s : method.getThrowable()) {
            builder.addField(
                    "throws: " + s,
                    "",
                    false
            );
        }

        return builder.build();
    }

    @NotNull
    private static MessageEmbed formatMethods(@NotNull Collection<Method> methods, @NotNull String className) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setAuthor("Docs")
                .setTitle("Methods in class " + className + " for search query")
                .setDescription("Found total " + methods.size() + " methods")
                .setFooter(Constants.DATE_FORMAT.format(System.currentTimeMillis()));
        for (Method method : methods) {
            builder.addField(
                    (method.isStaticMethod() ? "static " : "") + method.getReturnType() + " " + method.getName(),
                    method.getUrl(), true
            );
        }

        return builder.build();
    }
}
