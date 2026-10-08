package com.lilyy2565.sneaksprint;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class SneakSprintToggleDebugEntry implements DebugScreenEntry {
    public static final Identifier ENTRY_ID = Identifier.fromNamespaceAndPath("sneaksprint", "toggle_status");

    @Override
    public void display(DebugScreenDisplayer displayer, Level level, LevelChunk chunk, LevelChunk chunk2) {
        List<String> info = new ArrayList<>();
        info.add("§6[SneakSprint Toggle]");
        info.add("Sneak: " + (ConfigManager.config.toggleSneak ? "§aToggled" : "§cManual"));
        info.add("Sprint: " + (ConfigManager.config.toggleSprint ? "§aToggled" : "§cManual"));

        displayer.addToGroup(ENTRY_ID, info);
    }

    @Override
    public boolean isAllowed(boolean reducedDebugInfo) {
        return true;
    }
}