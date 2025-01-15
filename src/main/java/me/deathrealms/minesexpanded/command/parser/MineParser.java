package me.deathrealms.minesexpanded.command.parser;

import me.deathrealms.minesexpanded.Mine;
import me.deathrealms.minesexpanded.MineRegistry;
import me.deathrealms.minesexpanded.MinesExpanded;
import me.deathrealms.minesexpanded.command.exception.MineParseException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

public class MineParser<C> implements ArgumentParser<C, Mine> {

    public static <C> @NotNull ParserDescriptor<C, Mine> mineParser() {
        return ParserDescriptor.of(new MineParser<>(), Mine.class);
    }

    @Override
    public @NotNull ArgumentParseResult<Mine> parse(@NotNull CommandContext<C> context, CommandInput commandInput) {
        String input = commandInput.peekString();
        MineRegistry registry = MinesExpanded.instance().mineRegistry();

        if (!registry.hasMine(input)) {
            return ArgumentParseResult.failure(new MineParseException(input));
        }

        commandInput.readString();
        return ArgumentParseResult.success(registry.getMine(input));
    }

    @Override
    public @NonNull SuggestionProvider<C> suggestionProvider() {
        return SuggestionProvider.suggestingStrings(MinesExpanded.instance().mineRegistry().getMines().keySet());
    }
}
