package com.psiras.cellularautomata.model;

public abstract class CellularModel {
    protected static final byte wall = -1;
    protected static final byte fr2n = 3;
    protected final int edge;
    protected final int vertical;
    public final byte[] memory;
    public final int height;
    public final int width;

    protected CellularModel(int height, int width, int edge) {
        this.edge = edge;
        this.vertical = (height << fr2n);
        this.memory = new byte[vertical * width];
        this.height = height;
        this.width = width;

        for (int i = 0; i < vertical * width; ++i) {
            memory[i] = (byte)(Math.random() * 127);
        }
    }

    protected abstract byte get(int pos);

    protected int pos_set(int pos) {
        return (pos + height * width) % memory.length;
    }

    public void calculate(int line) {
        final int h = line % height;
        final boolean body = ((h >= edge) && (h < height - edge));
        final int dst = line * width;
        final int src = frame(line, -1) * width;

        if (body) for (int w = edge; w < width - edge; ++w) {
            memory[dst + w] = get(src + w);
        } else for (int w = 0; w < width; ++w) {
            memory[dst + w] = wall;
        }
        for (int w = 0; w < edge; ++w) {
            memory[dst + w] = wall;
            memory[dst + width - w - 1] = wall;
        }
    }

    public int frame(int line, int step) {
        return (line + step * height + vertical) % vertical;
    }

    public void erase(int y0, int x0, int y1, int x1, int radius) {
        final float dy = (y1 - y0);
        final float dx = (x1 - x0);

        if ((y0 < height) && (y1 < height) && (x0 < width) && (x1 < width)) {
            for (int h = Math.max(y0 - radius, 0); h < Math.min(y0 + 1 + radius, height); ++h) {
                final int src = frame(h, -1) * width;
                final int dst = h * width;
                final float py = (h - y0);

                for (int w = Math.max(x0 - radius, 0); w < Math.min(x0 + 1 + radius, width); ++w) {
                    final float px = (w - x0);
                    float c = (px * dx + py * dy);
                    c = (c > 1 ? 1 : 0);

                    final float ddy = c * dy - py;
                    final float ddx = c * dx - px;
                    if (Math.sqrt(ddy * ddy + ddx * ddx) < radius) {
                        memory[dst + w] = 0;
                        memory[src + w] = 0;
                    }
                }
            }
        }
    }
}
