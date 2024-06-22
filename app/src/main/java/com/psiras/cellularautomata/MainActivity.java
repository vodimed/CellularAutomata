package com.psiras.cellularautomata;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.psiras.cellularautomata.model.CellularModel;
import com.psiras.cellularautomata.model.ModelExecutor;
import com.psiras.cellularautomata.model.ThreadExecutor;
import com.psiras.cellularautomata.template.IllnessTemplate;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }

    @Override
    protected void onDestroy() {
        final ViewGroup layout = findViewById(android.R.id.content);
        ((DrawView)layout.getChildAt(0)).terminate();
        super.onDestroy();
    }

    // SurfaceView control
    static class DrawView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
        public static final int scale = 1;
        private static final Paint pencil = new Paint();
        private final ThreadExecutor painter = new ThreadExecutor(this, 1);
        private final ModelExecutor executor = new ModelExecutor(1.0f, 1);
        private final float[] array = new float[1024 * 2];
        private volatile boolean background = true;
        private final SurfaceHolder holder;
        private float touchX = -1.0f;
        private float touchY = -1.0f;

        static {
            pencil.setColor(Color.BLUE);
            pencil.setStrokeWidth(scale);
        }

        public DrawView(Context context) {
            super(context);
            this.holder = getHolder();
            holder.addCallback(this);
        }

        public void terminate() {
            painter.terminate();
            executor.terminate();
        }

        @Override
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            setWillNotDraw(false);

            if (!executor.isActive()) {
                final Rect frame = holder.getSurfaceFrame();
                final int height = Math.abs(frame.bottom - frame.top);
                final int width = Math.abs(frame.right - frame.left);
                final int square = Math.min(height, width) / scale;
                executor.setModel(new IllnessTemplate(square, square));
                executor.start();
            }

            if (!painter.isActive()) {
                painter.start();
            }

            background = false;
            synchronized (this) {
                notify();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            background = true;
        }

        protected void paint(Canvas canvas) {
            canvas.drawARGB(255, 255, 255, 255);
            final CellularModel model = executor.getModel();
            final int base = executor.baseline();
            int count = 0;

            for (int h = 0; h < model.height; ++h) {
                final int row = (base + h) * model.width;

                for (int w = 0; w < model.width; ++w) {
                    if (model.memory[row + w] <= 0) continue;
                    array[count++] = w * scale;
                    array[count++] = h * scale;

                    if (count >= array.length) {
                        canvas.drawPoints(array, 0, count, pencil);
                        count = 0;
                    }
                }
            }
            canvas.drawPoints(array, 0, count, pencil);
        }

        @Override
        public void run() {
            while (painter.isActive()) {
                if (background) synchronized (this) {
                    if (background) try {
                        wait();
                        continue;
                    } catch (InterruptedException e) {
                        break;
                    }
                } else try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    break;
                }

                final Canvas canvas;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    //canvas = holder.lockHardwareCanvas();
                    canvas = holder.lockCanvas(null);
                } else {
                    canvas = holder.lockCanvas(null);
                }

                if (canvas != null) {
                    paint(canvas);
                    holder.unlockCanvasAndPost(canvas);
                } else {
                    surfaceDestroyed(holder);
                }
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            //super.dispatchTouchEvent(event);
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                touchX = event.getX();
                touchY = event.getY();
            } else {
                final float pressure = 20 * event.getSize();
                executor.getModel().erase((int)(event.getY() / scale), (int)(event.getX() / scale),
                        (int)(touchY / scale), (int)(touchX / scale), (int)(pressure * pressure));
                touchX = event.getX();
                touchY = event.getY();
            }
            return true;
        }
    }
}