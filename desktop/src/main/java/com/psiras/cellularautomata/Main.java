package com.psiras.cellularautomata;

import com.psiras.cellularautomata.model.CellularModel;
import com.psiras.cellularautomata.model.ModelExecutor;
import com.psiras.cellularautomata.model.ThreadExecutor;
import com.psiras.cellularautomata.template.IllnessTemplate;
import com.psiras.cellularautomata.utils.Bitwise;
import com.psiras.cellularautomata.utils.FFT;

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
        final byte[] r = new byte[]{0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};
        final byte[] q = new byte[]{0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1};
        FFT.fftr_(r);
        FFT.fftr(q);

//        final int rows = 7;
//        final int cols = 2;
//        for (int i = 0; i < rows; ++i) {
//            for (int j = 0; j < cols; ++j) {
//                System.out.println(String.valueOf(j * rows + i) + " = " + String.valueOf(i * cols + j));
//            }
//        }

        final int n = 16;
        int prev = n;
        for (int i = 0; i < n; ++i) {
            prev = Bitwise.tree_next(n - 1, n / 2, prev);
            System.out.println(i + " " + prev);
        }

        //final Main main = new Main();
        //main.init();
    }

    public void init() {
        final Frame frame = new Frame("CellularAutomata"); // Для окна нужна "рама" - Frame
        frame.setMinimumSize(new Dimension(512 + 2, 768 + 2)); // Размеры окна
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
            addMouseListener(new MouseAdapter(){});
            addMouseMotionListener(new MouseAdapter(){});

            final Dimension dim = getSize();
            final int square = Bitwise.rndpow2(Math.min(dim.height, dim.width) / scale) >>> 1;
            executor.setModel(new IllnessTemplate(square, square));
            executor.start();
            painter.start();
        }

        public void terminate() {
            painter.terminate();
            executor.terminate();
        }

        private void paint_snapshot(Graphics canvas, byte[] snapshot, int height, int width, final int base) {
            final int margin = (getWidth() - width * scale) >>> 1;

            for (int h = 0; h < height; ++h) {
                canvas.clearRect(margin, margin + h * scale, width * scale, scale);
                final int row = (base + h) * width;

                for (int w = 0; w < width; ++w) {
                    if (snapshot[row + w] <= 0) continue;
                    canvas.fillRect(margin + w * scale, margin + h * scale, scale, scale);
                }
            }
        }

        @Override
        public void paint(Graphics canvas) {
            final CellularModel model = executor.getModel();
            paint_snapshot(canvas, model.memory, model.height, model.width, executor.baseline());
        }

        @Override
        public void run() {
            while (getGraphics() == null) Thread.yield();
            final Graphics canvas = getGraphics();
            ((Graphics2D)canvas).setStroke(pencil);

            while (painter.isActive()) {
                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    break;
                }
                final byte[] snapshot = executor.snapshot();
                final CellularModel model = executor.getModel();
                paint_snapshot(canvas, snapshot, model.height, model.width, 0);
                //paint(canvas);
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