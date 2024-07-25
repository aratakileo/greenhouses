package io.github.aratakileo.greenhouses.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Classes {
    static @NotNull String getFieldView(@NotNull Field field) {
        return getFieldOrMethodView(field.getDeclaringClass(), field.getName());
    }

    static @NotNull String getMethodView(@NotNull Method method) {
        return getFieldOrMethodView(method.getDeclaringClass(), method.getName());
    }

    static @NotNull String getFieldOrMethodView(@NotNull Class<?> owner, @NotNull String fieldOrMethodName) {
        return owner.getName() + '#' + fieldOrMethodName;
    }
}
