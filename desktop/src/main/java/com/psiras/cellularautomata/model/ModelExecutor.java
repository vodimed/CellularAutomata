package com.psiras.cellularautomata.model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelExecutor extends AbstractExecutor {
    private final AtomicInteger line = new AtomicInteger(0);
    private CellularModel model = null;
    private byte[] buff = new byte[0];

    public ModelExecutor(float power, int reserved) {
        super(power, reserved);
    }

    public void setModel(CellularModel model) {
        this.model = model;

        final int size = model.height * model.width;
        if (buff.length != size) buff = new byte[size];
    }

    public CellularModel getModel() {
        return model;
    }

    public int baseline() {
        final int base = model.frame(line.get(), -1);
        return (base / model.height) * model.height;
    }

    public byte[] snapshot() {
        final int base = baseline() * model.width;
        System.arraycopy(model.memory, base, buff, 0, buff.length);
        return buff;
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
