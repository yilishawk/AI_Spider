package com.github.catvod.demo;

import android.content.Context;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Code Generator Service - 代码生成与本地保存
 */
public class CodeGeneratorService {

    private Context context;

    public CodeGeneratorService(Context context) {
        this.context = context;
    }

    /**
     * 保存到内部存储
     */
    public File saveToInternalStorage(String code, String fileName) throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        }
        return file;
    }

    /**
     * 读取已保存的代码文件
     */
    public String readCodeFile(String fileName) throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        StringBuilder sb = new StringBuilder();
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * 获取保存目录的完整路径
     */
    public File getOutputDirectory() {
        return context.getFilesDir();
    }
}
