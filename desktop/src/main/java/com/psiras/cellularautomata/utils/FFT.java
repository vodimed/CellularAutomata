package com.psiras.cellularautomata.utils;

import java.util.Arrays;

public class FFT {
    private static final Complex ZERO = new Complex((byte)0, (byte)0);

    // Do not instantiate
    private FFT() {
    }

//    public static void fftr(byte[] x) {
//        Complex[] z = new Complex[x.length];
//
//        for (int i = 0; i < x.length; ++i)
//            z[i] = new Complex(x[i], (byte)0);
//
//        fft_kernel(z, z, 0, 1, z.length, null);
//
//        for (int i = 0; i < x.length; ++i)
//            x[i] = z[i].re();
//    }

//    public static Complex[] fftn(Complex[] x, int... dim) {
//        for (int d = 0; d < dimlength.length; ++d) {
//        }
//    }
//
//    public static Complex[] fft2(Complex[] x, int width) {
//        final int height = x.length / width;
//        if (height * width != x.length) throw new IllegalArgumentException("Dimensions don't agree");
//        final Complex[] z = new Complex[x.length];
//
//        for (int h = 0; h < height; ++h) {
//        }
//    }

    public static Complex[] fft(Complex[] x) {
        fft_kernel(x, x, 0, 1, x.length, null);
        return x;
    }

    public static Complex[] ifft(Complex[] x) {
        ifft_kernel(x, x, 0, 1, x.length, null);
        return x;
    }

    public static Complex[] convolve(Complex[] x, Complex[] y, boolean padzeroes) {
        if (padzeroes) x = convolve_array(x);
        if (padzeroes) y = convolve_array(y);
        convolve_kernel(x, y, 0, 1, x.length, null);
        return y;
    }

    public static Complex[] fft_stack(int count) {
        return new Complex[Bitwise.log2(count >>> 2) + 1];
    }

    private static Complex[] convolve_array(Complex[] x) {
        final Complex[] wider = new Complex[x.length << 1];
        System.arraycopy(x, 0, wider, 0, x.length);
        Arrays.fill(wider, x.length, (x.length << 1), ZERO);
        return wider;
    }

    protected static void convolve_kernel(Complex[] x, Complex[] y, int start, int step, int count, Complex[] stack) {
        if (y.length != x.length) throw new IllegalArgumentException("Dimensions don't agree");
        if (stack == null) stack = fft_stack(count);

        // compute FFT of each sequence
        fft_kernel(x, x, start, step, count, stack);
        fft_kernel(y, y, start, step, count, stack);

        // point-wise multiply
        for (int i = start; i < count; i += step) {
            y[i] = y[i].times(x[i]);
        }
        ifft_kernel(y, y, start, step, count, stack);
    }

    protected static void ifft_kernel(Complex[] x, Complex[] y, int start, int step, int count, Complex[] stack) {
        if (y.length != x.length) throw new IllegalArgumentException("Dimensions don't agree");
        final float div = 1.0f / count;

        // take conjugate
        for (int i = start; i < count; i += step) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        fft_kernel(y, y, start, step, count, stack);

        // take conjugate again
        for (int i = start; i < count; i += step) {
            y[i] = y[i].conjugate().scale(div);
        }
    }

    protected static void fft_kernel(Complex[] x, Complex[] y, int start, int step, int count, Complex[] stack) {
        if (y.length != x.length) throw new IllegalArgumentException("Dimensions don't agree");
        if (count == 1) {y[0] = x[0]; return;}
        if ((count % 2) != 0) throw new IllegalArgumentException("n is not a power of 2");

        if (stack == null) stack = fft_stack(count);
        final int half = (count >>> 1);

        fft_kernel(x, y, start, step << 1, half, stack); // even terms
        fft_kernel(x, y, start + step, step << 1, half, stack); // odd terms

        // combine
        final int decn = (count - 1);
        final int dech = (half - 1);
        final double th = -Math.PI / half;
        final int halfgap = step * half;
        Complex parent = y[start];

        for (int it = count; it != decn; it = Bitwise.tree_next(decn, half, it)) {
            final int level = Bitwise.trailing(it & dech);
            final int i = (it & decn);

            if (i < half) {
                final double ith = i * th;
                final int position = i * step;

                final int k = start + (position << 1) + step;
                final int n = start + position;

                final Complex wi = new Complex((byte)Math.cos(ith), (byte)Math.sin(ith)).times(y[k]);
                final Complex yk = parent;

                stack[level] = y[n + halfgap];
                y[n + halfgap] = yk.minus(wi);

                parent = y[n];
                y[n] = yk.plus(wi);
            } else {
                parent = stack[level];
            }
        }
    }

//    @Deprecated
//    private static Complex[] fft_original(Complex[] x) {
//        final int n = x.length;
//        if (n == 1) return new Complex[]{x[0]};
//        if ((n % 2) != 0) throw new IllegalArgumentException("n is not a power of 2");
//
//        // fft of even terms
//        final Complex[] even = new Complex[n >>> 1];
//        for (int k = 0; k < (n >>> 1); ++k) {
//            even[k] = x[k << 1];
//        }
//        final Complex[] q = fft_original(even);
//
//        // fft of odd terms
//        final Complex[] odd = even; // reuse the array
//        for (int k = 0; k < (n >>> 1); ++k) {
//            odd[k] = x[(k << 1) + 1];
//        }
//        final Complex[] r = fft_original(odd);
//
//        // combine
//        final Complex[] y = new Complex[n];
//        final double th = -2 * Math.PI / n;
//        final int halfgap = (n >>> 1);
//
//        for (int k = 0; k < (n >>> 1); ++k) {
//            final double kth = k * th;
//            final Complex wk = new Complex((byte)Math.cos(kth), (byte)Math.sin(kth)).times(r[k]);
//
//            y[k + halfgap] = q[k].minus(wk);
//            y[k] = q[k].plus(wk);
//        }
//        return y;
//    }
}
