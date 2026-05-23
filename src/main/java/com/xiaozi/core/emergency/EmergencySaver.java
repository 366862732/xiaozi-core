package com.xiaozi.core.emergency;

import com.xiaozi.core.XiaoZiCoreMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.concurrent.*;

public class EmergencySaver {
    
    private static volatile boolean saving = false;
    
    public static boolean forceSaveAll(MinecraftServer server) {
        if (saving) return false;
        saving = true;
        XiaoZiCoreMod.LOGGER.info("Starting emergency save...");
        
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    server.getPlayerManager().saveAllPlayerData();
                    for (ServerWorld world : server.getWorlds()) {
                        world.save(null, true, false);
                    }
                    server.save(true, false, false);
                } catch (Exception e) {
                    XiaoZiCoreMod.LOGGER.error("Emergency save error", e);
                }
            });
            future.get(10, TimeUnit.SECONDS);
            XiaoZiCoreMod.LOGGER.info("Emergency save completed");
            return true;
        } catch (TimeoutException e) {
            XiaoZiCoreMod.LOGGER.error("Emergency save timeout");
            return false;
        } catch (Exception e) {
            XiaoZiCoreMod.LOGGER.error("Emergency save failed", e);
            return false;
        } finally {
            saving = false;
        }
    }
}