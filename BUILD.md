# AI Spider Generator - 构建说明

## 快速开始

### 1. 克隆项目到本地
```bash
git clone https://github.com/yourname/AI_Spider_Generator.git
cd AI_Spider_Generator
```

### 2. 配置环境
- JDK 17+
- Android SDK (API 34)
- Gradle 8.5+

### 3. 添加 OkHttp 依赖

在 `build.gradle` 的 dependencies 中添加：

```groovy
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
```

### 4. 注册 Activity

在 `AndroidManifest.xml` 中：

```xml
<activity
    android:name=".aispider.GenerateSpiderActivity"
    android:exported="false" />
```

### 5. 构建 APK

```bash
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

---

## 代码结构

```
aispider/
├── GenerateSpiderActivity.java      # 主界面 + AI 调用逻辑
├── AiProvider.java                  # OpenAI 兼容 API 客户端
├── PromptBuilder.java               # CatVodSpider Prompt 模板
└── CodeGeneratorService.java        # 代码保存服务
```

---

## 功能特性

✅ 支持自定义 AI API 地址、Key、Model  
✅ 预置 CatVodSpider 框架规范 Prompt  
✅ 可选择是否添加密码门禁  
✅ 一键生成 Java Spider 代码  
✅ 自动保存到本地文件夹  
✅ 独立可移植，便于维护  

---

## 后续打包流程

生成器生成的 Java 代码会保存到：
```
/storage/emulated/0/Android/data/com.github.catvod.demo/files/spiders/
```

你可以通过以下方式获取：
1. 使用文件管理器导出
2. 使用 GitHub Actions 在线打包 JAR
3. 手动移动到项目的 `spider` 目录

---

## 注意事项

- 本 APP 仅负责**生成代码**，不负责编译 JAR
- 生成的代码需要放入 CatVodSpider 项目的 `app/src/main/java/com/github/catvod/spider/` 目录
- 然后执行：`./gradlew buildCustomSpiderJar`
