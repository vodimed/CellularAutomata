package com.psiras.cellularautomata.template;

import com.psiras.cellularautomata.model.CellularModel;

public class NeighborTemplate extends CellularModel {
    private static final int edge = 1;

    protected NeighborTemplate(int height, int width) {
        super(height, width, edge);
    }

    @Override
    protected byte get(int pos) {
        if (memory[pos] == wall) return wall;

        return 0;
    }
}
