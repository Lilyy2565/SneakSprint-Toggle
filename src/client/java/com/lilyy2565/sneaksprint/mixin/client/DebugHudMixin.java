package com.lilyy2565.sneaksprint.mixin.client;

import com.lilyy2565.sneaksprint.SneakSprintToggleClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @Inject(method = "getLeftText", at = @At("RETURN"))
    private void addSneakSprintInfo(CallbackInfoReturnable<List<String>> cir) {
        List<String> debugInfo = cir.getReturnValue();
        List<String> modInfo = SneakSprintToggleClient.getDebugInfo();
        debugInfo.addAll(modInfo);
    }
}
