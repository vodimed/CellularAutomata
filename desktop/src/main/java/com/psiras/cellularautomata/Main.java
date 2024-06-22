package com.psiras.cellularautomata;

import com.psiras.cellularautomata.model.CellularModel;
import com.psiras.cellularautomata.model.ModelExecutor;
import com.psiras.cellularautomata.model.ThreadExecutor;
import com.psiras.cellularautomata.template.IllnessTemplate;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static void main(String[] args) {
        final Main main = new Main();
        main.init();
    }

    public void init() {
        final Frame frame = new Frame("CellularAutomata"); // Для окна нужна "рама" - Frame
        frame.setMinimumSize(new Dimension(480, 640)); // Размеры окна
        frame.setResizable(false);
        frame.add(new DrawView(frame));
        frame.setLocationRelativeTo(null); // Окно - в центре экрана
        frame.setVisible(true); // Делаем окно видимым
        frame.addWindowListener(new WindowAdapter() { // Завершение при закрытии окна
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ((DrawView)frame.getComponent(0)).terminate();
                frame.dispose();
            }
        });
    }

    // Canvas control
    private static class DrawView extends Canvas implements Runnable {
        private static final int scale = 2;
        private static final Stroke pencil = new BasicStroke(scale);
        private final ThreadExecutor painter = new ThreadExecutor(this, 1);
        private final ModelExecutor executor = new ModelExecutor(1.0f, 1);
        private boolean pressed = false;
        private int touchX = -1;
        private int touchY = -1;

        public DrawView(Frame frame) {
            super();
            setSize(frame.getSize());
            setBackground(Color.WHITE);
            setForeground(Color.BLUE);

            final Dimension dim = getSize();
            final int square = Math.min(dim.height, dim.width) / scale;

            addMouseListener(new MouseAdapter(){});
            addMouseMotionListener(new MouseAdapter(){});
            executor.setModel(new IllnessTemplate(square, square));
            executor.start();
            painter.start();
        }

        public void terminate() {
            painter.terminate();
            executor.terminate();
        }

        @Override
        public void paint(Graphics canvas) {
            ((Graphics2D)canvas).setStroke(pencil);
            final CellularModel model = executor.getModel();
            final int base = executor.baseline();

            for (int h = 0; h < model.height; ++h) {
                canvas.clearRect(0, h * scale, model.width * scale, scale);
                final int row = (base + h) * model.width;

                for (int w = 0; w < model.width; ++w) {
                    if (model.memory[row + w] <= 0) continue;
                    canvas.fillRect(w * scale, h * scale, scale, scale);
                }
            }
        }

        @Override
        public void run() {
            while (getGraphics() == null) Thread.yield();
            final Graphics canvas = getGraphics();

            while (painter.isActive()) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    break;
                }
                paint(canvas);
            }
        }

        @Override
        protected void processMouseEvent(MouseEvent event) {
            super.processMouseEvent(event);

            if (event.getID() == MouseEvent.MOUSE_PRESSED) {
                pressed = true;
            } else if (event.getID() == MouseEvent.MOUSE_RELEASED) {
                pressed = false;
            }
            touchX = event.getX();
            touchY = event.getY();
        }

        @Override
        protected void processMouseMotionEvent(MouseEvent event) {
            super.processMouseMotionEvent(event);
            if (pressed) {
                final float pressure = 4;
                executor.getModel().erase(event.getY() / scale, event.getX() / scale,
                        touchY / scale, touchX / scale, (int)(pressure * pressure));
                touchX = event.getX();
                touchY = event.getY();
            }
        }
    }
}