package com.github.catvod.demo;

/**
 * Prompt Builder - 预置 CatVodSpider 规范 Prompt 模板
 */
public class PromptBuilder {

    public String buildPrompt(String websiteCode, boolean addPassword) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下网站代码/API信息，生成一个符合 CatVodSpider 框架的 Java Spider 类。\n\n");

        prompt.append("## 依赖说明\n");
        prompt.append("- 项目使用 CatVodSpider 框架\n");
        prompt.append("- 继承基类: com.github.catvod.spider.Spider\n");
        prompt.append("- 网络库: OkHttp (com.github.catvod.net.OkHttp)\n");
        prompt.append("- 数据模型: Result.java, Vod.java, Class.java, Filter.java, Sub.java\n");
        prompt.append("- 密码门禁: PasswordGate.java (如果需要)\n\n");

        prompt.append("## 签名要求\n");
        prompt.append("- fetchResult(String url, String referer) → String\n");
        prompt.append("- search(String quick, String pg) → String\n");
        prompt.append("- detail(List<String> ids) → String\n");
        prompt.append("- play(String flag, String id, List<Filter> filters) → String\n");
        prompt.append("- category(String tid, String pg, String filter, String extend) → String\n");
        prompt.append("- proxyVideo(String item, List<Filter> filters) → String (可选)\n\n");

        prompt.append("## 输出格式\n");
        prompt.append("- 只返回 Java 代码，不要 Markdown 代码块\n");
        prompt.append("- 代码必须完整可编译\n");
        prompt.append("- 如果网站需要加密算法或签名，我会提供具体算法和签名方式\n\n");

        prompt.append("## 门禁要求\n");
        if (addPassword) {
            prompt.append("- 必须添加密码门禁，用户需输入密码才能访问\n\n");
        } else {
            prompt.append("- 不需要密码门禁\n\n");
        }

        prompt.append("## 网站代码/API信息\n");
        prompt.append(websiteCode);

        return prompt.toString();
    }
}
