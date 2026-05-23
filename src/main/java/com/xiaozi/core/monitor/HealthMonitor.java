package com.xiaozi.core.monitor;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import com.xiaozi.core.XiaoZiCoreMod;
import com.xiaozi.core.config.XiaoZiCoreConfig;

public class HealthMonitor {
    
    public enum HealthLevel {
        HEALTHY(80, 100, "System running normally", 0x00AA00),
        WARNING(60, 79, "High memory usage, consider saving", 0xFFAA00),
        CRITICAL(30, 59, "Critical memory pressure, save and exit now!", 0xFF6600),
        EMERGENCY(0, 29, "System about to crash, emergency protocol...", 0xFF0000);
        
        public final int min, max, color;
        public final String message;
        HealthLevel(int min, int max, String message, int color) {
            this.min = min; this.max = max; this.message = message; this.color = color;
        }
        public static HealthLevel fromScore(int score) {
            for (HealthLevel l : values()) if (score >= l.min && score <= l.max) return l;
            return HEALTHY;
        }
    }
    
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<Consumer<HealthLevel>> listeners = new CopyOnWriteArrayList<>();
    private volatile int currentScore = 100;
    private volatile HealthLevel currentLevel = HealthLevel.HEALTHY;
    private volatile boolean emergencyTriggered = false;
    private long lastGcTime = 0;
    private int consecutiveHighUsage = 0;
    
    public void start() {
        int interval = XiaoZiCoreConfig.get().checkIntervalSeconds;
        scheduler.scheduleAtFixedRate(this::checkHealth, 0, interval, TimeUnit.SECONDS);
        XiaoZiCoreMod.LOGGER.info("HealthMonitor started");
    }
    
    private void checkHealth() {
        int memScore = calcMemoryScore();
        int gcScore = calcGcScore();
        int newScore = (memScore * 70 + gcScore * 30) / 100;
        currentScore = (currentScore * 3 + newScore) / 4;
        
        HealthLevel newLevel = HealthLevel.fromScore(currentScore);
        if (newLevel != currentLevel && newLevel.ordinal() > currentLevel.ordinal()) {
            currentLevel = newLevel;
            listeners.forEach(l -> l.accept(newLevel));
            if (newLevel == HealthLevel.CRITICAL || newLevel == HealthLevel.EMERGENCY) {
                System.gc();
            }
        }
        currentLevel = newLevel;
    }
    
    private int calcMemoryScore() {
        Runtime rt = Runtime.getRuntime();
        long used = rt.totalMemory() - rt.freeMemory();
        int usage = (int)(used * 100 / rt.maxMemory());
        int score = 100 - usage;
        if (usage > XiaoZiCoreConfig.get().warningThresholdPercent) {
            consecutiveHighUsage++;
            if (consecutiveHighUsage > 5) score -= 10;
        } else {
            consecutiveHighUsage = Math.max(0, consecutiveHighUsage - 1);
        }
        return Math.max(0, score);
    }
    
    private int calcGcScore() {
        long now = 0;
        for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
            now += bean.getCollectionTime();
        }
        long delta = now - lastGcTime;
        lastGcTime = now;
        return delta > 500 ? Math.max(0, 100 - (int)(delta / 10)) : 100;
    }
    
    public void addListener(Consumer<HealthLevel> listener) { listeners.add(listener); }
    public HealthLevel getCurrentLevel() { return currentLevel; }
    public boolean isEmergencyLevel() { return currentLevel == HealthLevel.EMERGENCY; }
    public void setEmergencyTriggered(boolean b) { emergencyTriggered = b; }
    public boolean isEmergencyTriggered() { return emergencyTriggered; }
    public int getCurrentScore() { return currentScore; }
}