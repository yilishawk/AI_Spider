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
    android:name=".GenerateSpiderActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

### 5. 构建 APK

```bash
./gradlew assembleDebug
```

APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

---

## 代码结构

```
AI_Spider_Generator/
├── .github/workflows/build.yml      # GitHub Actions 在线打包
├── gradle/wrapper/                  # Gradle Wrapper 配置
├── gradlew / gradlew.bat           # 可执行脚本
├── settings.gradle                  # 项目设置
├── build.gradle                     # 根构建配置
└── app/
    ├── build.gradle                 # 应用构建配置
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/github/catvod/demo/
        │   ├── GenerateSpiderActivity.java   # 主界面 + AI 调用逻辑
        │   ├── AiProvider.java               # OpenAI 兼容 API 客户端
        │   ├── PromptBuilder.java            # CatVodSpider Prompt 模板
        │   └── CodeGeneratorService.java     # 代码保存服务
        └── res/layout/
            └── activity_generate_spider.xml  # UI 布局文件
```

---

## 功能特性

✅ 支持自定义 AI API 地址、Key、Model  
✅ 预置 CatVodSpider 框架规范 Prompt  
✅ 可选择是否添加密码门禁  
✅ 一键生成 Java Spider 代码  
✅ 自动保存到本地文件夹  
✅ 独立可移植，便于维护  
✅ GitHub Actions 在线打包  

---

## GitHub Actions 在线打包

1. 推送代码到 GitHub
2. 进入仓库 → **Actions** 标签页
3. 会自动触发 `Build AI Spider Generator APK` 任务
4. 构建完成后在 **Actions → Job → Artifacts** 下载 APK

---

## 注意事项

- 本 APP 仅负责**生成代码**，不负责编译 JAR
- 生成的代码需要放入 CatVodSpider 项目的 `app/src/main/java/com/github/catvod/spider/` 目录
- 然后执行：`./gradlew buildCustomSpiderJar`
