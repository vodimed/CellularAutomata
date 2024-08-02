package com.psiras.cellularautomata.template;

import com.psiras.cellularautomata.model.CellularModel;

public class IllnessTemplate extends CellularModel {
    private static final int range = 10;
    private static final int edge = 1;
    private static final byte[] mask;

    static {
        final float sigma = (float) (edge + 1) / 2;
        final int len = (edge << 1) + 1;
        mask = new byte[len * len];

        for (int dh = 0; dh < len; ++dh) {
            for (int dw = 0; dw < len; ++dw) {
                //final float dist = (dh - edge) * (dh - edge) + (dw - edge) * (dw - edge);
                //mask[dh * len + dw] = (float) (1.0f * Math.exp(-dist / (2 * sigma * sigma)));
                final float dist = Math.max(Math.abs(dh - edge), Math.abs(dw - edge));
                mask[dh * len + dw] = 1;//(byte) (dist <= 1.0f ? 2 : 1);
            }
        }
        mask[edge * len + edge] = 0;
    }

    public IllnessTemplate(int height, int width) {
        super(height, width, edge);
    }

    @Override
    protected byte get(int pos) {
        if (memory[pos] == wall) return wall;

        if (memory[pos] == range - 1) {
            return 0;
        } else {
            final int len = (edge << 1) + 1;
            final int src = pos - edge - edge * width;
            int neighbours = 0;

            for (int dh = 0; dh < len; ++dh) {
                for (int dw = 0; dw < len; ++dw) {
                    neighbours += memory[src + dh * width + dw] * mask[dh * len + dw];
                }
            }

            if (memory[pos] == 0) {
                if (neighbours < 5) {
                    return 0;
                } else if (neighbours < range * ((edge << 1) + 1) * ((edge << 1) + 1)) {
                    return 2;
                } else {
                    return 3;
                }
            } else {
                return (byte)Math.min(neighbours / 8 + 5, range - 1);
            }
        }
    }
}
