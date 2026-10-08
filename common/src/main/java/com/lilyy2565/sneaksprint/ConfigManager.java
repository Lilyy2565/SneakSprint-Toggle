package com.lilyy2565.sneaksprint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    // Get the per-platform confifg directory
    private static final File CONFIG_FILE = Platform.getConfigFolder().resolve("SneakSprintToggle.json").toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static SneakSprintToggleConfig config = new SneakSprintToggleConfig();

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, SneakSprintToggleConfig.class);
                System.out.println("Config loaded: Sprint=" + config.toggleSprint + ", Sneak=" + config.toggleSneak);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No config file found. Saving default config.");
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
            System.out.println("Config saved: Sprint=" + config.toggleSprint + ", Sneak=" + config.toggleSneak);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
