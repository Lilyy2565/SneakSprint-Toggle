package com.lilyy2565.sneaksprint.mixin.client;

import com.lilyy2565.sneaksprint.SneakSprintToggleClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {

    @ModifyVariable(method = "method_51745", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private List<String> addSneakSprintInfo(List<String> debugInfo, net.minecraft.client.gui.DrawContext context, List<String> text, boolean left) {
        if (left) {
            List<String> modifiedDebugInfo = new ArrayList<>(debugInfo);
            List<String> modInfo = SneakSprintToggleClient.getDebugInfo();
            modifiedDebugInfo.addAll(modInfo);
            return modifiedDebugInfo;
        }
        return debugInfo;
    }
}

