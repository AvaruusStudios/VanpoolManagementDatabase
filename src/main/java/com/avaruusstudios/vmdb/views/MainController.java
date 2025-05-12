package com.avaruusstudios.vmdb.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    @FXML
    private ListView<String> navigationListView;
    @FXML
    private TableView<Map<String, Object>> mainTableView;
    @FXML
    private Label txtCurrentDate;

    private ObservableList<String> navigationItems;

    public void initialize() {
        navigationItems = FXCollections.observableArrayList("Participants", "Add Participant", "Edit Participant", "Remove Participant");
        navigationListView.setItems(navigationItems);

        // Set the current date in the txtCurrentDate TextField
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy"); // You can adjust the format
        txtCurrentDate.setText(currentDate.format(formatter));

        // Prevent any selection from the ListView
        navigationListView.setSelectionModel(null); // Disable selection model

        navigationListView.setCellFactory(param -> new ListCell<String>() {
            private final Label textLabel = new Label();
            private final Color defaultColor = Color.BLUE;
            private final Color clickedColor = Color.PURPLE;

            {
                textLabel.setFont(Font.font("System", 14));
                textLabel.setTextFill(Color.BLUE);
                textLabel.setUnderline(true);
                setGraphic(textLabel);
                setText(null); // Ensure default text is null
                textLabel.setCursor(javafx.scene.Cursor.HAND);

                // Set the click handler ONLY on the textLabel
                textLabel.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1) {
                        String item = getItem();
                        // Change color on click
                        textLabel.setTextFill(clickedColor);
                        switch (item) {
                            case "Participants":
                                loadTableData("SELECT * FROM Participants");
                                break;
                            case "Add Participant":
                                System.out.println("Text Clicked: Add Participant");
                                break;
                            case "Edit Participant":
                                System.out.println("Text Clicked: Edit Participant");
                                break;
                            case "Remove Participant":
                                System.out.println("Text Clicked: Remove Participant");
                                break;
                        }
                        event.consume(); // Prevent propagation

                        // Revert to the default color after a short delay
                        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200)); // Adjust delay as needed
                        pause.setOnFinished(e -> textLabel.setTextFill(defaultColor));
                        pause.play();
                    }
                });

                // Prevent the ListCell from being selected on click
                setOnMouseClicked(event -> {
                    event.consume();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    textLabel.setText(null);
                    setGraphic(null);
                    setOnMouseClicked(null); // Clear any ListCell click handler
                } else {
                    textLabel.setText(item);
                    setGraphic(textLabel);
                    // The ListCell's onMouseClicked now consumes the event,
                    // preventing default selection.
                }
            }
        });
    }

    /** Loads data into the TableView based on the provided SQL query */
    private void loadTableData(String sqlQuery) {
        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList();
        mainTableView.getColumns().clear(); // Clear any existing columns

        String url = "jdbc:sqlite:./vanpool.db";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                columnNames.add(columnName);
                TableColumn<Map<String, Object>, Object> column = new TableColumn<>(columnName);
                final String finalColumnName = columnName; // Need a final variable for the lambda

                column.setCellValueFactory(cellData ->
                        new javafx.beans.property.ReadOnlyObjectWrapper<>(cellData.getValue().get(finalColumnName)));

                mainTableView.getColumns().add(column);
            }

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (String columnName : columnNames) {
                    row.put(columnName, resultSet.getObject(columnName));
                }
                data.add(row);
            }

            mainTableView.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception properly
        }
    }
}