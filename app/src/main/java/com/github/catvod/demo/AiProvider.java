package com.github.catvod.demo;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * AI Provider - 统一调用 OpenAI 兼容 API
 */
public class AiProvider {

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    public String callAi(String apiUrl, String apiKey, String model, String prompt) throws IOException {
        String url = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
        if (!url.contains("/chat/completions")) {
            url += "/v1/chat/completions";
        }

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
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI API 返回错误: " + response.code());
            }
            String responseBody = response.body() != null ? response.body().string() : "";
            return extractAssistantMessage(responseBody);
        }
    }

    private String extractAssistantMessage(String json) {
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
