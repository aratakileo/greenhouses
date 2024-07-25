package io.github.aratakileo.greenhouses.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;

public final class SoundUtil {
    public static void playGuiSound(@NotNull SoundEvent sound) {
        playGuiSound(sound, 1);
    }

    public static void playGuiSound(@NotNull SoundEvent sound, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, volume));
    }
}
