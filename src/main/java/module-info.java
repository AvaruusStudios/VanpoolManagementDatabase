/**
 * <p>
 * This is the module descriptor for the Vanpool Management Database (VMDB) application.
 * It defines the module's name, its dependencies on other modules, and which of its
 * packages are made accessible to other modules at compile time (via {@code exports})
 * or at runtime for reflective access (via {@code opens}).
 * </p>
 *
 * <p>
 * The Java Module System (JPMS), introduced in Java 9, enforces strong encapsulation
 * and improves application reliability and performance by defining clear boundaries
 * and dependencies between components.
 * </p>
 */
module com.avaruusstudios.vmdb {
    /**
     * Declares a dependency on the {@code javafx.controls} module.
     * This module provides all the standard user interface controls for JavaFX applications,
     * such as {@link javafx.scene.control.TableView}, {@link javafx.scene.control.Button},
     * {@link javafx.scene.control.Label}, etc.
     */
    requires javafx.controls;
    /**
     * Declares a dependency on the {@code javafx.fxml} module.
     * This module enables the use of FXML, an XML-based language for defining the user
     * interface of JavaFX applications. It's crucial for loading {@code .fxml} files
     * and connecting UI elements to controller classes.
     */
    requires javafx.fxml;
    /**
     * Declares a dependency on the {@code javafx.base} module.
     * This module provides fundamental non-visual classes for JavaFX, including
     * core utilities, property APIs (like {@link javafx.beans.property.Property}),
     * and the reflection mechanisms used by components like {@link javafx.scene.control.cell.PropertyValueFactory}.
     * It is explicitly required here because `PropertyValueFactory` needs reflective access
     * to the `DashboardParticipantView` class.
     */
    requires javafx.base;
    /**
     * Declares a dependency on the standard Java {@code java.sql} module.
     * This module provides the Java Database Connectivity (JDBC) API, which is
     * used for connecting to and interacting with relational databases like SQLite.
     */
    requires java.sql;
    /**
     * Declares a dependency on the {@code org.slf4j} module.
     * This module is for the Simple Logging Facade for Java, providing a common
     * abstraction layer for various logging frameworks (e.g., Logback, Log4j).
     */
    requires org.slf4j;

    /**
     * <p>
     * Opens the root package {@code com.avaruusstudios.vmdb} for reflective access
     * specifically to the {@code javafx.fxml} module.
     * </p>
     * <p>
     * This is necessary if your main application class (e.g., {@code Main.java})
     * or any FXML controllers are directly within this root package and FXML needs
     * to instantiate them or inject fields/methods.
     * </p>
     */
    opens com.avaruusstudios.vmdb to javafx.fxml;
    /**
     * <p>
     * Opens the {@code com.avaruusstudios.vmdb.view} package for reflective access
     * specifically to the {@code javafx.fxml} module.
     * </p>
     * <p>
     * This is crucial for FXML controllers located directly in this package (e.g.,
     * {@code MainController} or other view controllers) to allow FXML to instantiate
     * them and inject their {@code @FXML} annotated fields and methods.
     * </p>
     */
    opens com.avaruusstudios.vmdb.view to javafx.fxml;
    /**
     * <p>
     * Opens the {@code com.avaruusstudios.vmdb.view.model} package for reflective access
     * specifically to the {@code javafx.base} module.
     * </p>
     * <p>
     * This is the critical directive that resolves {@code IllegalAccessException} errors
     * when using {@link javafx.scene.control.cell.PropertyValueFactory}. {@code PropertyValueFactory}
     * uses reflection to access getter methods (e.g., `getId()`, `getParticipantDisplayName()`)
     * on your data model classes (like {@link com.avaruusstudios.vmdb.views.models.DashboardParticipantView}).
     * Without this `opens` directive, the Java Module System's strong encapsulation would prevent
     * {@code javafx.base} from accessing these methods.
     * </p>
     */
    opens com.avaruusstudios.vmdb.view.model to javafx.base;

    /**
     * <p>
     * Exports the root package {@code com.avaruusstudios.vmdb}.
     * </p>
     * <p>
     * This makes all public types (classes, interfaces) within this package
     * accessible to *other modules that explicitly {@code require} this module*.
     * This is typically needed for the application's entry point (e.g., a `Main` class)
     * so that the Java runtime launcher can find and execute it.
     * </p>
     */
    exports com.avaruusstudios.vmdb;
    /**
     * <p>
     * Exports the {@code com.avaruusstudios.vmdb.view} package.
     * </p>
     * <p>
     * This allows public types within the `view` package (e.g., abstract view classes
     * or interfaces that other modules might depend on) to be accessible to other
     * modules that {@code require} {@code com.avaruusstudios.vmdb}.
     * </p>
     */
    exports com.avaruusstudios.vmdb.view;
}