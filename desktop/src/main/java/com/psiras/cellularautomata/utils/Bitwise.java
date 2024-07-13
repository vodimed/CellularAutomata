package com.psiras.cellularautomata.utils;

// See also: http://graphics.stanford.edu/~seander/bithacks.html
public class Bitwise {
    private static final int[] log32 = new int[]{0, 9, 1, 10, 13, 21, 2, 29, 11, 14, 16, 18,
            22, 25, 3, 30, 8, 12, 20, 28, 15, 17, 24, 7, 19, 27, 23, 6, 26, 5, 4, 31};

    // Traversal of FFT binary tree in depth (init: i = n = decn + 1 = 2 * half)
    // 0,16,8,4,2,1,17,18,9,25,20,10,5,21,26,13,29,24,12,6,3,19,22,11,27,28,14,7,23,30,15,31
    // 0:  0: 00000             16: 29: 11101 -
    // 1: 16: 10000             17: 24: 11000 ---------
    // 2:  8: 01000 ---------   18: 12: 01100 ------
    // 3:  4: 00100 ------      19:  6: 00110 ---
    // 4:  2: 00010 ---         20:  3: 00011 -
    // 5:  1: 00001 -           21: 19: 10011 -
    // 6: 17: 10001 -           22: 22: 10110 ---
    // 7: 18: 10010 ---         23: 11: 01011 -
    // 8:  9: 01001 -           24: 27: 11011 -
    // 9: 25: 11001 -           25: 28: 11100 ------
    //10: 20: 10100 ------      26: 14: 01110 ---
    //11: 10: 01010 ---         27:  7: 00111 -
    //12:  5: 00101 -           28: 23: 10111 -
    //13: 21: 10101 -           29: 30: 11110 ---
    //14: 26: 11010 ---         30: 15: 01111 -
    //15: 13: 01101 -           31: 31: 11111 -
    public static int tree_next(final int decn, final int half, int i) {
        if ((i & 0x1) == 0) return (i >>> 1);
        if ((i & half) == 0) return (i | half);
        do i <<= 1; while ((i & half) != 0);
        return (i & decn | half);
    }

    // Traversal of FFT binary tree level by level (OEIS sequence A030109)
    public static int oeisA030109(int n, int i) {
        return (dimreverse(n + i) - 1) >> 1;
    }

    // Trailing 0s: Calculate i & (~i + 1) and use the result as a lookup in a table with 32
    // entries. 1 means zero, 2 means one, 4 means two, and so on, except that 0 means 32 0s
    public static int trailing(int value) {
        value &= (~value + 1);
        return log2(value);
    }

    // Reverse bits of a number of 32-bit
    public static int reverse(int value) {
        value = (((value & 0xaaaaaaaa) >> 1) | ((value & 0x55555555) << 1));
        value = (((value & 0xcccccccc) >> 2) | ((value & 0x33333333) << 2));
        value = (((value & 0xf0f0f0f0) >> 4) | ((value & 0x0f0f0f0f) << 4));
        value = (((value & 0xff00ff00) >> 8) | ((value & 0x00ff00ff) << 8));
        return ((value >> 16) | (value << 16));
    }

    // Reverse actual bits of a number of 32-bit
    public static int dimreverse(int value) {
        final int gap = 31 - log2(value);
        value = (((value & 0xaaaaaaaa) >> 1) | ((value & 0x55555555) << 1));
        value = (((value & 0xcccccccc) >> 2) | ((value & 0x33333333) << 2));
        value = (((value & 0xf0f0f0f0) >> 4) | ((value & 0x0f0f0f0f) << 4));
        value = (((value & 0xff00ff00) >> 8) | ((value & 0x00ff00ff) << 8));
        value = ((value >> 16) | (value << 16));
        return (value >>> gap);
    }

    // Invert actual bits of a number of 32-bit
    public static int invert(int value) {
        int mask = value;
        mask |= (mask >> 1);
        mask |= (mask >> 2);
        mask |= (mask >> 4);
        mask |= (mask >> 8);
        mask |= (mask >> 16);
        return value ^ mask;
    }

    public static int log2(int value) {
        value |= (value >> 1);
        value |= (value >> 2);
        value |= (value >> 4);
        value |= (value >> 8);
        value |= (value >> 16);
        return log32[(int)((value * 0x07c4acddL & 0xFFFFFFFFL) >> 27)];
    }

    // Compute the next highest power of 2 of 32-bit
    public static int rndpow2(int value) {
        value--;
        value |= (value >> 1);
        value |= (value >> 2);
        value |= (value >> 4);
        value |= (value >> 8);
        value |= (value >> 16);
        value++;
        return value;
    }

    // Transposition of 2-row rectangular matrix
    private static void transp2(Complex[] x, int start, int step, int count) {
        final int halfgap = step * (count >>> 1);
        for (int item = start; item < start + halfgap; item += (step << 1)) {
            final Complex swap = x[item + step];
            x[item + step] = x[halfgap + item];
            x[halfgap + item] = swap;
        }
        if (count > 2) {
            transp2(x, start, step, (count >>> 1));
            transp2(x, start + halfgap, step, (count >>> 1));
        }
    }
}
