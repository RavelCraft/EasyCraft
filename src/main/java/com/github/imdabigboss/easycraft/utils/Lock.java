package com.github.imdabigboss.easycraft.utils;

import com.github.imdabigboss.easycraft.EasyCraft;

public class Lock {
    private boolean isLocked = false;

    public synchronized void lock() {
        try {
            while (isLocked) {
                wait();
            }
            isLocked = true;
        } catch (InterruptedException e) {
            EasyCraft.getLog().severe("Lock exception: " + e.getMessage());
        }
    }

    public synchronized void unlock() {
        isLocked = false;
        notify();
    }
}
