package com.xiaozi.core.crash;

import com.xiaozi.core.XiaoZiCoreMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;

public class CrashReportGenerator {
    
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path REPORT_DIR = Paths.get("crash-reports", "crashguard");
    
    public static void generate(String crashId, DiagnosticData data) {
        try {
            Files.createDirectories(REPORT_DIR);
            Path jsonPath = REPORT_DIR.resolve(crashId + ".json");
            Files.writeString(jsonPath, GSON.toJson(data));
            XiaoZiCoreMod.LOGGER.info("Crash report generated: {}", jsonPath);
        } catch (IOException e) {
            XiaoZiCoreMod.LOGGER.error("Failed to generate crash report", e);
        }
    }
}