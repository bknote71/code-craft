package com.bknote71.codecraft.robocode.loader;

import com.bknote71.codecraft.robocode.api.Robot;
import com.bknote71.codecraft.robocode.proxy.RobotProxy;

import java.util.HashSet;
import java.util.Set;

public class RobotClassLoader extends ClassLoader {
    private String author;
    private int specIndex;
    private String fullClassName;
    private Class<?> robotClass;

    private AwsS3ClassLoader classLoader;

    public RobotClassLoader(String author, int specIndex, String fullClassName) {
        this.author = author;
        this.fullClassName = fullClassName;
        this.specIndex = specIndex;
        classLoader = new AwsS3ClassLoader("robot-class");
    }

    public synchronized Class<?> loadClass(String name) {
        return classLoader.findClass(name);
    }

    public synchronized Class<?> loadRobotMainClass() {
        if (robotClass == null) {
            robotClass = loadClass(author + "/" + specIndex + "/" + fullClassName);
            if (robotClass == null || !Robot.class.isAssignableFrom(robotClass))
                return null;
        }

        return robotClass;
    }

    public synchronized Robot createRobotInstance() throws InstantiationException, IllegalAccessException {
        loadRobotMainClass();
        return (Robot) robotClass.newInstance();
    }

    public void cleanup() {
        classLoader = null;
        robotClass = null;
    }
}
