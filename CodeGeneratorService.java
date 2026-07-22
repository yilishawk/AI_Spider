package com.github.catvod.aispider;

import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Code Generator Service - 代码生成与保存
 */
public class CodeGeneratorService {

    private Context context;
    private File outputDir;

    public CodeGeneratorService(Context context) {
        this.context = context;
        this.outputDir = getOutputDirectory();
    }

    private File getOutputDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return new File(context.getExternalFilesDir(null), "spiders");
        } else {
            return new File(context.getFilesDir(), "spiders");
        }
    }

    public void saveGeneratedCode(String code) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        String fileName = generateFilename();
        File file = new File(outputDir, fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }

        System.out.println("已保存: " + file.getAbsolutePath());
    }

    private String generateFilename() {
        return "spider_" + System.currentTimeMillis() + ".java";
    }
}
