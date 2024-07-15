package com.psiras.cellularautomata.utils;

public class Complex {
    private static final double e = Math.exp(1);
    private final float re; // the real part
    private final float im; // the imaginary part

    public Complex(float real, float imag) {
        this.re = real;
        this.im = imag;
    }

    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    public float re() {
        return re;
    }

    public float im() {
        return im;
    }

    public float abs() {
        return (float)Math.sqrt(re * re + im * im);
    }

    public float abs2() {
        return (re * re + im * im);
    }

    public float phase() {
        return (float)Math.atan2(im, re);
    }

    public Complex plus(Complex that) {
        return new Complex(this.re + that.re, this.im + that.im);
    }

    public Complex minus(Complex that) {
        return new Complex(this.re - that.re, this.im - that.im);
    }

    public Complex times(Complex that) {
        final float real = (this.re * that.re - this.im * that.im);
        final float imag = (this.re * that.im + this.im * that.re);
        return new Complex(real, imag);
    }

    public Complex scale(float alpha) {
        return new Complex(alpha * re, alpha * im);
    }

    public Complex conjugate() {
        return new Complex(re, -im);
    }

    public Complex reciprocal() {
        final float scale = (re * re + im * im);
        return new Complex(re / scale, -im / scale);
    }

    public Complex divides(Complex that) {
        return this.times(that.reciprocal());
    }

    public Complex exp() {
        return new Complex((float)(Math.exp(re) * Math.cos(im)), (float)(Math.exp(re) * Math.sin(im)));
    }

    public Complex sin() {
        return new Complex((float)(Math.sin(re) * Math.cosh(im)), (float)(Math.cos(re) * Math.sinh(im)));
    }

    public Complex cos() {
        return new Complex((float)(Math.cos(re) * Math.cosh(im)), (float)(-Math.sin(re) * Math.sinh(im)));
    }

    public Complex tan() {
        return sin().divides(cos());
    }
}
