# 保存为 Build-All.ps1

$versions = @(
    @{ ver = "1.21.1";  loader = "0.16.9"; yarn = "1.21.1+build.3";   api = "0.148.2+1.21.1" }
    @{ ver = "1.21.2";  loader = "0.15.11"; yarn = "1.21.2+build.1";  api = "0.106.1+1.21.2" }
    @{ ver = "1.21.3";  loader = "0.16.0"; yarn = "1.21.3+build.1";  api = "0.114.1+1.21.3" }
    @{ ver = "1.21.4";  loader = "0.16.2"; yarn = "1.21.4+build.1";  api = "0.119.3+1.21.4" }
    @{ ver = "1.21.5";  loader = "0.16.5"; yarn = "1.21.5+build.1";  api = "0.119.9+1.21.5" }
    @{ ver = "1.21.6";  loader = "0.16.5"; yarn = "1.21.6+build.1";  api = "0.126.1+1.21.6" }
    @{ ver = "1.21.7";  loader = "0.16.5"; yarn = "1.21.7+build.1";  api = "0.128.2+1.21.7" }
    @{ ver = "1.21.8";  loader = "0.16.5"; yarn = "1.21.8+build.1";  api = "0.136.1+1.21.8" }
    @{ ver = "1.21.9";  loader = "0.16.5"; yarn = "1.21.9+build.1";  api = "0.134.1+1.21.9" }
    @{ ver = "1.21.10"; loader = "0.17.0"; yarn = "1.21.10+build.1"; api = "0.138.3+1.21.10" }
    @{ ver = "1.21.11"; loader = "0.17.3"; yarn = "1.21.11+build.1"; api = "0.148.2+1.21.11" }
)

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
$outputDir = "D:\mcMOD\compiled"
New-Item -ItemType Directory -Force -Path $outputDir | Out-Null

foreach ($v in $versions) {
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "编译 Minecraft $($v.ver) (Fabric API $($v.api))" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Cyan
    
    $props = @"
mod_id=xiaozi_core
mod_name=XiaoZiCore
mod_version=1.0.0
mod_description=XiaoZi Core - Crash Prevention System
mod_authors=XiaoZi
archives_base_name=xiaozi_core
maven_group=com.xiaozi.core

minecraft_version=$($v.ver)
loader_version=$($v.loader)
yarn_mappings=$($v.yarn)
fabric_api_version=$($v.api)
fabric_loader_version=$($v.loader)

org.gradle.jvmargs=-Xmx2G
org.gradle.daemon=true
org.gradle.java.home=C:\\Program Files\\BellSoft\\LibericaJDK-21-Full
"@
    [System.IO.File]::WriteAllText("D:\mcMOD\gradle.properties", $props, $utf8NoBom)
    
    $fabricJson = @"
{
  "schemaVersion": 1,
  "id": "xiaozi_core",
  "version": "1.0.0",
  "name": "XiaoZiCore",
  "description": "XiaoZi Core - Crash Prevention System",
  "authors": ["XiaoZi"],
  "license": "MIT",
  "environment": "*",
  "entrypoints": {
    "main": ["com.xiaozi.core.XiaoZiCoreMod"],
    "client": ["com.xiaozi.core.XiaoZiCoreClient"]
  },
  "depends": {
    "fabricloader": ">=0.15.0",
    "minecraft": ">=$($v.ver)",
    "java": ">=21",
    "fabric-api": "*"
  }
}
"@
    [System.IO.File]::WriteAllText("D:\mcMOD\src\main\resources\fabric.mod.json", $fabricJson, $utf8NoBom)
    
    Write-Host "清理中..." -ForegroundColor Gray
    & .\gradlew.bat clean --quiet
    
    Write-Host "构建中..." -ForegroundColor Gray
    & .\gradlew.bat build --quiet
    
    if ($LASTEXITCODE -eq 0) {
        $source = "D:\mcMOD\build\libs\xiaozi_core-1.0.0.jar"
        $dest = "$outputDir\XiaoZiCore_v1.0.0_MC$($v.ver).jar"
        Copy-Item $source $dest -Force
        Write-Host "✅ Minecraft $($v.ver) 编译成功" -ForegroundColor Green
    } else {
        Write-Host "❌ Minecraft $($v.ver) 编译失败" -ForegroundColor Red
    }
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "批量编译完成！" -ForegroundColor Green
Write-Host "输出目录: $outputDir" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Get-ChildItem $outputDir | ForEach-Object { Write-Host "  - $($_.Name)" }