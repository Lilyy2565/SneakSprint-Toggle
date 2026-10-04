package com.lilyy2565.sneaksprint;

import net.minecraft.client.gui.screens.Screen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod(value = "sneaksprint_toggle", dist = Dist.CLIENT)
public class SneakSprintToggleNeoForge {

    public SneakSprintToggleNeoForge(IEventBus modBus, ModContainer modContainer) {
        SneakSprintToggleClientCommon.initKeyMappings();

        modBus.addListener(SneakSprintToggleNeoForge::registerKeyMappings);
        modBus.addListener(SneakSprintToggleNeoForge::onClientSetup);

        // Register config screen
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parent) -> SneakSprintToggleConfigScreen.createConfigScreen(parent));
    }

    private static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // Register both sneak and sprint keybinds
        event.register(SneakSprintToggleClientCommon.getToggleSprintKeyBinding());
        event.register(SneakSprintToggleClientCommon.getToggleSneakKeyBinding());
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SneakSprintToggleClientCommon::init);
    }
}