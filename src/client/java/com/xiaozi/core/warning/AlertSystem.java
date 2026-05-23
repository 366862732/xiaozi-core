package com.xiaozi.core.warning;

import com.xiaozi.core.XiaoZiCoreMod;
import com.xiaozi.core.config.XiaoZiCoreConfig;
import com.xiaozi.core.monitor.HealthMonitor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class AlertSystem {
    
    private long lastChatTime = 0;
    private long lastSoundTime = 0;
    private boolean persistentWarning = false;
    
    public AlertSystem() {
        XiaoZiCoreMod.getHealthMonitor().addListener(this::onLevelChange);
    }
    
    private void onLevelChange(HealthMonitor.HealthLevel level) {
        long now = System.currentTimeMillis();
        
        if (XiaoZiCoreConfig.get().showChatWarning && now - lastChatTime > 30000) {
            lastChatTime = now;
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.literal(level.message), false);
            }
        }
        
        if (now - lastSoundTime > 10000) {
            lastSoundTime = now;
            float pitch = level == HealthMonitor.HealthLevel.EMERGENCY ? 0.5f : 0.8f;
            MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING, pitch));
        }
        
        persistentWarning = level.ordinal() >= HealthMonitor.HealthLevel.CRITICAL.ordinal();
    }
    
    public void renderHud(DrawContext context, int width, int height) {
        if (!XiaoZiCoreConfig.get().showHudWarning || !persistentWarning) return;
        
        HealthMonitor.HealthLevel level = XiaoZiCoreMod.getHealthMonitor().getCurrentLevel();
        if (level.ordinal() < HealthMonitor.HealthLevel.CRITICAL.ordinal()) return;
        
        int barHeight = 30;
        context.fill(0, 0, width, barHeight, level.color | 0xC0000000);
        
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int textWidth = textRenderer.getWidth(level.message);
        context.drawText(textRenderer, level.message,
            (width - textWidth) / 2, (barHeight - 9) / 2, 0xFFFFFF, true);
        
        int score = XiaoZiCoreMod.getHealthMonitor().getCurrentScore();
        int barWidth = 200;
        context.fill((width - barWidth) / 2, barHeight + 5,
            (width + barWidth) / 2, barHeight + 13, 0xFF333333);
        context.fill((width - barWidth) / 2, barHeight + 5,
            (width - barWidth) / 2 + barWidth * score / 100, barHeight + 13,
            score > 80 ? 0xFF00AA00 : (score > 60 ? 0xFFFFAA00 : 0xFFFF0000));
    }
}