package jsonpathui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class JsonPathUI extends Application {

    private TabPane tabPane = new TabPane();
    private String nameStateFile = "state.json";
    private AnchorPane anchorPane = new AnchorPane();
    private Map<Tab, JsonPathUIController> controllersList = new HashMap<>();

    @Override
    public void start(Stage stage) {
        AnchorPane.setBottomAnchor(tabPane, 0.0);
        AnchorPane.setTopAnchor(tabPane, 0.0);
        AnchorPane.setLeftAnchor(tabPane, 0.0);
        AnchorPane.setRightAnchor(tabPane, 0.0);
        anchorPane.getChildren().add(tabPane);

        readState();
        createButtonNewTab(tabPane);

        URL cssResource = JsonPathUI.class.getResource("JsonPathUI.css");
        Image image = new Image(JsonPathUI.class.getResource("JsonPathUI_icon.png").toString());
        Scene scene = new Scene(anchorPane, 1000, 700);
        scene.getStylesheets().add(cssResource.toExternalForm());
        stage.setTitle("JsonPathUI 1.4");
        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> writeState());
        stage.show();
    }

    private void readState() {
        try (Reader reader = Files.newBufferedReader(Paths.get(nameStateFile))) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            StateTabs stateTabs = gson.fromJson(reader, StateTabs.class);
            for (StateScene stateScene : stateTabs.getStaitScenes()) {
                Tab newTab = tabPane.getTabs().isEmpty() ? createTab(stateScene.getTabName(),false) : createTab(stateScene.getTabName(),true);
                JsonPathUIController controller = controllersList.get(newTab);
                controller.getJsonPath().setText(stateScene.getJsonPath());
                controller.getJsonText().replaceText(stateScene.getJson());
            }
        } catch (Exception e) {
            createTab("Tab 1", false);
        }
    }

    private void writeState() {
        ArrayList<StateScene> stateScenes = new ArrayList<>();
        for (Tab tab : tabPane.getTabs()) {
            if (!tab.getText().equals("+")) {
                JsonPathUIController controller = controllersList.get(tab);
                stateScenes.add(new StateScene(controller.getJsonText().getText(),
                        controller.getJsonPath().getText(),
                        tab.getText()));
            }
        }
        StateTabs stateTabs = new StateTabs(stateScenes);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (BufferedWriter writter = Files.newBufferedWriter(Paths.get(nameStateFile))) {
            writter.write(gson.toJson(stateTabs));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewTab(String name) {
        FXMLLoader loader = new FXMLLoader(JsonPathUI.class.getResource("JsonPathUI.fxml"));
        Tab newTab = null;
        try {
            newTab = new Tab(name, loader.load());
            newTab.closableProperty().set(true);
            addRenamePropertyTab(newTab);
            newTab.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controllersList.put(newTab, loader.getController());
        tabPane.getTabs().add(tabPane.getTabs().size() - 1, newTab);
        tabPane.getSelectionModel().selectPrevious();
    }

    private Tab createTab(String name, boolean isClosable) {
        FXMLLoader loader = new FXMLLoader(JsonPathUI.class.getResource("JsonPathUI.fxml"));
        Tab newTab = null;
        try {
            newTab = new Tab(name, loader.load());
            newTab.closableProperty().set(isClosable);
            addRenamePropertyTab(newTab);
            newTab.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controllersList.put(newTab, loader.getController());
        tabPane.getTabs().add(newTab);
        return newTab;
    }

    private void createButtonNewTab(TabPane tabPane) {
        Tab newTab = new Tab("+");
        newTab.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14;");
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
        textField.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 13;");
        rename.setOnAction(event -> {
            tab.setGraphic(textField);
            tab.setText("");
            tab.setText(tab.getText());
            textField.selectAll();
            textField.requestFocus();
        });
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                if (!textField.getText().isEmpty()) {
                    tab.setText(textField.getText());
                    tab.setGraphic(null);
                }
            }
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!textField.getText().isEmpty()) {
                    tab.setText(textField.getText());
                    tab.setGraphic(null);
                }
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}