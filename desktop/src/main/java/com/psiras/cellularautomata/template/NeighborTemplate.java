package com.psiras.cellularautomata.template;

import com.psiras.cellularautomata.model.CellularModel;

public class NeighborTemplate extends CellularModel {
    private static final int edge = 1;
    private final float alpha = 3.0f;
    private final float beta = 2.0f;
    private final float gamma = 1.0f;
    private final float[] a;
    private final float[] b;
    private final float[] c;

    public NeighborTemplate(int height, int width) {
        super(height, width, edge);
        this.a = new float[vertical * width];
        this.b = new float[vertical * width];
        this.c = new float[vertical * width];
        for (int i = 0; i < a.length; ++i) {
            a[i] = (float)Math.random();
            b[i] = (float)Math.random();
            c[i] = (float)Math.random();
        }
    }

    @Override
    protected byte get(int pos) {
        if (memory[pos] == wall) return wall;
        final int dest = pos_set(pos);

        final float c_a = neighborhood(a, pos);
        final float c_b = neighborhood(b, pos);
        final float c_c = neighborhood(c, pos);

        a[dest] = transition(c_a, c_b, c_c, alpha, gamma);
        b[dest] = transition(c_b, c_c, c_a, beta, alpha);
        c[dest] = transition(c_c, c_a, c_b, gamma, beta);

        return (byte)a[dest];
    }

    private float neighborhood(float[] matrix, int pos) {
        final int len = (edge << 1) + 1;
        final int src = pos - edge - edge * width;
        float neighbours = 0;

        for (int dh = 0; dh < len; ++dh) {
            for (int dw = 0; dw < len; ++dw) {
                neighbours += matrix[src + dh * width + dw];
            }
        }
        return neighbours / 9.0f;
    }

    private float transition(float a, float b, float c, float factor1, float factor2) {
        final float value = a * (1 + factor1 * b - factor2 * c);
        return Math.max(0, Math.min(1, value));
    }
}
