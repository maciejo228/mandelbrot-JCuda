package com.example.mandelbrotfx_v2;

import static java.lang.Math.sqrt;

public class Mandelbrot {

    int returnNumberOfIterations(int n, double x, double y) {
        double zIM, zRE, tmp;
        zIM = zRE = 0;
        for(int i=0; i<n; i++) {
            if (sqrt(zRE * zRE + zIM * zIM) >= 2) {
                return i;
            }
            tmp = zRE * zRE - zIM * zIM + x;
            zIM = zIM * zRE + zIM * zRE + y;
            zRE = tmp;
        }
        return n;
    }

    public int[][] mandelbrot(int height, int width, int N, double minX, double maxX, double minY, double maxY) {
        double x, y;
        int[][] tab = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                x = ((maxX - minX) * i) / height + minX;
                y = ((maxY - minY) * j) / width + minY;
                tab[i][j] = returnNumberOfIterations(N, x, y);
            }
        }
        return tab;
    }
}
