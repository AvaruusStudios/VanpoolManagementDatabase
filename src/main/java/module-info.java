module com.avaruusstudios.vmdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;


    opens com.avaruusstudios.vmdb to javafx.fxml;
    exports com.avaruusstudios.vmdb;
    exports com.avaruusstudios.vmdb.views;
    opens com.avaruusstudios.vmdb.views to javafx.fxml;
}