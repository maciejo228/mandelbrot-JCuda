package com.example.mandelbrotfx_v2;

import org.pcj.*;

import static java.lang.Math.sqrt;
@RegisterStorage(MandelbrotPCJ.Shared.class)
public class MandelbrotPCJ implements StartPoint{
    @Storage(MandelbrotPCJ.class)
    enum Shared{
        tab
    }
    int height, width, N;
    double minX, maxX, minY, maxY;
    public int[][] tab;
    int[][] finalTab;

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
    @Override
    public void main() {
        PCJ.barrier();
        double time = System.nanoTime();

        height = HelloController.height;
        width = HelloController.width;
        minX = HelloController.minX;
        maxX = HelloController.maxX;
        minY = HelloController.minY;
        maxY = HelloController.maxY;
        N = HelloController.N;

        int tHeight = height/PCJ.threadCount();
        tab = new int[tHeight][width];
        double  x,y;
        for(int i=0;i<tHeight;i++) {
            for(int j=0;j<width;j++) {
                x = ( (maxX-minX)*(PCJ.myId()+i*PCJ.threadCount()))/height+minX;
                y = ( (maxY-minY)*j)/width+minY;
                tab[i][j] = returnNumberOfIterations(N,x,y);

            }
        }
        PCJ.barrier();
        if(PCJ.myId() == 0) {
            int[][] tmp;
            finalTab = new int[height][width];
            for(int thread = 0; thread<PCJ.threadCount();thread++) {
                if(thread != 0) {
                    tmp = PCJ.get(thread,Shared.tab);
                } else {
                    tmp = tab;
                }
                for(int i=0;i<tHeight;i++) {
                    for(int j=0;j<width;j++) {
                        finalTab[i*PCJ.threadCount()+thread][j] = tmp[i][j];
                    }
                }
            }
            HelloController.imageTab = finalTab;
        }
        PCJ.barrier();
        time = System.nanoTime() - time;
        if(PCJ.myId() == 0) {
            HelloController.timePCJ = time;
        }
    }
}
