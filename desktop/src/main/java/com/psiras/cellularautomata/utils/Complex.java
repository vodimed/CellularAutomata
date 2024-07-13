package com.psiras.cellularautomata.utils;

public class Complex {
    private static final double e = 2.71828182845904523536;
    private final byte re; // the real part
    private final byte im; // the imaginary part

    public Complex(byte real, byte imag) {
        this.re = real;
        this.im = imag;
    }

    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    public byte re() {
        return re;
    }

    public byte im() {
        return im;
    }

    public float abs() {
        return (float)Math.sqrt(re * re + im * im);
    }

    public float phase() {
        return (float)Math.atan2(im, re);
    }

    public Complex plus(Complex that) {
        return new Complex((byte)(this.re + that.re), (byte)(this.im + that.im));
    }

    public Complex minus(Complex that) {
        return new Complex((byte)(this.re - that.re), (byte)(this.im - that.im));
    }

    public Complex times(Complex that) {
        final byte real = (byte)(this.re * that.re - this.im * that.im);
        final byte imag = (byte)(this.re * that.im + this.im * that.re);
        return new Complex(real, imag);
    }

    public Complex scale(float alpha) {
        return new Complex((byte)(alpha * re), (byte)(alpha * im));
    }

    public Complex conjugate() {
        return new Complex(re, (byte)(-im));
    }

    public Complex reciprocal() {
        final byte scale = (byte)(re * re + im * im);
        return new Complex((byte)(re / scale), (byte)(-im / scale));
    }

    public Complex divides(Complex that) {
        return this.times(that.reciprocal());
    }

    public Complex exp() {
        return new Complex((byte)(exp(re) * Math.cos(im)), (byte)(exp(re) * Math.sin(im)));
    }

    public Complex sin() {
        return new Complex((byte)(Math.sin(re) * Math.cosh(im)), (byte)(Math.cos(re) * Math.sinh(im)));
    }

    public Complex cos() {
        return new Complex((byte)(Math.cos(re) * Math.cosh(im)), (byte)(-Math.sin(re) * Math.sinh(im)));
    }

    public Complex tan() {
        return sin().divides(cos());
    }

    private static double exp(int exp) {
        return pow(e, exp);
    }

    private static double pow(double base, int exp) {
        int result = 1;
        for (;;) {
            if ((exp & 1) != 0) result *= base;
            exp >>= 1;
            if (exp == 0) return result;
            base *= base;
        }
    }
}
