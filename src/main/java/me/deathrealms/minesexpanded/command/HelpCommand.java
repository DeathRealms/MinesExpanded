package me.deathrealms.minesexpanded.command;

import me.deathrealms.minesexpanded.MinesExpanded;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.component.DefaultValue;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.stream.Collectors;

public class HelpCommand implements MECommand {

    @Override
    public void registerCommand(MinesExpanded plugin, BukkitCommandManager<CommandSender> manager) {
        manager.command(
                manager.commandBuilder("mines")
                        .literal("help")
                        .commandDescription(Description.of("View all commands for the plugin."))
                        .optional("query", StringParser.stringComponent(StringParser.StringMode.GREEDY)
                                .defaultValue(DefaultValue.constant(""))
                                .suggestionProvider(SuggestionProvider.blocking((context, input) -> manager.createHelpHandler()
                                        .queryRootIndex((CommandSender) context.sender())
                                        .entries()
                                        .stream()
                                        .map(commandEntry -> commandEntry.syntax().replace("mines", "").trim())
                                        .map(Suggestion::suggestion)
                                        .collect(Collectors.toList()))))
                        .handler(context -> {
                            String query = context.getOrDefault("query", "").trim();
                            if (!query.isEmpty() && !query.startsWith("mines")) {
                                query = "mines " + query;
                            }
                            plugin.minecraftHelp().queryCommands(query, context.sender());
                        })
        );
    }
}
