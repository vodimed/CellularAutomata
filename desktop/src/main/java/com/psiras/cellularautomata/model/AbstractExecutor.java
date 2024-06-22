package com.psiras.cellularautomata.model;

public abstract class AbstractExecutor implements Runnable {
    public static final int numproc = Runtime.getRuntime().availableProcessors();
    private final Thread[] threadpool;
    protected volatile boolean active = false;

    public AbstractExecutor(int numthreads) {
        this.threadpool = new Thread[Math.max(numthreads, 1)];

        for (int i = 0; i < threadpool.length; ++i) {
            threadpool[i] = new Thread(this);
            threadpool[i].setPriority(Thread.MAX_PRIORITY);
            threadpool[i].setDaemon(true);
        }
    }

    public AbstractExecutor(float power, int reserved) {
        this((int)(power * numproc) - Math.max(reserved, 0));
    }

    public void start() {
        if (!active) {
            this.active = true;
            for (Thread thread : threadpool) thread.start();
        }
    }

    public void terminate() {
        if (active) try {
            this.active = false;
            for (Thread thread : threadpool) thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isActive() {
        return active;
    }
}
