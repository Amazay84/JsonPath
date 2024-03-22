package jsonpathui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class JsonPathUI extends Application {

    private TabPane tabPane = new TabPane();
    private AnchorPane anchorPane = new AnchorPane();
    private ArrayList<JsonPathUIController> controllersList = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);

        createFirstTab("Tab 1");
        createButtonNewTab(tabPane);

        anchorPane.getChildren().add(tabPane);
        URL cssResource = JsonPathUI.class.getResource("JsonPathUI.css");
        Image image = new Image(JsonPathUI.class.getResource("JsonPathUI_icon.png").toString());
        Scene scene = new Scene(anchorPane, 1000, 700);
        scene.getStylesheets().add(cssResource.toExternalForm());
        stage.setTitle("JsonPathUI");
        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.show();
    }

    private void createNewTab(String name) {
        FXMLLoader loader = new FXMLLoader(JsonPathUI.class.getResource("JsonPathUI.fxml"));
        Tab newTab = null;
        try {
            newTab = new Tab(name, loader.load());
            newTab.closableProperty().set(true);
            addRenamePropertyTab(newTab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controllersList.add(loader.getController());
        tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);
        tabPane.getSelectionModel().selectPrevious();
    }

    private void createFirstTab(String name) {
        FXMLLoader loader = new FXMLLoader(JsonPathUI.class.getResource("JsonPathUI.fxml"));
        Tab newTab = null;
        try {
            newTab = new Tab(name, loader.load());
            newTab.closableProperty().set(false);
            addRenamePropertyTab(newTab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controllersList.add(loader.getController());
        tabPane.getTabs().add(newTab);
    }

    private void createButtonNewTab(TabPane tabPane) {
        Tab newTab = new Tab("+");
        newTab.setOnSelectionChanged(event ->
                createNewTab("Tab " + tabPane.getTabs().size()));
        tabPane.getTabs().add(newTab);
    }

    private void addRenamePropertyTab(Tab tab) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem rename = new MenuItem("Rename");
        contextMenu.getItems().add(rename);
        final TextField textField = new TextField();
        tab.setContextMenu(contextMenu);
        rename.setOnAction(event -> {
            tab.setGraphic(textField);
            tab.setText("");
            textField.selectAll();
            textField.requestFocus();
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                tab.setText(textField.getText());
                tab.setGraphic(null);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}