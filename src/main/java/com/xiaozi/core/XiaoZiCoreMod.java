package com.xiaozi.core;

import com.xiaozi.core.config.XiaoZiCoreConfig;
import com.xiaozi.core.crash.CrashHandler;
import com.xiaozi.core.emergency.EmergencySaver;
import com.xiaozi.core.emergency.ForceExitHandler;
import com.xiaozi.core.monitor.HealthMonitor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XiaoZiCoreMod implements ModInitializer {
    public static final String MOD_ID = "crashguard";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static volatile HealthMonitor healthMonitor;
    private static volatile boolean emergencyTriggered = false;
    
    @Override
    public void onInitialize() {
        LOGGER.info("CrashGuard initializing...");
        
        XiaoZiCoreConfig.load();
        CrashHandler.install();
        
        healthMonitor = new HealthMonitor();
        healthMonitor.start();
        
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
        
        LOGGER.info("CrashGuard initialized!");
    }
    
    private void onServerTick(MinecraftServer server) {
        if (healthMonitor != null && healthMonitor.isEmergencyLevel() && !emergencyTriggered) {
            emergencyTriggered = true;
            handleEmergency(server);
        }
    }
    
    private void handleEmergency(MinecraftServer server) {
        LOGGER.warn("========== EMERGENCY MODE ACTIVATED ==========");
        
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                EmergencySaver.forceSaveAll(server);
                ForceExitHandler.exit(server);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "CrashGuard-Emergency").start();
    }
    
    public static HealthMonitor getHealthMonitor() { return healthMonitor; }
}