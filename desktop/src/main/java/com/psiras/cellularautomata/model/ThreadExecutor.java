package com.psiras.cellularautomata.model;

public class ThreadExecutor extends AbstractExecutor {
    private final Runnable loop;

    public ThreadExecutor(Runnable loop, int numthreads) {
        super(numthreads);
        this.loop = loop;
    }

    @Override
    public void run() {
        loop.run();
    }
}
