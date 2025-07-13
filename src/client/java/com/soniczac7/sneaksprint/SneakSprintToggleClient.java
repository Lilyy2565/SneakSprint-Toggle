package com.soniczac7.sneaksprint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import com.soniczac7.sneaksprint.mixin.client.GameOptionsAccessor;
import net.minecraft.util.Identifier;


public class SneakSprintToggleClient implements ClientModInitializer {
	
    public static boolean ToggleSprint = false;
	public static boolean ToggleSneak = false;

    // Keybindings
    private KeyBinding toggleSprintKeyBinding;
    private KeyBinding toggleSneakKeyBinding;

    // Define an identifier for your custom HUD layer.
    private static final Identifier DEBUG_LAYER = Identifier.of("sneaksprint", "debug");
	
	@Override
	public void onInitializeClient() {
		// Load the configuration.
        ConfigManager.loadConfig();
        ToggleSprint = ConfigManager.config.toggleSprint;
		ToggleSneak = ConfigManager.config.toggleSneak;

        // Sync with Minecraft's native settings on startup
        syncSprintToggle();
        syncSneakToggle();

        toggleSprintKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Sprint",     // Keybind name
            GLFW.GLFW_KEY_KP_7,  // Default key: KP_7 (Numpad 7)
            "SneakSprint Toggle" // Category
        ));

        toggleSneakKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle Sneak",      // Keybind name
            GLFW.GLFW_KEY_KP_8,  // Default key: KP_8 (Numpad 8)
            "SneakSprint Toggle" // Category
        ));

        // Register a client tick event.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Check if the key was pressed
            while (toggleSprintKeyBinding.wasPressed()) {
                ToggleSprint = !ToggleSprint;
                syncSprintToggle();
                ConfigManager.config.toggleSprint = ToggleSprint;
                ConfigManager.saveConfig();
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(
                    Text.literal("Sprint Toggle: " + (ToggleSprint ? "Toggled" : "Manual")),
                    true // 'true' makes it display in the action bar
                );
            }

            while (toggleSneakKeyBinding.wasPressed()) {
                ToggleSneak = !ToggleSneak;
                syncSneakToggle();
                ConfigManager.config.toggleSneak = ToggleSneak;
                ConfigManager.saveConfig();
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(
                    Text.literal("Sneak Toggle: " + (ToggleSneak ? "Toggled" : "Manual")),
                    true // 'true' makes it display in the action bar
                );
            }
        });
	}

    // Method to sync sprint toggle with Minecraft's native setting
    private static void syncSprintToggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options != null) {
            boolean wasToggled = ((GameOptionsAccessor) client.options).getSprintToggled().getValue();
            ((GameOptionsAccessor) client.options).getSprintToggled().setValue(ToggleSprint);
            
            // If we're switching from toggled to manual and player was sprinting,
            // simulate a key press to properly reset the toggle state
            if (wasToggled && !ToggleSprint && client.player != null && client.player.isSprinting()) {
                // For sprinting, we need to both simulate key press AND ensure player stops sprinting
                if (client.options.sprintKey != null) {
                    // First set sprinting to false to ensure it stops
                    client.player.setSprinting(false);
                    // Then simulate key press to clear any toggle state
                    client.options.sprintKey.setPressed(true);
                    client.options.sprintKey.setPressed(false);
                }
            }
        }
    }

    // Method to sync sneak toggle with Minecraft's native setting
    private static void syncSneakToggle() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options != null) {
            boolean wasToggled = ((GameOptionsAccessor) client.options).getSneakToggled().getValue();
            ((GameOptionsAccessor) client.options).getSneakToggled().setValue(ToggleSneak);
            
            // If we're switching from toggled to manual and player was sneaking,
            // simulate a key press to properly reset the toggle state
            if (wasToggled && !ToggleSneak && client.player != null && client.player.isSneaking()) {
                // For sneaking, we need to both simulate key press AND ensure player stops sneaking
                if (client.options.sneakKey != null) {
                    // First set sneaking to false to ensure it stops
                    client.player.setSneaking(false);
                    // Then simulate key press to clear any toggle state
                    client.options.sneakKey.setPressed(true);
                    client.options.sneakKey.setPressed(false);
                }
            }
        }
    }

    // Static method to get debug info for F3 screen (called by mixin)
    public static java.util.List<String> getDebugInfo() {
        java.util.List<String> info = new java.util.ArrayList<>();
        info.add("");
        info.add("§6[SneakSprint Toggle]");
        info.add("Sprint: " + (ToggleSprint ? "§aToggled" : "§cManual"));
        info.add("Sneak: " + (ToggleSneak ? "§aToggled" : "§cManual"));
        return info;
    }
}