module com.example.xcelparser2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires kotlin.stdlib;
    requires poi.ooxml;
    requires poi;
    requires java.sql;

    opens com.example.xcelparser2 to javafx.fxml;
    exports com.example.xcelparser2;
}