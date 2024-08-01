package com.psiras.cellularautomata.template;

import com.psiras.cellularautomata.model.CellularModel;

// See: http://ru.wikipedia.org/wiki/Реакция_Белоусова_—_Жаботинского
// Брюсселятор: Простейшая модель, предложенная Пригожиным, которая имеет колебательную динамику.
// Орегонатор: Модель не способна на сложные типы колебаний, сложнопериодические и хаотические.
// Расширенный о.: Модель сложнопериодического и хаотического поведения. Хаос получить не удалось.
public class OregonatorTemplate extends CellularModel {
//    private final float[] k = new float[5]{};
//    private final float[] X; // HBrO2
//    private final float[] Y; // Br–1
//    private final float[] Z; // Се4+

    protected OregonatorTemplate(int height, int width, int edge, byte wall) {
        super(height, width, edge, wall);
    }

    @Override
    protected byte get(int pos) {
        return 0;
    }
}
