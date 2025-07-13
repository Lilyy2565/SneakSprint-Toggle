package com.soniczac7.sneaksprint;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.Identifier;
import com.soniczac7.sneaksprint.mixin.client.GameOptionsAccessor;


import net.fabricmc.api.ClientModInitializer;

public class SneakSprintToggleClient implements ClientModInitializer {

	public static boolean ToggleSprint = false;
	public static boolean ToggleSneak = false;
    
    public static boolean textEnabled = true;

    // Keybindings
    private KeyBinding toggleSprintKeyBinding;
    private KeyBinding toggleSneakKeyBinding;
    private KeyBinding toggleTextKeyBinding;

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
                // Simulate pressing and releasing the sneak key to clear the toggle state
                if (client.options.sneakKey != null) {
                    // Trigger the key binding action to properly reset toggle state
                    client.options.sneakKey.setPressed(true);
                    client.options.sneakKey.setPressed(false);
                }
            }
        }
    }

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		// Load the configuration.
        ConfigManager.loadConfig();
        ToggleSprint = ConfigManager.config.toggleSprint;
		ToggleSneak = ConfigManager.config.toggleSneak;
        textEnabled = ConfigManager.config.textEnabled;

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

        toggleTextKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Toggle SneakSprint HUD", // Keybind name
            GLFW.GLFW_KEY_KP_9,       // Default key: KP_9 (Numpad 9)
            "SneakSprint Toggle"      // Category
        ));

		// Register a HUD render callback.
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            renderHud(drawContext);
        });

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

            while (toggleTextKeyBinding.wasPressed()) {
                textEnabled = !textEnabled;
                MinecraftClient.getInstance().inGameHud.setOverlayMessage(
                    Text.literal("SneakSprint HUD: " + (textEnabled ? "Enabled" : "Disabled")),
                    true // 'true' makes it display in the action bar
                );
            }
        });
	}

	// This method will be called to render your debug info.
    private static void renderHud(DrawContext drawContext) {
        if(textEnabled){
            MinecraftClient client = MinecraftClient.getInstance();
            int x = 10;
            int y = 10;
            int lineHeight = client.textRenderer.fontHeight + 2; // Add some spacing between lines
            
            // Draw each line separately since drawText doesn't handle \n properly
            drawContext.drawText(client.textRenderer, 
                String.format("Sneak Toggle: %s", ToggleSneak ? "Toggled" : "Manual"), 
                x, y, 0xffffff, true);
            drawContext.drawText(client.textRenderer, 
                String.format("Sprint Toggle: %s", ToggleSprint ? "Toggled" : "Manual"), 
                x, y + lineHeight, 0xffffff, true);
        }
    }
}