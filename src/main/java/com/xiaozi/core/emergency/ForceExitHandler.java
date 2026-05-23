package com.xiaozi.core.emergency;

import com.xiaozi.core.XiaoZiCoreMod;
import net.minecraft.server.MinecraftServer;

public class ForceExitHandler {
    
    public static void exit(MinecraftServer server) {
        XiaoZiCoreMod.LOGGER.warn("Starting force exit...");
        try {
            if (server != null) {
                server.stop(false);
            }
            Thread.sleep(1000);
            XiaoZiCoreMod.LOGGER.info("Exiting game...");
            System.exit(0);
        } catch (Exception e) {
            XiaoZiCoreMod.LOGGER.error("Graceful shutdown failed, force terminating");
            Runtime.getRuntime().halt(1);
        }
    }
}