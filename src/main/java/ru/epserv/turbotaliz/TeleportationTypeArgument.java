package ru.epserv.turbotaliz;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.bukkit.BukkitCaptionKeys;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.function.BiFunction;

public class TeleportationTypeArgument<C> extends CommandArgument<C, RandomTeleportCommand.TeleportationType> {

    private TeleportationTypeArgument(final boolean required, final @NonNull String name, final @NonNull String defaultValue, final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider, final @NonNull ArgumentDescription defaultDescription) {
        super(required, name, new TeleportationTypeArgument.TeleportationTypeParser<>(), defaultValue, RandomTeleportCommand.TeleportationType.class, suggestionsProvider, defaultDescription);
    }

    public static <C> TeleportationTypeArgument.@NonNull Builder<C> builder(final @NonNull String name) {
        return new TeleportationTypeArgument.Builder<>(name);
    }

    public static <C> @NonNull CommandArgument<C, RandomTeleportCommand.TeleportationType> of(final @NonNull String name) {
        return TeleportationTypeArgument.<C>builder(name).asRequired().build();
    }

    public static <C> @NonNull CommandArgument<C, RandomTeleportCommand.TeleportationType> optional(final @NonNull String name) {
        return TeleportationTypeArgument.<C>builder(name).asOptional().build();
    }

    public static final class TeleportationTypeParser<C> implements ArgumentParser<C, RandomTeleportCommand.TeleportationType> {

        public @NonNull ArgumentParseResult<RandomTeleportCommand.TeleportationType> parse(final @NonNull CommandContext<C> commandContext, final @NonNull Queue<@NonNull String> inputQueue) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(PlayerArgument.PlayerParser.class, commandContext));
            } else {
                RandomTeleportCommand.TeleportationType type = null;
                try {
                    input = input.toLowerCase() + "_origin";
                    type = RandomTeleportCommand.TeleportationType.valueOf(input.toUpperCase());
                } catch (Exception ignored) {}
                if (type == null) {
                    return ArgumentParseResult.failure(new TeleportationTypeArgument.TeleportationTypeParseException(input, commandContext));
                } else {
                    inputQueue.remove();
                    return ArgumentParseResult.success(type);
                }
            }
        }

        public @NonNull List<@NonNull String> suggestions(final @NonNull CommandContext<C> commandContext, final @NonNull String input) {
            List<String> output = new ArrayList<>();
            List<RandomTeleportCommand.TeleportationType> var4 = Arrays.stream(RandomTeleportCommand.TeleportationType.values()).toList();
            var4.forEach(t -> output.add(t.name().toLowerCase().replace("_origin", "")));
            return output;
        }
    }

    public static final class Builder<C> extends CommandArgument.Builder<C, RandomTeleportCommand.TeleportationType> {
        private Builder(final @NonNull String name) {
            super(RandomTeleportCommand.TeleportationType.class, name);
        }

        public @NonNull TeleportationTypeArgument<C> build() {
            return new TeleportationTypeArgument<>(this.isRequired(), this.getName(), this.getDefaultValue(), this.getSuggestionsProvider(), this.getDefaultDescription());
        }
    }

    public static final class TeleportationTypeParseException extends ParserException {
        private final String input;

        public TeleportationTypeParseException(final @NonNull String input, final @NonNull CommandContext<?> context) {
            super(TeleportationTypeArgument.TeleportationTypeParser.class, context, BukkitCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, CaptionVariable.of("input", input));
            this.input = input;
        }

        public @NonNull String getInput() {
            return this.input;
        }
    }
}

