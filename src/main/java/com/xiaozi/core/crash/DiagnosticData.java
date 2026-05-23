package com.xiaozi.core.crash;

import java.time.Instant;
import java.util.*;

public class DiagnosticData {
    public String crashId;
    public Instant crashTime;
    public String crashThread;
    public long crashThreadId;
    public String exceptionClass;
    public String exceptionMessage;
    public List<String> stackTrace = new ArrayList<>();
    
    public MemoryInfo memoryInfo = new MemoryInfo();
    public HardwareInfo hardwareInfo = new HardwareInfo();
    public ModInfo modInfo = new ModInfo();
    public GameConfig gameConfig = new GameConfig();
    public Map<String, List<String>> threadDumps = new HashMap<>();
    
    public static class MemoryInfo {
        public long maxMemory, totalMemory, freeMemory, usedMemory;
        public int usagePercent;
        public Map<String, Map<String, Long>> poolDetails = new HashMap<>();
        public Map<String, Map<String, Long>> gcStats = new HashMap<>();
    }
    
    public static class HardwareInfo {
        public int cpuCores;
        public String osName, osArch, osVersion;
        public double systemLoadAverage;
        public long totalPhysicalMemory;
    }
    
    public static class ModInfo {
        public String fabricVersion;
        public List<Map<String, String>> mods = new ArrayList<>();
    }
    
    public static class GameConfig {
        public int viewDistance, simulationDistance, maxFps;
        public String graphicsMode;
        public boolean vsync;
        public long allocatedMemory;
    }
}