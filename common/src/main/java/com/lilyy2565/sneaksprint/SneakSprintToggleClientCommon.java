package com.lilyy2565.sneaksprint;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
//? if <26.3
//import org.lwjgl.glfw.GLFW;

import com.lilyy2565.sneaksprint.mixin.client.GameOptionsAccessor;


public class SneakSprintToggleClientCommon {
	
    // Keybindings
    private static KeyMapping toggleSprintKeyBinding;
    private static KeyMapping toggleSneakKeyBinding;

    public static void initKeyMappings() {
        toggleSprintKeyBinding = new KeyMapping(
            "Toggle Sprint",     // Keybind name
            //? if >=26.3 {
            InputConstants.Type.KEYBOARD,
            //?} else
            //InputConstants.Type.KEYSYM,
            //? if >=26.3 {
            InputConstants.KEY_NUMPAD7,  // Default key: Numpad 7
            //?} else
            //GLFW.GLFW_KEY_KP_7,  // Default key: Numpad 7
            KeyMapping.Category.MOVEMENT // Category
        );

        toggleSneakKeyBinding = new KeyMapping(
            "Toggle Sneak",      // Keybind name
            //? if >=26.3 {
            InputConstants.Type.KEYBOARD,
            //?} else
            //InputConstants.Type.KEYSYM,
            //? if >=26.3 {
            InputConstants.KEY_NUMPAD8,  // Default key: KP_8 (Numpad 8)
            //?} else
            //GLFW.GLFW_KEY_KP_8,  // Default key: KP_8 (Numpad 8)
            KeyMapping.Category.MOVEMENT // Category
        );
    }

    public static KeyMapping getToggleSprintKeyBinding() {
        return toggleSprintKeyBinding;
    }

    public static KeyMapping getToggleSneakKeyBinding() {
        return toggleSneakKeyBinding;
    }

	public static void init() {
		// Load the configuration.
        ConfigManager.loadConfig();

        // Sync with Minecraft's native settings on startup - read current MC settings first
        syncFromMinecraftSettings();
        syncSprintToggle();
        syncSneakToggle();

        // Register a client tick event.
        ClientTickEvent.CLIENT_POST.register(client -> {
            // Check for manual setting changes in Minecraft's controls
            checkForManualSettingChanges();
            
            // Check if the key was pressed
            while (toggleSprintKeyBinding.consumeClick()) {
                ConfigManager.config.toggleSprint = !ConfigManager.config.toggleSprint;
                syncSprintToggle();
                ConfigManager.saveConfig();
                Minecraft.getInstance().gui
                    //? if >=26.2
                    .hud
                    .setOverlayMessage(
                        Component.literal("Sprint Toggle: " + (ConfigManager.config.toggleSprint ? "Toggled" : "Manual")),
                        true // 'true' makes it display in the action bar
                    );
            }

            while (toggleSneakKeyBinding.consumeClick()) {
                ConfigManager.config.toggleSneak = !ConfigManager.config.toggleSneak;
                syncSneakToggle();
                ConfigManager.saveConfig();
                Minecraft.getInstance().gui
                    //? if >=26.2
                    .hud
                    .setOverlayMessage(
                        Component.literal("Sneak Toggle: " + (ConfigManager.config.toggleSneak ? "Toggled" : "Manual")),
                        true // 'true' makes it display in the action bar
                );
            }
        });
	}

