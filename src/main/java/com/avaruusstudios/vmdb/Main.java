package com.avaruusstudios.vmdb;

import com.avaruusstudios.vmdb.db.Database;
import com.avaruusstudios.vmdb.db.DatabaseInitializationException;
import com.avaruusstudios.vmdb.model.AppErrorCode; // Not directly used here, but good for context
import javafx.application.Application;
import javafx.application.Platform; // For exiting the platform
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <p>
 * The main entry point for the Vanpool Management Database desktop application.
 * This class extends {@link javafx.application.Application} to bootstrap the
 * JavaFX user interface.
 * </p>
 *
 * <p>
 * It handles the initial setup, including database initialization,
 * and sets up the primary application window.
 * </p>
 *
 * @author [Your Name/Team Name] (You can update this)
 * @version 1.0
 */
public class Main extends Application {
    /**
     * <p>
     * An instance of {@link Logger} from the SLF4J API.
     * This logger is used for capturing and outputting diagnostic messages
     * related to the application's startup and JavaFX lifecycle.
     * </p>
     */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * <p>
     * The primary entry point for the JavaFX application. This method is
     * called after the {@link #main(String[])} method's `launch()` call
     * has processed the application setup.
     * </p>
     *
     * <p>
     * It loads the main FXML layout, sets up the primary scene,
     * applies stylesheets, and displays the main application window.
     * </p>
     *
     * @param stage The primary stage for this application, onto which
     * the application scene can be set. The stage is provided by the JavaFX runtime.
     * @throws IOException If the FXML file cannot be loaded, indicating a resource issue.
     */
    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting Vanpool Management Database application...");
        // Use absolute paths for FXML and CSS resources for robustness
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/avaruusstudios/vmdb/views/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        // Load CSS stylesheet
        String cssPath = getClass().getResource("/com/avaruusstudios/vmdb/styles/styles.css").toExternalForm();
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
            logger.debug("Stylesheet loaded from: {}", cssPath);
        } else {
            logger.warn("Stylesheet not found at /com/avaruusstudios/vmdb/styles/styles.css");
        }

        stage.setTitle("Vanpool Management Database");
        stage.setScene(scene);
        stage.show();
        logger.info("Application main window displayed successfully.");
    }
    /**
     * <p>
     * The main method and the entry point for the Java application.
     * </p>
     *
     * <p>
     * It first initializes the SQLite database, ensuring it exists and its schema
     * is up-to-date. If database initialization fails, it logs a critical error
     * and gracefully exits the application as the database is essential for operation.
     * After successful database initialization, it then calls `launch()` to
     * start the JavaFX application lifecycle, which will eventually invoke the
     * {@link #start(Stage)} method.
     * </p>
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        logger.info("Application starting...");
        try {
            // Initialize the database (create if it doesn't exist)
            Database.createDatabase();
            logger.info("Database initialization complete.");
            launch(); // Start the JavaFX application
        } catch (DatabaseInitializationException e) {
            logger.error("FATAL ERROR: Database initialization failed. Application cannot proceed.", e);
            // Optionally, display a simple error dialog before exiting for user feedback
            // For a GUI app, you might use Platform.exit() and then System.exit()
            Platform.exit(); // Shuts down JavaFX toolkit
            System.exit(1); // Exits the JVM with a non-zero status indicating an error
        } catch (Exception e) {
            // Catch any other unexpected exceptions during startup
            logger.error("FATAL ERROR: An unexpected error occurred during application startup.", e);
            Platform.exit();
            System.exit(1);
        }
    }
    /**
     * <p>
     * The `init()` method is a JavaFX Application lifecycle method.
     * It is called by the launcher immediately after the Application class is loaded and constructed.
     * Override this method if you have any non-GUI initialization tasks that need to be performed
     * before the `start()` method is called. For example, setting up services or loading resources.
     * </p>
     *
     * <p>
     * (Currently not overridden, but included for conceptual understanding of lifecycle.)
     * </p>
     */
    // @Override
    // public void init() throws Exception {
    //     super.init();
    //     // Perform non-GUI initialization here
    //     logger.info("Application init method called.");
    // }
    /**
     * <p>
     * The `stop()` method is a JavaFX Application lifecycle method.
     * It is called by the launcher when the application is shutting down.
     * Override this method to perform cleanup tasks, such as closing database connections,
     * saving user preferences, or releasing system resources.
     * </p>
     *
     * <p>
     * (Currently not overridden, but included for conceptual understanding of lifecycle.)
     * </p>
     */
    // @Override
    // public void stop() throws Exception {
    //     super.stop();
    //     // Perform cleanup tasks here
    //     logger.info("Application stop method called. Performing cleanup...");
    //     // Example: Database.closeConnections(); // If you had a pool to explicitly close
    // }
}