package com.psiras.cellularautomata.model;

import java.util.concurrent.atomic.AtomicInteger;

public class ModelExecutor extends AbstractExecutor {
    private final AtomicInteger line = new AtomicInteger(0);
    private CellularModel model = null;

    public ModelExecutor(float power, int reserved) {
        super(power, reserved);
    }

    public void setModel(CellularModel model) {
        this.model = model;
    }

    public CellularModel getModel() {
        return model;
    }


    public int baseline() {
        final int base = model.frame(line.get(), -1);
        return (base / model.height) * model.height;
    }

    @Override
    public void run() {
        while (active) {
            final int local = line.getAndIncrement();
            model.calculate(local % model.vertical);

            if (local % (model.vertical * 1000) == 0) {
                System.out.println(local);
            }
        }
    }
}
