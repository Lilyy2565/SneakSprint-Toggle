package com.soniczac7.sneaksprint;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import com.soniczac7.sneaksprint.mixin.client.GameOptionsAccessor;

public class SneakSprintToggleConfigScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        System.out.println("SneakSprintToggle: getModConfigScreenFactory() called!");

        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("SneakSprint Toggle Configuration"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            var general = builder.getOrCreateCategory(Text.literal("General"));

            // Sprint Toggle
            general.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Sprint: Toggled Mode"),
                        SneakSprintToggleClient.ToggleSprint)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("When enabled, sprint becomes toggled instead of manual (also syncs with Minecraft's setting)"))
                    .setSaveConsumer(newValue -> {
                        SneakSprintToggleClient.ToggleSprint = newValue;
                        ConfigManager.config.toggleSprint = newValue;
                    })
                    .build()
            );

            // Sneak Toggle
            general.addEntry(
                entryBuilder.startBooleanToggle(Text.literal("Sneak: Toggled Mode"),
                        SneakSprintToggleClient.ToggleSneak)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("When enabled, sneak becomes toggled instead of manual (also syncs with Minecraft's setting)"))
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
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.options != null) {
                    ((GameOptionsAccessor) client.options).getSprintToggled().setValue(SneakSprintToggleClient.ToggleSprint);
                    ((GameOptionsAccessor) client.options).getSneakToggled().setValue(SneakSprintToggleClient.ToggleSneak);
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