    // Method to read current Minecraft settings on startup
    private static void syncFromMinecraftSettings() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) {
            // Read current Minecraft settings and update our mod state
            boolean mcSprintToggled = ((GameOptionsAccessor) client.options).getSprintToggled().get();
            boolean mcSneakToggled = ((GameOptionsAccessor) client.options).getSneakToggled().get();
            
            // If Minecraft settings don't match our config, update our config to match Minecraft
            if (ConfigManager.config.toggleSprint != mcSprintToggled) {
                ConfigManager.config.toggleSprint = mcSprintToggled;
                ConfigManager.saveConfig();
                System.out.println("SneakSprint: Synced sprint setting from Minecraft: " + ConfigManager.config.toggleSprint);
            }
            
            if (ConfigManager.config.toggleSneak != mcSneakToggled) {
                ConfigManager.config.toggleSneak = mcSneakToggled;
                ConfigManager.saveConfig();
                System.out.println("SneakSprint: Synced sneak setting from Minecraft: " + ConfigManager.config.toggleSneak);
            }
        }
    }

    // Method to check for manual changes to Minecraft settings
    private static void checkForManualSettingChanges() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) {
            boolean mcSprintToggled = ((GameOptionsAccessor) client.options).getSprintToggled().get();
            boolean mcSneakToggled = ((GameOptionsAccessor) client.options).getSneakToggled().get();
            
            // If Minecraft settings changed without us knowing, update our mod state
            if (ConfigManager.config.toggleSprint != mcSprintToggled) {
                ConfigManager.config.toggleSprint = mcSprintToggled;
                ConfigManager.saveConfig();
                // Show feedback message
                Minecraft.getInstance().gui
                    //? if >=26.2
                    .hud
                    .setOverlayMessage(
                        Component.literal("Sprint Toggle: " + (ConfigManager.config.toggleSprint ? "Toggled" : "Manual") + " (synced from controls)"),
                        true // 'true' makes it display in the action bar
                    );
            }
            
            if (ConfigManager.config.toggleSneak != mcSneakToggled) {
                ConfigManager.config.toggleSneak = mcSneakToggled;
                ConfigManager.saveConfig();
                // Show feedback message
                Minecraft.getInstance().gui
                    //? if >=26.2
                    .hud
                    .setOverlayMessage(
                        Component.literal("Sneak Toggle: " + (ConfigManager.config.toggleSneak ? "Toggled" : "Manual") + " (synced from controls)"),
                        true // 'true' makes it display in the action bar
                    );
            }
        }
    }

    // Method to sync sprint toggle with Minecraft's native setting
    private static void syncSprintToggle() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) {
            boolean wasToggled = ((GameOptionsAccessor) client.options).getSprintToggled().get();
            ((GameOptionsAccessor) client.options).getSprintToggled().set(ConfigManager.config.toggleSprint);
            
            // If we're switching from toggled to manual and player was sprinting,
            // simulate a key press to properly reset the toggle state
            if (wasToggled && !ConfigManager.config.toggleSprint && client.player != null && client.player.isSprinting()) {
                // For sprinting, we need to both simulate key press AND ensure player stops sprinting
                if (client.options.keySprint != null) {
                    // First set sprinting to false to ensure it stops
                    client.player.setSprinting(false);
                    // Then simulate key press to clear any toggle state
                    client.options.keySprint.setDown(true);
                    client.options.keySprint.setDown(false);
                }
            }
        }
    }

    // Method to sync sneak toggle with Minecraft's native setting
    private static void syncSneakToggle() {
        Minecraft client = Minecraft.getInstance();
        if (client.options != null) {
            boolean wasToggled = ((GameOptionsAccessor) client.options).getSneakToggled().get();
            ((GameOptionsAccessor) client.options).getSneakToggled().set(ConfigManager.config.toggleSneak);
            
            // If we're switching from toggled to manual and player was sneaking,
            // simulate a key press to properly reset the toggle state
            if (wasToggled && !ConfigManager.config.toggleSneak && client.player != null && client.player.isCrouching()) {
                // For sneaking, we need to both simulate key press AND ensure player stops sneaking
                if (client.options.keyShift != null) {
                    // First set sneaking to false to ensure it stops
                    // Then simulate key press to clear any toggle state
                    client.options.keyShift.setDown(true);
                    client.options.keyShift.setDown(false);
                }
            }
        }
    }
}
