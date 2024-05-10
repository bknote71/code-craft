package com.bknote71.codecraft.compile;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;
import com.bknote71.codecraft.robocode.loader.AwsS3ClassLoader;
import com.bknote71.codecraft.web.dto.CompileResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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

    private AmazonS3Client s3;
    private final String bucketName = "robot-class";

    public JavaClassCompiler() {
        s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("credentials.properties"));
        s3.setRegion(Region.AP_Seoul.toAWSRegion());
    }

    public CompileResult createRobot(String author, String code, int specIndex) {
        String[] lines = code.split("\n");

        int startIndex = 0;
        for (int idx = 0; idx < lines.length; ++idx) {
            if (lines[idx] != null && !(lines[idx].isEmpty() || lines[idx].isBlank())) {
                startIndex = idx;
                break;
            }
        }

        if (startIndex == lines.length) {
            return new CompileResult(-1, "empty code");
        }

        String javaName = "";
        String startLine = lines[startIndex];
        String[] startWords = startLine.split(" ");

        if (startWords.length == 0) {
            return new CompileResult(-1, "있을 수 없는일인데..");
        }

        if (startWords[0].equals("public"))
            javaName = startWords[2];
        else if (startWords[0].equals("class"))
            javaName = startWords[1];

        if (javaName == null || javaName.isEmpty() || javaName.isBlank()) {
            return new CompileResult(-1, "class name 이 없습니다.");
        }

        String realContent =
                "package " + packagePath + ";\n" +
                importPath + "\n" +
                "import com.bknote71.codecraft.robocode.event.*;" +
                code + "\n";

        CompileResult result;
        if ((result = compileRobot(author, javaName, realContent)).exitCode != 0) {
            return result;
        }

        String key = author + "/" + specIndex + "/" + javaName + ".class";
        String jpath = author + "/" + javaName + ".class";
        File file = new File(outputPath + jpath);
        uploadFileToS3(key, file);
        removeDir(outputPath + author);

        return result;
    }

    private CompileResult compileRobot(String author, String javaName, String code) {
        String javaFileName = javaName + ".java";
        String javaClassName = javaName + ".class";
        try (FileWriter writer = new FileWriter(filePath + javaFileName)) {
            writer.write(code);
            writer.flush();
            writer.close();

            String javaPath = "src/main/java";
            String libPath = "lib/*";
            String outputDir = outputPath + author;
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

    private void uploadFileToS3(String key, File file) {
        PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
        s3.putObject(request);
    }

    private void removeDir(String path) {
        try {
            String[] removeCmd = new String[]{
                    "rm",
                    "-rf",
                    path
            };

            ProcessBuilder removeProcessBuilder = new ProcessBuilder(removeCmd);
            removeProcessBuilder.inheritIO();
            Process removeProcess = removeProcessBuilder.start();
            if (removeProcess.waitFor(5, TimeUnit.SECONDS) != true) {
                removeProcess.destroyForcibly();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
