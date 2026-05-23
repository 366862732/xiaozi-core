# Fabric Starter Mod

一个可直接打开的 Fabric 1.21.x 模组开发起始工程。

## 当前配置

- Minecraft: `1.21.11`
- Fabric Loader: `0.17.3`
- Fabric API: `0.141.3+1.21.11`
- Gradle Loom: `1.16-SNAPSHOT`
- 开发 JDK: `25`
- 编译目标: `Java 21`

## 为什么是 JDK 25 + Java 21

工程默认使用 `JDK 25` 作为开发工具链，和你当前机器环境一致。
但输出的 class 目标仍然锁定为 `Java 21`，这样更适合 Fabric `1.21.x` 生态，避免把成品模组限制死在更高版本运行时上。

如果你明确只打算让模组运行在 `Java 25+`，可以把 `build.gradle` 里的 `options.release`、`sourceCompatibility` 和 `targetCompatibility` 改成 `25`。

## 常用命令

Windows:

```powershell
.\gradlew.bat genSources
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

## 打开方式

- IntelliJ IDEA: 直接以 Gradle 项目打开根目录
- VS Code: 安装 Java Extension Pack 后打开根目录

首次导入时建议先执行一次：

```powershell
.\gradlew.bat genSources
```

## 需要你后续修改的占位信息

- `gradle.properties`
  - `mod_id`
  - `mod_name`
  - `maven_group`
- `src/main/resources/fabric.mod.json`
  - 描述
  - 作者
  - 联系方式
- Java 包名
  - 当前示例包为 `com.example.startermod`

## 目录说明

- `src/main/java`: 公共与服务端逻辑
- `src/client/java`: 仅客户端逻辑
- `src/main/resources/assets/startermod`: 资源与图标

## License

本工程基于 Fabric 官方示例整理，保留原始 `CC0-1.0` 许可证。
