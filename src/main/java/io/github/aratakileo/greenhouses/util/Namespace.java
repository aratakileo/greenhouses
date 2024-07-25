package io.github.aratakileo.greenhouses.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Namespace {
    public final static Namespace MINECRAFT = new Namespace("minecraft");

    private final String namespace;

    private Namespace(@NotNull String namespace) {
        this.namespace = namespace;
    }

    public @NotNull ResourceLocation getIdentifier(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public @NotNull Logger getLogger() {
        return LoggerFactory.getLogger(namespace);
    }

    public @NotNull String asString() {
        return namespace;
    }

    @Override
    public @NotNull String toString() {
        return "%s:".formatted(namespace);
    }

    public static @NotNull Namespace of(@NotNull String namespace) {
        return namespace.equals("minecraft") ? MINECRAFT : new Namespace(namespace);
    }
}
