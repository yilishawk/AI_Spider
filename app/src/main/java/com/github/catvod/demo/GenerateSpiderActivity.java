package com.github.catvod.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * AI Spider Generator - Main Activity
 * 用户输入 AI API Key/URL/Model，粘贴网站代码/API信息，生成 Java 爬虫
 */
public class GenerateSpiderActivity extends Activity {

    private EditText etApiUrl, etApiKey, etModel, etWebsiteCode;
    private EditText etGeneratedCode;
    private Button btnGenerate, btnReset, btnEditAi, btnTestAi, btnSaveAi, btnCopy, btnDownload;
    private CheckBox cbAddPassword;
    private ScrollView svResult;
    private TextView tvStatus, tvAiLabel;
    private LinearLayout llAiConfig, llActionButtons, llPasswordLayout;

    private CodeGeneratorService generatorService;
    private AiProvider aiProvider;
    private OkHttpClient client;

    private static final String PREFS_NAME = "spider_generator_prefs";
    private static final String KEY_API_URL = "api_url";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_MODEL = "model";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_spider);

        initViews();
        setupListeners();
        loadSavedConfig();
    }

    private void initViews() {
        etApiUrl = findViewById(R.id.et_api_url);
        etApiKey = findViewById(R.id.et_api_key);
        etModel = findViewById(R.id.et_model);
        etWebsiteCode = findViewById(R.id.et_website_code);
        etGeneratedCode = findViewById(R.id.et_generated_code);
        btnGenerate = findViewById(R.id.btn_generate);
        btnReset = findViewById(R.id.btn_reset);
        btnEditAi = findViewById(R.id.btn_edit_ai);
        btnTestAi = findViewById(R.id.btn_test_ai);
        btnSaveAi = findViewById(R.id.btn_save_ai);
        btnCopy = findViewById(R.id.btn_copy);
        btnDownload = findViewById(R.id.btn_download);
        cbAddPassword = findViewById(R.id.cb_add_password);
        svResult = findViewById(R.id.sv_result);
        tvStatus = findViewById(R.id.tv_status);
        tvAiLabel = findViewById(R.id.tv_ai_label);
        llAiConfig = findViewById(R.id.ll_ai_config);
        llActionButtons = findViewById(R.id.ll_action_buttons);
        llPasswordLayout = findViewById(R.id.ll_password_layout);

        generatorService = new CodeGeneratorService(this);
        aiProvider = new AiProvider();
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build();
    }

    private void setupListeners() {
        btnEditAi.setOnClickListener(v -> toggleAiConfig());
        btnTestAi.setOnClickListener(v -> testAiConnection());
        btnSaveAi.setOnClickListener(v -> saveAiConfig());
        btnGenerate.setOnClickListener(v -> generateSpider());
        btnReset.setOnClickListener(v -> resetForm());
        btnCopy.setOnClickListener(v -> copyCode());
        btnDownload.setOnClickListener(v -> downloadCode());
    }

    // ==================== AI 配置面板 ====================

    private void toggleAiConfig() {
        if (llAiConfig.getVisibility() == View.VISIBLE) {
            llAiConfig.setVisibility(View.GONE);
        } else {
            llAiConfig.setVisibility(View.VISIBLE);
            // 填充已保存的配置
            loadSavedConfigToFields();
        }
    }

    private void saveAiConfig() {
        String apiUrl = etApiUrl.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();
        String model = etModel.getText().toString().trim();

        if (apiUrl.isEmpty() || apiKey.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "请填写完整的 AI API 配置", Toast.LENGTH_SHORT).show();
            return;
        }

        savePreferences(KEY_API_URL, apiUrl);
        savePreferences(KEY_API_KEY, apiKey);
        savePreferences(KEY_MODEL, model);

        llAiConfig.setVisibility(View.GONE);
        tvAiLabel.setText("AI: " + getShortModelName(model));
        Toast.makeText(this, "配置已保存", Toast.LENGTH_SHORT).show();
    }

    private void loadSavedConfig() {
        String apiUrl = getPreference(KEY_API_URL, "");
        String apiKey = getPreference(KEY_API_KEY, "");
        String model = getPreference(KEY_MODEL, "");

        if (!apiUrl.isEmpty() && !apiKey.isEmpty() && !model.isEmpty()) {
            tvAiLabel.setText("AI: " + getShortModelName(model));
        }
    }

    private void loadSavedConfigToFields() {
        etApiUrl.setText(getPreference(KEY_API_URL, ""));
        etApiKey.setText(getPreference(KEY_API_KEY, ""));
        etModel.setText(getPreference(KEY_MODEL, ""));
    }

    private String getShortModelName(String model) {
        if (model.contains("/")) {
            return model.substring(model.lastIndexOf("/") + 1);
        }
        return model;
    }

    // ==================== AI 连接测试 ====================

    private void testAiConnection() {
        String apiUrl = etApiUrl.getText().toString().trim();
        String apiKey = getPreference(KEY_API_KEY, etApiKey.getText().toString().trim());
        String model = getPreference(KEY_MODEL, etModel.getText().toString().trim());

        if (apiUrl.isEmpty() || apiKey.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "请先配置 AI API", Toast.LENGTH_SHORT).show();
            return;
        }

        showStatus("正在测试 AI 连接...");
        new Thread(() -> {
            try {
                JSONObject response = aiProvider.testConnection(client, apiUrl, apiKey, model);
                runOnUiThread(() -> {
                    if (response != null) {
                        showStatus("✅ AI 连接成功！返回代码: " + response.optString("status", "200"));
                    } else {
                        showStatus("❌ AI 连接失败，请检查配置");
                    }
                    toggleGenerateButton(true);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showStatus("❌ AI 调用失败: " + e.getMessage());
                    toggleGenerateButton(true);
                });
            }
        }).start();
    }

    // ==================== 生成爬虫 ====================

    private void generateSpider() {
        String apiUrl = etApiUrl.getText().toString().trim();
        String apiKey = getPreference(KEY_API_KEY, etApiKey.getText().toString().trim());
        String model = getPreference(KEY_MODEL, etModel.getText().toString().trim());
        String websiteCode = etWebsiteCode.getText().toString().trim();

        if (apiUrl.isEmpty() || apiKey.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "请填写 AI API 地址、Key 和 Model", Toast.LENGTH_SHORT).show();
            return;
        }

        if (websiteCode.isEmpty()) {
            Toast.makeText(this, "请粘贴网站代码/API 信息", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean addPassword = cbAddPassword.isChecked();
        String prompt = PromptBuilder.buildPrompt(websiteCode, addPassword);

        showStatus("正在调用 AI...");
        toggleGenerateButton(false);
        llActionButtons.setVisibility(View.GONE);
        etGeneratedCode.setText("");
        svResult.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                String response = aiProvider.callAi(client, apiUrl, apiKey, model, prompt);
                runOnUiThread(() -> {
                    String cleanedCode = aiProvider.cleanCode(response);
                    etGeneratedCode.setText(cleanedCode);
                    svResult.setVisibility(View.VISIBLE);
                    llActionButtons.setVisibility(View.VISIBLE);
                    showStatus("✅ 代码生成成功");
                    toggleGenerateButton(true);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showStatus("❌ AI 调用失败: " + e.getMessage());
                    toggleGenerateButton(true);
                });
            }
        }).start();
    }

    // ==================== 复制与下载 ====================

    private void copyCode() {
        String code = etGeneratedCode.getText().toString();
        if (code.isEmpty()) {
            Toast.makeText(this, "没有可复制的代码", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clipData = android.content.ClipData.newPlainText("Generated Spider", code);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(this, "✅ 代码已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

    private void downloadCode() {
        String code = etGeneratedCode.getText().toString();
        if (code.isEmpty()) {
            Toast.makeText(this, "没有可下载的代码", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = "spider_" + System.currentTimeMillis() + ".java";
            File file = generatorService.saveToInternalStorage(code, fileName);
            if (file != null) {
                Toast.makeText(this, "✅ 文件已保存: " + file.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "❌ 保存失败，请选择其他位置", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "❌ 保存失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // ==================== 辅助方法 ====================

    private void resetForm() {
        etWebsiteCode.setText("");
        etGeneratedCode.setText("");
        tvStatus.setText("");
        svResult.setVisibility(View.GONE);
        llActionButtons.setVisibility(View.GONE);
        toggleGenerateButton(true);
    }

    private void showStatus(String message) {
        tvStatus.setText(message);
        tvStatus.setVisibility(View.VISIBLE);
    }

    private void toggleGenerateButton(boolean enabled) {
        btnGenerate.setEnabled(enabled);
        btnGenerate.setAlpha(enabled ? 1.0f : 0.5f);
    }

    private void savePreferences(String key, String value) {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(key, value).apply();
    }

    private String getPreference(String key, String defaultValue) {
        android.content.SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(key, defaultValue);
    }
}
