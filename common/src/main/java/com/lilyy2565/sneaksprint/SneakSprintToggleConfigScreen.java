package com.lilyy2565.sneaksprint;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import com.lilyy2565.sneaksprint.mixin.client.GameOptionsAccessor;

public class SneakSprintToggleConfigScreen {

    public static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.literal("SneakSprint Toggle Configuration"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        var general = builder.getOrCreateCategory(Component.literal("General"));

        // Sprint Toggle
        general.addEntry(
                entryBuilder.startBooleanToggle(Component.literal("Sprint: Toggled Mode"),ConfigManager.config.toggleSprint)
                .setDefaultValue(false)
                .setTooltip(Component.literal("When enabled, sprint becomes toggled instead of manual (also syncs with Minecraft's setting)"))
                .setSaveConsumer(newValue -> { ConfigManager.config.toggleSprint = newValue; })
                .build()
        );

        // Sneak Toggle
        general.addEntry(
                entryBuilder.startBooleanToggle(Component.literal("Sneak: Toggled Mode"), ConfigManager.config.toggleSneak)
                .setDefaultValue(false)
                .setTooltip(Component.literal("When enabled, sneak becomes toggled instead of manual (also syncs with Minecraft's setting)"))
                .setSaveConsumer(newValue -> { ConfigManager.config.toggleSneak = newValue; })
                .build()
        );

        builder.setSavingRunnable(() -> {
            ConfigManager.saveConfig();

            // Sync with Minecraft's native settings when config is saved
            Minecraft client = Minecraft.getInstance();

            if (client.options != null) {
                ((GameOptionsAccessor) client.options).getSprintToggled().set(ConfigManager.config.toggleSprint);

                ((GameOptionsAccessor) client.options).getSneakToggled().set(ConfigManager.config.toggleSneak);
            }
        });

        return builder.build();
    }
}