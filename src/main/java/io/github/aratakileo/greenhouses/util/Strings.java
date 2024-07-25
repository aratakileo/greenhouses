package io.github.aratakileo.greenhouses.util;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Function;

public interface Strings {
    static @NotNull String camelToSnake(@NotNull String value) {
        if (value.isEmpty()) return value;

        return separateCamelCase(value, '_');
    }

    static @NotNull String separateCamelCase(@NotNull String value, char separator) {
        final var replacementStatement = "$1" + separator + "$2";

        return value.replaceAll("([A-Z]+)([A-Z][a-z])", replacementStatement)
                .replaceAll("([a-z])([A-Z])", replacementStatement)
                .toLowerCase();
    }

    static @NotNull String requireReturnNotAsArgument(
            @NotNull String argumentValue,
            @NotNull Function<@NotNull String, @NotNull String> processor,
            @NotNull String elseValue
    ) {
        final var functionReturn = processor.apply(argumentValue);

        return functionReturn.equals(argumentValue) ? elseValue : functionReturn;
    }

    static @NotNull String doesNotMeetCondition(
            @NotNull String ifValue,
            @NotNull Function<@NotNull String, @NotNull Boolean> condition,
            @NotNull String elseValue
    ) {
        return !condition.apply(ifValue) ? ifValue : elseValue;
    }

    static @NotNull String substring(
            @NotNull String source,
            @Range(from = 0, to = Integer.MAX_VALUE) int beginIndex,
            @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int endIndex
    ) {
        if (endIndex == beginIndex)
            return "";

        if (endIndex < 0) {
            if (endIndex <= -source.length() + beginIndex) return "";

            return source.substring(beginIndex, source.length() - endIndex);
        }

        return source.substring(beginIndex, endIndex);
    }

    static @NotNull String capitalize(@NotNull String source) {
        return StringUtils.capitalize(source);
    }

    static @NotNull String repr(@NotNull String value) {
        final var stringBuilder = new StringBuilder("\"");

        for (final var ch: value.toCharArray()) {
            if (ch == '\n') {
                stringBuilder.append("\\n");
                continue;
            }

            if (ch == '"' || ch == '\\') stringBuilder.append('\\');

            stringBuilder.append(ch);
        }

        return stringBuilder.append('"').toString();
    }
}
