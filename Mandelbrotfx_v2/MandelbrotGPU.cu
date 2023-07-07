__device__ int returnNumberOfIterations(int n, double x, double y) {
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
extern "C"
__global__ void mandelbrot(int *tab, int *ints, double *doubles) {

    int height = ints[0];
    int width = ints[1];

    int i = blockIdx.x * blockDim.x + threadIdx.x;

    if (i >= height * width) return;

    int N = ints[2];

    double minX = doubles[0];
    double maxX = doubles[1];
    double minY = doubles[2];
    double maxY = doubles[3];
    double  x,y;

    int row = i / width;
    int col = i % width;

    x = ((maxX - minX) * row) / height + minX;
    y = ((maxY - minY) * col) / width + minY;

    tab[i] = returnNumberOfIterations(N,x,y);
}