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

    // --- Module Dependencies (requires) ---
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
     * and mechanisms for reflective access, which are essential for components
     * like {@link javafx.scene.control.cell.PropertyValueFactory}.
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

    // --- Module Exports (exports) ---
    /**
     * <p>
     * Exports the root package {@code com.avaruusstudios.vmdb}.
     * </p>
     * <p>
     * This makes all public types (classes, interfaces) within this package
     * accessible to *other modules that explicitly {@code require} this module*.
     * This is typically needed for the application's entry point (e.g., a {@code Main} class)
     * so that the Java runtime launcher can find and execute it.
     * </p>
     */
    exports com.avaruusstudios.vmdb;
    /**
     * <p>
     * Exports the {@code com.avaruusstudios.vmdb.view.main} package.
     * </p>
     * <p>
     * This allows public types within the `main` view package (e.g., {@code MainController}
     * if it were needed by other modules) to be accessible to other modules that
     * {@code require} {@code com.avaruusstudios.vmdb}.
     * </p>
     */
    exports com.avaruusstudios.vmdb.view.main;
    /**
     * <p>
     * Exports the {@code com.avaruusstudios.vmdb.view.vanpool.participant} package.
     * </p>
     * <p>
     * This allows public types within the `roster` view package (e.g., view models
     * or utility classes that other modules might depend on) to be accessible to
     * other modules that {@code require} {@code com.avaruusstudios.vmdb}.
     * </p>
     */
    exports com.avaruusstudios.vmdb.view.vanpool.participant;

    // --- Module Opens (opens) ---
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
     * Opens the general {@code com.avaruusstudios.vmdb.view} package for reflective access
     * specifically to the {@code javafx.fxml} module.
     * </p>
     * <p>
     * This is crucial if any FXML controllers or their related components are located
     * directly within this `view` package (not in sub-packages) and FXML needs to
     * instantiate them or inject their {@code @FXML} annotated fields and methods.
     * </p>
     */
    opens com.avaruusstudios.vmdb.view to javafx.fxml;
    /**
     * <p>
     * Opens the {@code com.avaruusstudios.vmdb.view.main} package for reflective access
     * specifically to the {@code javafx.fxml} module.
     * </p>
     * <p>
     * This is crucial for FXML controllers (like {@code MainController}) located
     * in this package to allow FXML to instantiate them and inject their
     * {@code @FXML} annotated fields and methods.
     * </p>
     */
    opens com.avaruusstudios.vmdb.view.main to javafx.fxml;
    /**
     * <p>
     * Opens the {@code com.avaruusstudios.vmdb.view.vanpool.participant} package for reflective access
     * specifically to the {@code javafx.base} and {@code javafx.fxml} modules.
     * </p>
     * <p>
     * Opening to {@code javafx.fxml} is crucial for FXML controllers (like {@code ContentParticipantController})
     * to allow FXML to instantiate them and inject {@code @FXML} annotated fields and methods.
     * </p>
     * <p>
     * Opening to {@code javafx.base} is critical for resolving {@code IllegalAccessException} errors
     * when using {@link javafx.scene.control.cell.PropertyValueFactory}. {@code PropertyValueFactory}
     * uses reflection to access getter methods (e.g., `getId()`, `getParticipantDisplayName()`)
     * on data model classes (like {@code DashboardParticipantView}) that are used in TableView columns.
     * </p>
     */
    opens com.avaruusstudios.vmdb.view.vanpool.participant to javafx.base, javafx.fxml;
}