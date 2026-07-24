package com.github.catvod.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Prompt Builder - 预置 CatVodSpider 规范 Prompt 模板
 */
public class PromptBuilder {

    /**
     * 构建 Prompt（简化版）
     */
    public static String buildPrompt(String websiteCode, boolean addPassword) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下信息，生成一个符合 CatVodSpider 框架的 Java Spider 类。\n\n");

        // ==================== 依赖基础信息（固定） ====================
        prompt.append("## 项目基础依赖\n");
        prompt.append("- 基类: `com.github.catvod.crawler.Spider`\n");
        prompt.append("- 数据模型:\n");
        prompt.append("  - `Result.get().classes(list).string()` — 分类返回\n");
        prompt.append("  - `Result.get().vod(list).page(p, total, limit, totalTotal).string()` — 列表分页\n");
        prompt.append("  - `Result.get().vod(item).string()` — 单条详情\n");
        prompt.append("  - `Result.get().url(realPlayUrl).header(headers).string()` — 播放地址\n");
        prompt.append("  - `Result.string(list)` — 简单视频列表\n");
        prompt.append("- 网络请求: `OkHttp.string(url, Map<String,String> headers)`\n");
        prompt.append("- Vod类方法: `setVodId()`, `setVodName()`, `setVodPic()`, `setVodRemarks()`, `setVodPlayFrom()`, `setVodPlayUrl()`\n\n");

        // ==================== 必须遵守的方法签名 ====================
        prompt.append("## 必须遵守的方法签名\n\n");
        prompt.append("```java\n");
        prompt.append("@Override\n");
        prompt.append("public String homeContent(boolean filter) throws Exception {\n");
        prompt.append("    // 返回 Result.get().classes(list).string()\n");
        prompt.append("}\n\n");

        prompt.append("@Override\n");
        prompt.append("public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) throws Exception {\n");
        prompt.append("    // 返回 Result.get().vod(list).page(page, totalPage, limit, totalTotal).string()\n");
        prompt.append("}\n\n");

        prompt.append("@Override\n");
        prompt.append("public String detailContent(List<String> ids) throws Exception {\n");
        prompt.append("    // 返回 Result.get().vod(vod).string()\n");
        prompt.append("}\n\n");

        prompt.append("@Override\n");
        prompt.append("public String searchContent(String key, boolean quick) throws Exception {\n");
        prompt.append("    // 返回 Result.get().vod(list).string()\n");
        prompt.append("}\n\n");

        prompt.append("@Override\n");
        prompt.append("public String playerContent(String flag, String id, List<String> vipFlags) throws Exception {\n");
        prompt.append("    // 返回 Result.get().url(playUrl).header(getHeaders()).string()\n");
        prompt.append("}\n");
        prompt.append("```\n\n");

        // ==================== 输出规则 ====================
        prompt.append("## 输出格式\n");
        prompt.append("- 只返回 Java 代码，不要 Markdown 代码块\n");
        prompt.append("- 不要包含 package 声明\n");
        prompt.append("- 不要包含 import（由项目统一管理）\n");
        prompt.append("- 直接开始写代码\n\n");

        prompt.append("## 编码规则\n");
        prompt.append("- 使用正则解析 HTML（避免依赖 Jsoup）\n");
        prompt.append("- URL 处理：相对路径自动补全为 `HOST + path`\n");
        prompt.append("- 所有 API 调用用 `try/catch` 包裹\n");
        prompt.append("- 内部辅助类用 `static class` 定义在底部\n\n");

        if (websiteCode != null && !websiteCode.isEmpty()) {
            prompt.append("## 网站代码/API信息\n");
            prompt.append(websiteCode);
        }

        return prompt.toString();
    }
}
