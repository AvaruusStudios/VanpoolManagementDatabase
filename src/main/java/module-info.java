module com.avaruusstudios.vmdb {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.avaruusstudios.vmdb to javafx.fxml;
    exports com.avaruusstudios.vmdb;
}