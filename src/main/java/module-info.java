module com.example.dld3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.dld3 to javafx.fxml;
    exports com.example.dld3;
}