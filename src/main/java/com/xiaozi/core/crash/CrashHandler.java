package com.xiaozi.core.crash;

import com.xiaozi.core.XiaoZiCoreMod;
import java.io.*;
import java.nio.file.*;
import java.time.Instant;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final CrashHandler INSTANCE = new CrashHandler();
    private final Thread.UncaughtExceptionHandler defaultHandler;
    
    private CrashHandler() { this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler(); }
    
    public static void install() { Thread.setDefaultUncaughtExceptionHandler(INSTANCE); }
    
    @Override
    public void uncaughtException(Thread thread, Throwable t) {
        String id = "crash_" + Instant.now().toString().replace(":", "-");
        writeEmergencyLog(id, thread, t);
        XiaoZiCoreMod.LOGGER.error("Crash captured: {}", id);
        if (defaultHandler != null) defaultHandler.uncaughtException(thread, t);
    }
    
    private void writeEmergencyLog(String id, Thread thread, Throwable t) {
        try {
            Path dir = Paths.get("crash-reports", "crashguard");
            Files.createDirectories(dir);
            StringBuilder sb = new StringBuilder();
            sb.append("=== CRASHGUARD EMERGENCY LOG ===\n");
            sb.append("ID: ").append(id).append("\n");
            sb.append("Thread: ").append(thread.getName()).append("\n");
            sb.append("Exception: ").append(t.getClass().getName()).append("\n");
            sb.append("Message: ").append(t.getMessage()).append("\n\n");
            sb.append("Stack Trace:\n");
            for (StackTraceElement e : t.getStackTrace()) {
                sb.append("  at ").append(e).append("\n");
            }
            Files.writeString(dir.resolve(id + ".txt"), sb.toString());
        } catch (IOException e) { /* Silent fail */ }
    }
}