package com.example.mandelbrotfx_v2;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import org.pcj.PCJ;

import jcuda.*;
import jcuda.driver.*;
import jcuda.utils.*;
import static jcuda.driver.JCudaDriver.*;
import static jcuda.driver.CUdevice_attribute.*;

public class HelloController {
    @FXML
    TextField w,h,minx,maxx,miny,maxy,n,t,blockSize;
    public static int height, width, N, threads;
    public static double minX, maxX, minY, maxY;
    File nodes;
    Color[] colors;
    public int[] blocks, grids;
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public ImageView imageView;
    @FXML
    public Label pcjTimeLabel, gpuTimeLabel, TimeLabel;
    public static double timePCJ;
    public static int[][] imageTab;
    WritableImage img;
    PixelWriter pw;

    boolean compare = false;

    public void initialize() {
        blocks = new int[2];
        grids = new int[2];
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        scrollPane.setContent(null);
        scrollPane.setContent(imageView);
        imageView.setFocusTraversable(true);
    }

    void setImage() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pw.setColor(i,j,colors[imageTab[i][j]]);
            }
        }
        imageView.setImage(img);
    }

    Color[] generateColors(int iter) {
        Color[] colors = new Color[iter+1];
        colors[iter] = Color.rgb(0,0,0);
        int red = 0, green = 0, blue = 0;
        for (int i = 0; i < iter; i++)
        {
            red = 13*(iter-i) % 256;
            green = 7*(iter-i) % 256;
            blue = 11*(iter-i) % 256;
            colors[i] = Color.rgb(red,green,blue);
        }
        return colors;
    }

    public int[][] mandelbrot() {
        if(h.getText() == "" || w.getText() == "" || minx.getText() == "" || maxx.getText() == "" || miny.getText() == ""|| maxy.getText() == ""|| n.getText() == "") {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Incorrect input data");
            alert.showAndWait();
            return null;
        }
        height = Integer.parseInt(h.getText());
        width = Integer.parseInt(w.getText());
        N = Integer.parseInt(n.getText());
        minX = Double.parseDouble(minx.getText());
        maxX = Double.parseDouble(maxx.getText());
        minY = Double.parseDouble(miny.getText());
        maxY = Double.parseDouble(maxy.getText());
        imageTab = null;
        imageView.setImage(null);
        img = null;
        pw = null;
        img = new WritableImage(height,width);
        pw = img.getPixelWriter();

        colors = generateColors(N);

        imageTab = new int[height][width];

        Mandelbrot m = new Mandelbrot();
        double time = System.nanoTime();
        imageTab = m.mandelbrot(height,width,N,minX,maxX,minY,maxY);
        if(compare)
            return imageTab;
        time = System.nanoTime() - time;
        int timeVal = (int)Math.floor(time * 1.0E-9);
        String output = String.format(Locale.US,"%02d:%02d:%02.3f",timeVal/3600,timeVal/60,(time * 1.0E-9)%60);
        TimeLabel.setText(output);

        setImage();
        return null;
    }
    private static String createString(byte bytes[])
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++)
        {
            char c = (char)bytes[i];
            if (c == 0)
            {
                break;
            }
            sb.append(c);
        }
        return sb.toString();
    }
    private static String getAttributeDescription(int attribute)
    {
        switch (attribute)
        {
            case CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK:
                return "Maximum number of threads per block";
            case CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_X:
                return "Maximum x-dimension of a block";
            case CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Y:
                return "Maximum y-dimension of a block";
            case CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Z:
                return "Maximum z-dimension of a block";
            case CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X:
                return "Maximum x-dimension of a grid";
            case CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Y:
                return "Maximum y-dimension of a grid";
            case CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Z:
                return "Maximum z-dimension of a grid";
            case CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK:
                return "Maximum shared memory per thread block in bytes";
            case CU_DEVICE_ATTRIBUTE_TOTAL_CONSTANT_MEMORY:
                return "Total constant memory on the device in bytes";
            case CU_DEVICE_ATTRIBUTE_WARP_SIZE:
                return "Warp size in threads";
            case CU_DEVICE_ATTRIBUTE_MAX_PITCH:
                return "Maximum pitch in bytes allowed for memory copies";
            case CU_DEVICE_ATTRIBUTE_MAX_REGISTERS_PER_BLOCK:
                return "Maximum number of 32-bit registers per thread block";
            case CU_DEVICE_ATTRIBUTE_CLOCK_RATE:
                return "Clock frequency in kilohertz";
            case CU_DEVICE_ATTRIBUTE_TEXTURE_ALIGNMENT:
                return "Alignment requirement";
            case CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT:
                return "Number of multiprocessors on the device";
            case CU_DEVICE_ATTRIBUTE_KERNEL_EXEC_TIMEOUT:
                return "Whether there is a run time limit on kernels";
            case CU_DEVICE_ATTRIBUTE_INTEGRATED:
                return "Device is integrated with host memory";
            case CU_DEVICE_ATTRIBUTE_CAN_MAP_HOST_MEMORY:
                return "Device can map host memory into CUDA address space";
            case CU_DEVICE_ATTRIBUTE_COMPUTE_MODE:
                return "Compute mode";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_WIDTH:
                return "Maximum 1D texture width";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_WIDTH:
                return "Maximum 2D texture width";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_HEIGHT:
                return "Maximum 2D texture height";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_WIDTH:
                return "Maximum 3D texture width";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_HEIGHT:
                return "Maximum 3D texture height";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_DEPTH:
                return "Maximum 3D texture depth";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_WIDTH:
                return "Maximum 2D layered texture width";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_HEIGHT:
                return "Maximum 2D layered texture height";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_LAYERS:
                return "Maximum layers in a 2D layered texture";
            case CU_DEVICE_ATTRIBUTE_SURFACE_ALIGNMENT:
                return "Alignment requirement for surfaces";
            case CU_DEVICE_ATTRIBUTE_CONCURRENT_KERNELS:
                return "Device can execute multiple kernels concurrently";
            case CU_DEVICE_ATTRIBUTE_ECC_ENABLED:
                return "Device has ECC support enabled";
            case CU_DEVICE_ATTRIBUTE_PCI_BUS_ID:
                return "PCI bus ID of the device";
            case CU_DEVICE_ATTRIBUTE_PCI_DEVICE_ID:
                return "PCI device ID of the device";
            case CU_DEVICE_ATTRIBUTE_TCC_DRIVER:
                return "Device is using TCC driver model";
            case CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE:
                return "Peak memory clock frequency in kilohertz";
            case CU_DEVICE_ATTRIBUTE_GLOBAL_MEMORY_BUS_WIDTH:
                return "Global memory bus width in bits";
            case CU_DEVICE_ATTRIBUTE_L2_CACHE_SIZE:
                return "Size of L2 cache in bytes";
            case CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR:
                return "Maximum resident threads per multiprocessor";
            case CU_DEVICE_ATTRIBUTE_ASYNC_ENGINE_COUNT:
                return "Number of asynchronous engines";
            case CU_DEVICE_ATTRIBUTE_UNIFIED_ADDRESSING:
                return "Device shares a unified address space with the host";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_LAYERED_WIDTH:
                return "Maximum 1D layered texture width";
            case CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_LAYERED_LAYERS:
                return "Maximum layers in a 1D layered texture";
            case CU_DEVICE_ATTRIBUTE_PCI_DOMAIN_ID:
                return "PCI domain ID of the device";
        }
        return "(UNKNOWN ATTRIBUTE)";
    }
    private static List<Integer> getAttributes()
    {
        List<Integer> list = new ArrayList<Integer>();
        list.add(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_X);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Y);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Z);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Y);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Z);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK);
        list.add(CU_DEVICE_ATTRIBUTE_TOTAL_CONSTANT_MEMORY);
        list.add(CU_DEVICE_ATTRIBUTE_WARP_SIZE);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_PITCH);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_REGISTERS_PER_BLOCK);
        list.add(CU_DEVICE_ATTRIBUTE_CLOCK_RATE);
        list.add(CU_DEVICE_ATTRIBUTE_TEXTURE_ALIGNMENT);
        list.add(CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT);
        list.add(CU_DEVICE_ATTRIBUTE_KERNEL_EXEC_TIMEOUT);
        list.add(CU_DEVICE_ATTRIBUTE_INTEGRATED);
        list.add(CU_DEVICE_ATTRIBUTE_CAN_MAP_HOST_MEMORY);
        list.add(CU_DEVICE_ATTRIBUTE_COMPUTE_MODE);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_WIDTH);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_WIDTH);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_HEIGHT);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_WIDTH);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_HEIGHT);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_DEPTH);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_WIDTH);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_HEIGHT);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_LAYERS);
        list.add(CU_DEVICE_ATTRIBUTE_SURFACE_ALIGNMENT);
        list.add(CU_DEVICE_ATTRIBUTE_CONCURRENT_KERNELS);
        list.add(CU_DEVICE_ATTRIBUTE_ECC_ENABLED);
        list.add(CU_DEVICE_ATTRIBUTE_PCI_BUS_ID);
        list.add(CU_DEVICE_ATTRIBUTE_PCI_DEVICE_ID);
        list.add(CU_DEVICE_ATTRIBUTE_TCC_DRIVER);
        list.add(CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE);
        list.add(CU_DEVICE_ATTRIBUTE_GLOBAL_MEMORY_BUS_WIDTH);
        list.add(CU_DEVICE_ATTRIBUTE_L2_CACHE_SIZE);
        list.add(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR);
        list.add(CU_DEVICE_ATTRIBUTE_ASYNC_ENGINE_COUNT);
        list.add(CU_DEVICE_ATTRIBUTE_UNIFIED_ADDRESSING);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_LAYERED_WIDTH);
        list.add(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_LAYERED_LAYERS);
        list.add(CU_DEVICE_ATTRIBUTE_PCI_DOMAIN_ID);
        return list;
    }
    public void gpuInfo() {
        setExceptionsEnabled(true);
        cuInit(0);
        String info = "";
        // Obtain the number of devices
        int deviceCountArray[] = { 0 };
        cuDeviceGetCount(deviceCountArray);
        int deviceCount = deviceCountArray[0];

        for (int i = 0; i < deviceCount; i++) {
            CUdevice device = new CUdevice();
            cuDeviceGet(device, i);

            // Obtain the device name
            byte deviceName[] = new byte[1024];
            cuDeviceGetName(
                    deviceName, deviceName.length, device);
            String name = createString(deviceName);

            // Obtain compute capability
            int majorArray[] = {0};
            int minorArray[] = {0};
            cuDeviceComputeCapability(
                    majorArray, minorArray, device);
            int major = majorArray[0];
            int minor = minorArray[0];
            info += String.format("Device " + i + ": " + name  + " with Compute Capability " + major + "." + minor + "\n");

            // Obtain the device attributes
            int array[] = { 0 };
            List<Integer> attributes = getAttributes();
            for (Integer attribute : attributes) {
                String description = getAttributeDescription(attribute);
                cuDeviceGetAttribute(array, attribute, device);
                int value = array[0];
                info += String.format("%-52s : %d\n", description, value);
            }

        }

        TextArea textArea = new TextArea(info);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("GPU Information");
        alert.setHeaderText("Devices found: " + deviceCount);
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();

    }
    public int[] mandelbrotGPU() {

        setExceptionsEnabled(true);
        cuInit(0);

        height = Integer.parseInt(h.getText());
        width = Integer.parseInt(w.getText());
        N = Integer.parseInt(n.getText());
        minX = Double.parseDouble(minx.getText());
        maxX = Double.parseDouble(maxx.getText());
        minY = Double.parseDouble(miny.getText());
        maxY = Double.parseDouble(maxy.getText());

        int[] tabOfInts = {height,width,N};
        double[] tabOfDoubles = {minX,maxX,minY,maxY};
        imageView.setImage(null);
        img = null;
        pw = null;
        img = new WritableImage(height,width);
        pw = img.getPixelWriter();
        colors = generateColors(N);
        int[] tab = new int[height * width];

        Timer.createTimer("timer");

        // Prepare the kernel
        KernelLauncher kernelLauncher = KernelLauncher.load("MandelbrotGPU.ptx","mandelbrot");

        // Allocate the device memory
        CUdeviceptr ints_GPU = new CUdeviceptr();
        cuMemAlloc(ints_GPU, 3 * Sizeof.INT);

        CUdeviceptr doubles_GPU = new CUdeviceptr();
        cuMemAlloc(doubles_GPU, 4 * Sizeof.DOUBLE);

        CUdeviceptr tab_GPU = new CUdeviceptr();
        cuMemAlloc(tab_GPU, height * width * Sizeof.INT);

        Timer.startTimer("timer");
        // copy the input data to the device
        cuMemcpyHtoD(ints_GPU, Pointer.to(tabOfInts), 3 * Sizeof.INT);
        cuMemcpyHtoD(doubles_GPU, Pointer.to(tabOfDoubles), 4 * Sizeof.DOUBLE);

        int bSize = Integer.parseInt(blockSize.getText());
        int gSize = (int)Math.ceil( ( (double)(height * width) + bSize - 1) / bSize);


        // Call the kernel
        kernelLauncher.setGridSize(gSize,1);
        kernelLauncher.setBlockSize(bSize,1,1);
        kernelLauncher.call(tab_GPU,ints_GPU,doubles_GPU);

        // Copy the result from the device to the host
        cuMemcpyDtoH(Pointer.to(tab), tab_GPU, width * height * Sizeof.INT);

        Timer.stopTimer("timer");

        int timeVal = Timer.getTimerValue("timer");
        String output = String.format(Locale.US,"%02d:%02d:%02.3f",timeVal/3600000,timeVal/60000,(double)(timeVal%60000)/1000);
        gpuTimeLabel.setText(output);
        Timer.deleteTimer("timer");

        // Clean up
        cuMemFree(ints_GPU);
        cuMemFree(doubles_GPU);
        cuMemFree(tab_GPU);

        if(compare) {
            return tab;
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pw.setColor(i,j,colors[tab[i*width+j]]);
            }
        }
        imageView.setImage(img);
        return null;
    }
    public void mandelbrotPCJ(){
        if(h.getText() == "" || w.getText() == "" || minx.getText() == "" || maxx.getText() == "" || t.getText() == ""|| miny.getText() == ""|| maxy.getText() == ""|| n.getText() == "") {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Incorrect input data");
            alert.showAndWait();
        } else {
            if(Integer.parseInt(h.getText()) % Integer.parseInt(t.getText()) != 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Threads must divide width");
                alert.showAndWait();
                return;
            }
            height = Integer.parseInt(h.getText());
            width = Integer.parseInt(w.getText());
            N = Integer.parseInt(n.getText());
            threads = Integer.parseInt(t.getText());
            minX = Double.parseDouble(minx.getText());
            maxX = Double.parseDouble(maxx.getText());
            minY = Double.parseDouble(miny.getText());
            maxY = Double.parseDouble(maxy.getText());
            setNodes(threads);
            imageTab = null;
            imageView.setImage(null);
            img = null;
            pw = null;
            img = new WritableImage(height,width);
            pw = img.getPixelWriter();

            colors = generateColors(N);

            imageTab = new int[height][width];

            try {
                PCJ.executionBuilder (MandelbrotPCJ.class)
                        .addNodes(nodes)
                        .start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int timeVal = (int)Math.floor(timePCJ * 1.0E-9);
            String output = String.format(Locale.US,"%02d:%02d:%02.3f",timeVal/3600,timeVal/60,(timePCJ * 1.0E-9)%60);
            pcjTimeLabel.setText(output);

            setImage();
        }
    }
    void setNodes(int numberOfNodes){
        nodes = new File("nodes.txt");
        FileWriter myWriter;
        try {
            myWriter = new FileWriter(nodes.getName());
            for (int i = 0; i < numberOfNodes; i++) {
                myWriter.write("localhost\n");
            }
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearImage() {
        imageView.setImage(null);
        pcjTimeLabel.setText("--:--:--.---");
        gpuTimeLabel.setText("--:--:--.---");
        TimeLabel.setText("--:--:--.---");
    }

    public void comparator() {
        compare = true;
        int tabCPU[][] = new int[height][width];
        int tabGPU[] = new int[height*width];

        Mandelbrot m = new Mandelbrot();
        tabCPU = mandelbrot();
        tabGPU = mandelbrotGPU();
        compare = false;
        int diffValues = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if(tabGPU[i*width+j] != tabCPU[i][j])
                    diffValues++;
            }
        }
        System.out.println("Different values: " + diffValues + "/" + (height*width));

    }
}