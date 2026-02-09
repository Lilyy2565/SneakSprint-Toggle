package com.lilyy2565.sneaksprint.mixin.client;

import com.lilyy2565.sneaksprint.SneakSprintToggleDebugEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryVisibility;
import net.minecraft.client.gui.hud.debug.DebugProfileType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(DebugHudEntries.class)
public abstract class DebugHudEntriesMixin {

    @Shadow
    @Final
    @Mutable
    private static Map<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> PROFILES;

    @Shadow
    public static Identifier register(Identifier id, DebugHudEntry entry) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void registerSneakSprintToggleDebug(CallbackInfo ci) {
        Identifier entryId = Identifier.of("sneaksprint-toggle", "debug");
        register(entryId, new SneakSprintToggleDebugEntry());

        Map<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> updatedProfiles = new HashMap<>();
        for (Map.Entry<DebugProfileType, Map<Identifier, DebugHudEntryVisibility>> profileEntry : PROFILES.entrySet()) {
            Map<Identifier, DebugHudEntryVisibility> visibilityMap = new HashMap<>(profileEntry.getValue());
            visibilityMap.putIfAbsent(entryId, DebugHudEntryVisibility.IN_F3);
            updatedProfiles.put(profileEntry.getKey(), visibilityMap);
        }
        PROFILES = updatedProfiles;
    }
}
