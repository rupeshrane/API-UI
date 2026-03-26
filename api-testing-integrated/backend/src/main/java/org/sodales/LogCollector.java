package org.sodales;

import java.util.ArrayList;
import java.util.List;

public class LogCollector {

    private static final List<String> logs = new ArrayList<>();

    public static synchronized void log(String message) {
        System.out.println(message); // keep console log
        logs.add(message);
    }

    public static synchronized List<String> getLogs() {
        return new ArrayList<>(logs);
    }

    public static synchronized void clear() {
        logs.clear();
    }
}