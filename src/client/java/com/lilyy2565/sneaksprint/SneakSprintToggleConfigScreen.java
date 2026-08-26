package com.lilyy2565.sneaksprint;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.lilyy2565.sneaksprint.mixin.client.GameOptionsAccessor;

public class SneakSprintToggleConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        System.out.println("SneakSprintToggle: getModConfigScreenFactory() called!");

        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("SneakSprint Toggle Configuration"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            var general = builder.getOrCreateCategory(Component.literal("General"));

            // Sprint Toggle
            general.addEntry(
                entryBuilder.startBooleanToggle(Component.literal("Sprint: Toggled Mode"),
                        SneakSprintToggleClient.ToggleSprint)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("When enabled, sprint becomes toggled instead of manual (also syncs with Minecraft's setting)"))
                    .setSaveConsumer(newValue -> {
                        SneakSprintToggleClient.ToggleSprint = newValue;
                        ConfigManager.config.toggleSprint = newValue;
                    })
                    .build()
            );

            // Sneak Toggle
            general.addEntry(
                entryBuilder.startBooleanToggle(Component.literal("Sneak: Toggled Mode"),
                        SneakSprintToggleClient.ToggleSneak)
                    .setDefaultValue(false)
                    .setTooltip(Component.literal("When enabled, sneak becomes toggled instead of manual (also syncs with Minecraft's setting)"))
                    .setSaveConsumer(newValue -> {
                        SneakSprintToggleClient.ToggleSneak = newValue;
                        ConfigManager.config.toggleSneak = newValue;
                    })
                    .build()
            );

            // Additional entries can be added here.
            builder.setSavingRunnable(() -> {
                ConfigManager.saveConfig();
                // Sync with Minecraft's native settings when config is saved
                Minecraft client = Minecraft.getInstance();
                if (client.options != null) {
                    ((GameOptionsAccessor) client.options).getSprintToggled().set(SneakSprintToggleClient.ToggleSprint);
                    ((GameOptionsAccessor) client.options).getSneakToggled().set(SneakSprintToggleClient.ToggleSneak);
                }
            });
            return builder.build();
        };
    }

    // Optional helper method to open the config screen directly
    public static Screen createConfigScreen(Screen parent) {
        return new SneakSprintToggleConfigScreen().getModConfigScreenFactory().create(parent);
    }
}
