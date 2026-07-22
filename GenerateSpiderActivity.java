package com.github.catvod.aispider;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * AI Spider Generator - Main Activity
 * 用户输入 AI API Key、URL、Model，粘贴网站代码/API信息，生成 Java 爬虫
 */
public class GenerateSpiderActivity extends Activity {

    private EditText etApiUrl, etApiKey, etModel, etWebsiteCode;
    private Button btnGenerate, btnReset, btnTogglePassword;
    private CheckBox cbAddPassword;
    private ScrollView svOutput;
    private TextView tvStatus;
    private ProgressBar pbProgress;
    private CodeGeneratorService generatorService;
    private PromptBuilder promptBuilder;
    private AiProvider aiProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_spider);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etApiUrl = findViewById(R.id.et_api_url);
        etApiKey = findViewById(R.id.et_api_key);
        etModel = findViewById(R.id.et_model);
        etWebsiteCode = findViewById(R.id.et_website_code);
        btnGenerate = findViewById(R.id.btn_generate);
        btnReset = findViewById(R.id.btn_reset);
        btnTogglePassword = findViewById(R.id.btn_toggle_password);
        cbAddPassword = findViewById(R.id.cb_add_password);
        svOutput = findViewById(R.id.sv_output);
        tvStatus = findViewById(R.id.tv_status);
        pbProgress = findViewById(R.id.pb_progress);
        generatorService = new CodeGeneratorService(this);
        promptBuilder = new PromptBuilder();
        aiProvider = new AiProvider();
    }

    private void setupListeners() {
        btnGenerate.setOnClickListener(v -> generateSpider());
        btnReset.setOnClickListener(v -> resetForm());
        btnTogglePassword.setOnClickListener(v -> togglePasswordGate());
    }

    private void generateSpider() {
        String apiUrl = etApiUrl.getText().toString().trim();
        String apiKey = etApiKey.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String websiteCode = etWebsiteCode.getText().toString().trim();

        if (apiUrl.isEmpty() || apiKey.isEmpty() || model.isEmpty()) {
            Toast.makeText(this, "请填写 AI API 地址、Key 和 Model", Toast.LENGTH_SHORT).show();
            return;
        }

        if (websiteCode.isEmpty()) {
            Toast.makeText(this, "请粘贴网站代码/API 信息", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建 Prompt
        boolean addPassword = cbAddPassword.isChecked();
        String prompt = promptBuilder.buildPrompt(websiteCode, addPassword);

        // 调用 AI
        tvStatus.setText("正在调用 AI...");
        pbProgress.setVisibility(View.VISIBLE);
        btnGenerate.setEnabled(false);

        new Thread(() -> {
            try {
                String response = aiProvider.callAi(apiUrl, apiKey, model, prompt);
                runOnUiThread(() -> {
                    tvStatus.setText("AI 响应成功，正在解析代码...");
                    try {
                        generatorService.saveGeneratedCode(response);
                        Toast.makeText(this, "代码已保存到独立文件夹", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        tvStatus.setText("保存失败: " + e.getMessage());
                    }
                    btnGenerate.setEnabled(true);
                    pbProgress.setVisibility(View.GONE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvStatus.setText("AI 调用失败: " + e.getMessage());
                    btnGenerate.setEnabled(true);
                    pbProgress.setVisibility(View.GONE);
                });
            }
        }).start();
    }

    private void resetForm() {
        etApiUrl.setText("");
        etApiKey.setText("");
        etModel.setText("");
        etWebsiteCode.setText("");
        tvStatus.setText("");
    }

    private void togglePasswordGate() {
        LinearLayout passwordLayout = findViewById(R.id.ll_password_layout);
        if (passwordLayout.getVisibility() == View.VISIBLE) {
            passwordLayout.setVisibility(View.GONE);
        } else {
            passwordLayout.setVisibility(View.VISIBLE);
        }
    }
}
