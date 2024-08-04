package com.bknote71.codecraft.compile;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.Region;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class S3Uploader {
    public static S3Uploader Instance = new S3Uploader();
    private final ThreadPoolExecutor threadPool;

    private AmazonS3Client s3;
    private final String bucketName = "s3-testtest";
    private String outputPath;

    private final Map<String, String> states = new ConcurrentHashMap<>();

    S3Uploader() {
        this.threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        this.threadPool.setMaximumPoolSize(10);

        s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider("credentials.properties"));
        s3.setRegion(Region.AP_Seoul.toAWSRegion());
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void uploadFile(String key, String author, int specIndex) {
        threadPool.submit(() -> {
            states.put(author + "/" + specIndex, key);
            File file = new File(outputPath + key);
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file);
            s3.putObject(request);

            // completion
            System.out.println("upload complete!!");
            states.remove(author + "/" + specIndex);
            removeFile(outputPath + key);
        });
    }

    public boolean isUploading(String author, int specIndex) {
        return states.containsKey(author + "/" + specIndex);
    }

    public Class<?> loadFromLocal(String author, int specIndex) {
        try {
            String key = states.get(author + "/" + specIndex);
            String className = "com.bknote71.codecraft.engine.sample." + extractClassNameFromKey(key);

            File file = new File(outputPath + extractDirectoryFromKey(key));

            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            return urlClassLoader.loadClass(className);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void removeFile(String path) {
        try {
            String[] removeCmd = new String[]{
                    "rm",
                    "-f",
                    path
            };

            ProcessBuilder removeProcessBuilder = new ProcessBuilder(removeCmd);
            removeProcessBuilder.inheritIO();
            Process removeProcess = removeProcessBuilder.start();
            if (removeProcess.waitFor(5, TimeUnit.SECONDS) != true) {
                removeProcess.destroyForcibly();
            }
        } catch (IOException e) {
            // throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // throw new RuntimeException(e);
        }
    }

    private static String extractClassNameFromKey(String key) {
        // key: author/specIndex/className.class
        String[] parts = key.split("/");
        String javaNameWithExtension = parts[parts.length - 1];
        String className = javaNameWithExtension.replace(".class", "");

        return className;
    }

    private static String extractDirectoryFromKey(String key) {
        if (key != null && key.contains("/")) {
            return key.substring(0, key.lastIndexOf('/'));
        }
        return "";
    }
}
