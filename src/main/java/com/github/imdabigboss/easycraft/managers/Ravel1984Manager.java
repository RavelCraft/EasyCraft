package com.github.imdabigboss.easycraft.managers;

import com.github.imdabigboss.easycraft.EasyCraft;
import com.github.imdabigboss.easycraft.utils.Lock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Ravel1984Manager {
    private String logPath = EasyCraft.getInstance().getDataFolder().getAbsolutePath();
    private Lock lock = new Lock();

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
            this.logData(logFile, "[" + timeFormatter.format(ZonedDateTime.now()) + "] " + data + "\n");
        }).start();
    }

    private void logData(File logFile, String data) {
        lock.lock();
        logFile.getParentFile().mkdirs();

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(logFile, true));
            out.write(data);
            out.close();
        } catch (IOException e) {
            EasyCraft.getLog().severe("Could not write to log file! " + e.getMessage());
        }
        lock.unlock();
    }
}
