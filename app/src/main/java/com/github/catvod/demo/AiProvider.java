package com.github.catvod.demo;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * AI Provider - 统一调用 OpenAI 兼容 API
 */
public class AiProvider {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 测试 AI 连接
     */
    public JSONObject testConnection(OkHttpClient client, String apiUrl, String apiKey, String model) throws IOException {
        String url = buildUrl(apiUrl);

        String jsonBody = "{" +
                "\"model\":\"" + escapeJson(model) + "\"," +
                "\"messages\":[" +
                "{\"role\":\"user\",\"content\":\"ping\"}" +
                "]," +
                "\"max_tokens\":10" +
                "}";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(JSON, jsonBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            String responseBody = response.body() != null ? response.body().string() : "";
            return new JSONObject(responseBody);
        }
    }

    /**
     * 调用 AI 生成代码
     */
    public String callAi(OkHttpClient client, String apiUrl, String apiKey, String model, String prompt) throws IOException {
        String url = buildUrl(apiUrl);

        String jsonBody = "{" +
                "\"model\":\"" + escapeJson(model) + "\"," +
                "\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"You are a Java Android developer.\"}," +
                "{\"role\":\"user\",\"content\":\"" + escapeJson(prompt) + "\"}" +
                "]," +
                "\"temperature\":0.7," +
                "\"max_tokens\":8000" +
                "}";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(JSON, jsonBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("AI API 返回错误: " + response.code() + " - " + errorBody);
            }
            String responseBody = response.body() != null ? response.body().string() : "";
            return extractAssistantMessage(responseBody);
        }
    }

    /**
     * 清理 AI 返回的代码（去除 markdown 包裹）
     */
    public String cleanCode(String code) {
        if (code == null || code.isEmpty()) return "";

        // 去除 ```java ... ``` 或 ``` ... ``` 包裹
        if (code.startsWith("```")) {
            code = code.substring(3);
            if (code.startsWith("\n") || code.startsWith("\r")) {
                code = code.substring(1);
            }
            if (code.endsWith("```")) {
                code = code.substring(0, code.length() - 3);
                if (code.endsWith("\n") || code.endsWith("\r")) {
                    code = code.substring(0, code.length() - 1);
                }
            }
        }

        return code.trim();
    }

    /**
     * 构建完整 URL（处理不同平台的 URL 格式）
     */
    private String buildUrl(String baseUrl) {
        if (baseUrl.endsWith("/") && !baseUrl.contains("/chat/completions")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        if (baseUrl.contains("/chat/completions")) {
            return baseUrl;
        } else if (baseUrl.endsWith("/v1")) {
            return baseUrl + "/chat/completions";
        } else {
            return baseUrl + "/v1/chat/completions";
        }
    }

    private String extractAssistantMessage(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            JSONArray choices = obj.getJSONArray("choices");
            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                JSONObject message = firstChoice.getJSONObject("message");
                return message.getString("content");
            }
        } catch (Exception e) {
            // Fallback to simple parsing
        }

        int start = json.indexOf("\"content\":\"");
        if (start == -1) return "";
        start += 11;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
