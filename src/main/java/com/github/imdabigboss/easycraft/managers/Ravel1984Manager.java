package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.Lock;
import com.google.common.collect.Multimap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Ravel1984Manager {
    private String logPath = EasyCraft.getInstance().getDataFolder().getAbsolutePath();
    private final Queue<LogData> logDataQueue = new LinkedList<>();
    private long lastLogTime = 0;
    private final Lock lock = new Lock();

    public void setLogPath(String path) {
        this.logPath = path;
    }

    public String getLogPath() {
        return this.logPath;
    }

    public void logData(String dataType, String data, UUID uuid) {
        new Thread(() -> {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            File logFile = new File(logPath + "/" + dateFormatter.format(ZonedDateTime.now()) + "/" + uuid.toString() + "/" + dataType + ".txt");
            String logData = "[" + timeFormatter.format(ZonedDateTime.now()) + "] " + data + "\n";

            lock.lock();
            logDataQueue.add(new LogData(logFile, logData));
            lock.unlock();

            lock.lock();
            boolean condition = logDataQueue.size() > 900 || System.currentTimeMillis() > lastLogTime + (60 * 1000);
            lock.unlock();
            if (condition) {
                this.flushCache();
            }
        }).start();
    }

    public void flushCache() {
        lock.lock();
        lastLogTime = System.currentTimeMillis();
        int size = logDataQueue.size();
        lock.unlock();

        Map<File, List<String>> logDataMap = new HashMap<>();

        for (int i = 0; i < size; i++) {
            lock.lock();
            LogData logData = logDataQueue.poll();
            lock.unlock();

            logDataMap.computeIfAbsent(logData.file, k -> new ArrayList<>()).add(logData.data);
        }

        for (Map.Entry<File, List<String>> entry : logDataMap.entrySet()) {
            File file = entry.getKey();
            List<String> data = entry.getValue();

            try {
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                for (String line : data) {
                    writer.write(line);
                }
                writer.close();
            } catch (IOException e) {
                EasyCraft.getLog().severe("Could not write to log file! " + e.getMessage());
            }
        }
    }

    private static class LogData {
        public File file;
        public String data;

        public LogData(File file, String data) {
            this.file = file;
            this.data = data;
        }
    }
}
