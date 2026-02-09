package com.lilyy2565.sneaksprint;

import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SneakSprintToggleDebugEntry implements DebugHudEntry {

    @Override
    public void render(DebugHudLines lines, @Nullable World world, @Nullable WorldChunk worldChunk, @Nullable WorldChunk worldChunk2) {
        Identifier sectionId = Identifier.of("sneaksprint-toggle", "debug");
        List<String> debugInfo = SneakSprintToggleClient.getDebugInfo();
        lines.addLinesToSection(sectionId, debugInfo);
    }
}
