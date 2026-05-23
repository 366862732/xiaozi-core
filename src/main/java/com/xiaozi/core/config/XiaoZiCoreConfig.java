package com.xiaozi.core.config;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;

import com.xiaozi.core.XiaoZiCoreMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class XiaoZiCoreConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("crashguard.json");
    
    public int checkIntervalSeconds = 2;
    public int warningThresholdPercent = 75;
    public int criticalThresholdPercent = 85;
    public int emergencyThresholdPercent = 95;
    public int healthHistorySize = 60;
    
    public int emergencyCountdownSeconds = 10;
    public boolean autoSaveOnEmergency = true;
    public boolean autoReduceViewDistance = true;
    public int reducedViewDistance = 16;
    public boolean showHudWarning = true;
    public boolean showChatWarning = true;
    
    public boolean enableCrashReporting = true;
    public boolean includeFullThreadDump = true;
    public int maxReportCount = 20;
    public boolean openReportFolderOnCrash = true;
    
    public boolean autoUploadEnabled = false;
    public boolean consentObtained = false;
    public boolean debugLogging = false;
    
    private static XiaoZiCoreConfig INSTANCE;
    
    public static XiaoZiCoreConfig get() {
        if (INSTANCE == null) load();
        return INSTANCE;
    }
    
    public static void load() {
        try {
            if (CONFIG_PATH.toFile().exists()) {
                try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                    INSTANCE = GSON.fromJson(reader, XiaoZiCoreConfig.class);
                }
            } else {
                INSTANCE = new XiaoZiCoreConfig();
                save();
            }
        } catch (IOException e) {
            XiaoZiCoreMod.LOGGER.error("Failed to load config", e);
            INSTANCE = new XiaoZiCoreConfig();
        }
    }
    
    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            XiaoZiCoreMod.LOGGER.error("Failed to save config", e);
        }
    }
}