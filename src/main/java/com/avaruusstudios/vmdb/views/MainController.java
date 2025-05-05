package com.avaruusstudios.vmdb.views;

import javafx.fxml.FXML;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class MainController {
    @FXML
    private TreeView<String> navigationTreeView;

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
                NavigationTreeCell cell = new NavigationTreeCell();
                cell.setRootItem(root);
                return cell;
            }
        });
    }

    /** Custom TreeCell Factory */
    private static class NavigationTreeCell extends TreeCell<String> {
        private TreeItem<String> rootItem;

        public NavigationTreeCell() {
            setOnMouseClicked(event -> {
                if (!isEmpty() && event.getClickCount() == 1) {
                    String item = getItem();
                    if (item != null) {
                        switch (item) {
                            case "Participants":
                                System.out.println("TreeCell Clicked: Participants");
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