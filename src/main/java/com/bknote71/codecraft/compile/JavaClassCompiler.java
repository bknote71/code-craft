package com.bknote71.codecraft.compile;

import com.bknote71.codecraft.engine.loader.AwsS3ClassLoader;
import com.bknote71.codecraft.web.dto.CompileResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class JavaClassCompiler {
    private static String packagePath;
    private static String importPath;
    private static String filePath;
    private static String outputPath;
    private static String eventPath;
    private static String[] delegatedNames;

    static {
        try (InputStream input = AwsS3ClassLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                throw new RuntimeException();
            }

            prop.load(input);
            packagePath = prop.getProperty("path.package");
            importPath = prop.getProperty("path.import");
            filePath = prop.getProperty("path.file");
            outputPath = prop.getProperty("path.output");
            eventPath = prop.getProperty("path.event");
            delegatedNames = prop.getProperty("path.delegate").split(",");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private S3Uploader uploadThreadPool;

    public JavaClassCompiler() {
        this.uploadThreadPool = S3Uploader.Instance;
        this.uploadThreadPool.setOutputPath(outputPath);
    }

    public CompileResult createRobot(String author, String code, int specIndex) {
        final String javaName = validateCode(code);

        if (javaName == null) {
            return new CompileResult(-1, "code exception");
        }

        code = preprocess(code);
        String realContent = realContent(code);

        CompileResult result;
        if ((result = compileRobot(author, specIndex, javaName, realContent)).exitCode != 0) {
            return result;
        }

        String key = author + "/" + specIndex + "/" + javaName + ".class";
        uploadThreadPool.uploadFile(key, author, specIndex);
        return result;
    }

    private String validateCode(String code) {
        String[] lines = code.split("\n");
        int startIndex = 0;
        for (int idx = 0; idx < lines.length; ++idx) {
            if (lines[idx] != null && !(lines[idx].isEmpty() || lines[idx].isBlank())) {
                startIndex = idx;
                break;
            }
        }

        if (startIndex == lines.length) {
            return null;
        }

        String javaName = "";
        String startLine = lines[startIndex];
        String[] startWords = startLine.split(" ");

        if (startWords.length == 0) {
            return null;
        }

        if (startWords[0].equals("public"))
            javaName = startWords[2];
        else if (startWords[0].equals("class"))
            javaName = startWords[1];

        if (javaName == null || javaName.isEmpty() || javaName.isBlank()) {
            return null;
        }

        return javaName;
    }

    private String preprocess(String code) {
        String patternString = "while\\s*\\(([^)]+)\\)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(code);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            System.out.println(matcher);
            String originalCondition = matcher.group(1); // 기존 조건 추출
            String modifiedCondition = "(" + originalCondition + ")" + " && !Thread.currentThread().isInterrupted()";
            matcher.appendReplacement(sb, "while (" + modifiedCondition + ")");
        }
        matcher.appendTail(sb);
         code = sb.toString();

        String whilePattern = "while\\s*\\(";
        String forPattern = "(for\\s*\\([^)]*\\)\\s*\\{)([^}]*)\\}";

        code = addSleepToForLoops(code, forPattern);
        code = addSleepToWhileLoops(code, whilePattern);

        return code;
    }



    private String addSleepToForLoops(String code, String pattern) {
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(code);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String loopStart = matcher.group(1); // 루프 시작 부분 (while 또는 for)
            String loopBody = matcher.group(2);  // 루프 내부 코드
            String modifiedLoop = loopStart + loopBody + "\n    Thread.sleep(1); \n}"; // Thread.sleep(1) 추가
            matcher.appendReplacement(result, modifiedLoop);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String addSleepToWhileLoops(String code, String pattern) {
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(code);
        StringBuilder result = new StringBuilder();

        int lastEnd = 0;
        while (matcher.find()) {
            int openParen = matcher.end();   // '(' 바로 다음 위치
            int closeParen = findClosingParen(code, openParen); // 닫는 괄호 ')' 위치
            int openBrace = code.indexOf('{', closeParen);      // 여는 중괄호 '{' 위치

            if (closeParen != -1 && openBrace != -1) {
                int loopEnd = findClosingBrace(code, openBrace + 1); // 닫는 중괄호 '}' 위치

                if (loopEnd != -1) {
                    // 이전까지의 코드 추가
                    result.append(code, lastEnd, openBrace + 1); // 기존 코드에서 중괄호 까지 추가
                    result.append("\n    Thread.sleep(1);"); // while 문 본문에 sleep 추가
                    lastEnd = openBrace + 1; // 마지막 위치 업데이트
                }
            }
        }
        result.append(code.substring(lastEnd)); // 남은 코드 추가
        return result.toString();
    }

    // 닫는 괄호 ')' 찾기
    private int findClosingParen(String code, int start) {
        int openParens = 1;
        for (int i = start; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '(') openParens++;
            else if (c == ')') openParens--;
            if (openParens == 0) return i;
        }
        return -1;
    }

    // 닫는 중괄호 '}' 찾기
    private int findClosingBrace(String code, int start) {
        int openBraces = 1;
        for (int i = start; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '{') openBraces++;
            else if (c == '}') openBraces--;
            if (openBraces == 0) return i + 1;
        }
        return -1;
    }

    private CompileResult compileRobot(String author, int specIndex, String javaName, String code) {
        String javaFileName = javaName + ".java";
        String javaClassName = javaName + ".class";
        try {
            FileWriter writer = new FileWriter(filePath + javaFileName);
            writer.write(code);
            writer.flush();
            writer.close();

            String javaPath = "src/main/java";
            String libPath = "lib/*";
            String outputDir = outputPath + author + "/" + specIndex;
            String sourceFile = filePath + javaFileName;

            String[] cmd = new String[]{
                    "javac",
                    "-cp",
                    String.format("%s:%s", javaPath, libPath),
                    "-d",
                    outputDir,
                    sourceFile
            };

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();

            if ((process.waitFor(20, TimeUnit.SECONDS)) != true) { // 정상 종료되지 않음
                process.destroyForcibly();
                return new CompileResult(-1, "timeout");
            }

            if (process.exitValue() != 0) {
                return new CompileResult(-1, "Compile Error");
            }

            String[] copyCmd = new String[]{
                    "cp",
                    "-f",
                    String.format("%s/%s/%s", outputDir, packagePath.replace(".", "/"), javaClassName),
                    outputDir
            };

            ProcessBuilder copyProcessBuilder = new ProcessBuilder(copyCmd);
            copyProcessBuilder.inheritIO();
            Process copyProcess = copyProcessBuilder.start();

            if (copyProcess.waitFor(5, TimeUnit.SECONDS) != true) {
                copyProcess.destroyForcibly();
                return new CompileResult(-1, "timeout");
            }

            if (copyProcess.exitValue() != 0) {
                return new CompileResult(-1, "copy error");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new CompileResult(-1, e.toString());
        }

        return new CompileResult(0, "success", javaName, javaClassName, code);
    }

    private String realContent(String code) {
        return
                "package " + packagePath + ";\n" +
                        importPath + "\n" +
                        "import com.bknote71.codecraft.engine.event.*;" +
                        code + "\n";
    }
}
