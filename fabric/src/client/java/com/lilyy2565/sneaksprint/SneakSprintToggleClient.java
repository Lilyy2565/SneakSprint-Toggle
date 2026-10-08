package com.lilyy2565.sneaksprint;

import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.fabricmc.api.ClientModInitializer;

public class SneakSprintToggleClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SneakSprintToggleClientCommon.initKeyMappings();
        KeyMappingRegistry.register(SneakSprintToggleClientCommon.getToggleSprintKeyBinding());
        KeyMappingRegistry.register(SneakSprintToggleClientCommon.getToggleSneakKeyBinding());

        SneakSprintToggleClientCommon.init();
    }
}