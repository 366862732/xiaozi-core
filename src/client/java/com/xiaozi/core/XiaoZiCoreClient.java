package com.xiaozi.core;

import com.xiaozi.core.warning.AlertSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class XiaoZiCoreClient implements ClientModInitializer {
    
    private static AlertSystem alertSystem;
    
    @Override
    public void onInitializeClient() {
        alertSystem = new AlertSystem();
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            if (alertSystem != null) {
                var client = net.minecraft.client.MinecraftClient.getInstance();
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();
                alertSystem.renderHud(context, width, height);
            }
        });
    }
    
    public static AlertSystem getAlertSystem() {
        return alertSystem;
    }
}