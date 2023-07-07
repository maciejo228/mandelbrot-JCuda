module com.example.mandelbrotfx_v2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.pcj;
    requires jcuda;
    requires jcudaUtils;


    opens com.example.mandelbrotfx_v2 to javafx.fxml;
    exports com.example.mandelbrotfx_v2;
}