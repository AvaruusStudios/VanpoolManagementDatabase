package com.avaruusstudios.vmdb.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {
    @FXML
    private TreeView<String> navigationTreeView;
    @FXML
    private TableView<Map<String, Object>> mainTableView;

    public void initialize() {
        /** Creates a root item in the TreeView Navigation */
        TreeItem<String> root = new TreeItem<>("Navigation");
        root.setExpanded(true);

        /** Prevent collapsing the root node */
        root.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // If the user tries to collapse (newValue is false)
                root.setExpanded(true); // Immediately expand it back
            }
        });

        /** Creates group in the TreeView Navigation */
        TreeItem<String> participantsGroup = new TreeItem<>("Participants");
        participantsGroup.setExpanded(true);

        /** Creates sub-items for addition to a group in the TreeView Navigation */
        TreeItem<String> addParticipantItem = new TreeItem<>("Add Participant");
        TreeItem<String> editParticipantItem = new TreeItem<>("Edit Participant");
        TreeItem<String> removeParticipantItem = new TreeItem<>("Remove Participant");

        /** Adds the items to the group */
        participantsGroup.getChildren().addAll(addParticipantItem, editParticipantItem, removeParticipantItem);

        /** Adds the group to the root */
        root.getChildren().add(participantsGroup);

        navigationTreeView.setRoot(root);
        navigationTreeView.setShowRoot(true);

//        final TreeItem<String> finalRoot = root;

        navigationTreeView.setCellFactory(new Callback<TreeView<String>, TreeCell<String>>() {
            @Override
            public TreeCell<String> call(TreeView<String> param) {
                NavigationTreeCell cell = new NavigationTreeCell(root, MainController.this);
                cell.setRootItem(root);
                return cell;
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

    /** Custom TreeCell Factory */
    private static class NavigationTreeCell extends TreeCell<String> {
        private TreeItem<String> rootItem;
        private final MainController controller;

        public NavigationTreeCell(TreeItem<String> rootItem, MainController controller) {
            this.rootItem = rootItem;
            this.controller = controller;
            setOnMouseClicked(event -> {
                if (!isEmpty() && event.getClickCount() == 1) {
                    String item = getItem();
                    if (item != null) {
                        switch (item) {
                            case "Participants":
                                System.out.println("TreeCell Clicked: Participants");
                                this.controller.loadTableData("SELECT * FROM Participants");
                                break;
                            case "Add Participant":
                                System.out.println("TreeCell Clicked: Add Participant");
                                break;
                            case "Edit Participant":
                                System.out.println("TreeCell Clicked: Edit Participant");
                                break;
                            case "Remove Participant":
                                System.out.println("TreeCell Clicked: Remove Participant");
                                break;
                        }
                    }
                }
            });
        }

        public void setRootItem(TreeItem<String> rootItem) {
            this.rootItem = rootItem;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : item);
            if (getTreeItem() == rootItem) {
                setDisclosureNode(null);
            }
        }
    }
}