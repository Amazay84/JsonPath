package jsonpathui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import jsonPath.JsonPath;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


import static jsonpathui.StyleJson.highlight;

public class JsonPathUIController {

    @FXML
    private TextField jsonPath;

    @FXML
    private CodeArea jsonText;

    @FXML
    private CodeArea result;

    private JsonElement jsonNew;
    private JsonElement jsonOld;

    private Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    @FXML
    void initialize() {
        result.editableProperty().set(false);
        addContextMenu(jsonText);
        addContextMenu(result);
        addEventKeyCombination(jsonText);
        addEventKeyCombination(result);
        jsonText.setParagraphGraphicFactory(LineNumberFactory.get(jsonText));
        result.setParagraphGraphicFactory(LineNumberFactory.get(result));
        result.replaceText("Ведите Json!");
        jsonText.textProperty().addListener((observable, oldValue, newValue) -> listnerJson(gson, newValue));
        jsonPath.setOnKeyTyped(keyEvent -> listnerJsonPath());
        jsonPath.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (jsonNew != null && jsonNew != jsonOld) {
                String prettyJson = gson.toJson(jsonNew);
                jsonText.replaceText(prettyJson);
                jsonOld = jsonNew;
                jsonText.setStyleSpans(0, highlight(prettyJson));
            }
        });
    }

    private void addEventKeyCombination(CodeArea codeArea) {
        KeyCombination C_c = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        KeyCombination C_v = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
        KeyCombination C_a = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
        KeyCombination C_z = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        KeyCombination C_x = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
        KeyCombination C_S_z = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        if (codeArea.isEditable())
            codeArea.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
                if (C_c.match(evt) && evt.getText().charAt(0) != 'c')
                    jsonText.copy();
                else if (C_v.match(evt) && evt.getText().charAt(0) != 'v')
                    jsonText.paste();
                else if (C_a.match(evt) && evt.getText().charAt(0) != 'a')
                    jsonText.selectAll();
                else if (C_x.match(evt) && evt.getText().charAt(0) != 'x')
                    jsonText.cut();
                else if (C_z.match(evt) && evt.getText().charAt(0) != 'z')
                    jsonText.undo();
                else if (C_S_z.match(evt) && evt.getText().charAt(0) != 'z')
                    jsonText.redo();
            });
        else
            codeArea.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
                if (C_c.match(evt) && evt.getText().charAt(0) != 'c')
                    jsonText.copy();
                else if (C_a.match(evt) && evt.getText().charAt(0) != 'a')
                    jsonText.selectAll();
            });
    }

    private void addContextMenu(CodeArea codeArea) {
        ContextMenu contextMenu = new ContextMenu();
        addActionToContextMenu(contextMenu, "Copy", codeArea::copy, true);
        addActionToContextMenu(contextMenu, "Paste", codeArea::paste, codeArea.isEditable());
        addActionToContextMenu(contextMenu, "Cut", codeArea::cut, codeArea.isEditable());
        addActionToContextMenu(contextMenu, "Select all", codeArea::selectAll, true);
        addActionToContextMenu(contextMenu, "Undo", codeArea::undo, codeArea.isEditable());
        addActionToContextMenu(contextMenu, "Redo", codeArea::redo, codeArea.isEditable());
        codeArea.setContextMenu(contextMenu);
    }

    private void addActionToContextMenu(ContextMenu contextMenu, String name, RunFunction supplier, boolean isEdit) {
        MenuItem item = new MenuItem(name);
        if (!isEdit)
            item.setDisable(true);
        contextMenu.getItems().add(item);
        item.setOnAction(event -> supplier.run());
    }

    private void listnerJson(Gson gson, String newValue) {
        try {
            jsonNew = gson.fromJson(newValue, JsonElement.class);
            jsonText.setStyleSpans(0, highlight(newValue));
        } catch (Exception e) {
            jsonNew = null;
            result.replaceText("Не валидный верный Json!");
            return;
        }
        if (jsonNew != null && !jsonNew.isJsonNull()) {
            if (!jsonPath.getText().isEmpty())
                listnerJsonPath();
            else
                result.replaceText("Ведите JsonPath!");
        } else
            result.replaceText("Ведите Json!");
    }

    private void listnerJsonPath() {
        if (jsonNew != null) {
            try {
                String resultStr = JsonPath.getValue(jsonNew, jsonPath.getText());
                try {
                    JsonElement elem = gson.fromJson(resultStr, JsonElement.class);
                    if (!elem.isJsonPrimitive())
                        resultStr = gson.toJson(elem);
                    else if (((JsonPrimitive) elem).isString())
                        resultStr = "\"" + resultStr + "\"";
                } catch (Exception ignored) {
                    resultStr = "\"" + resultStr + "\"";
                }
                result.replaceText(resultStr);
                result.setStyleSpans(0, highlight(resultStr));
            } catch (Exception e) {
                if (e.getMessage() != null)
                    result.replaceText(e.getMessage());
                else if (!jsonPath.getText().isEmpty())
                    result.replaceText("Не валидный JsonPath!");
                else
                    result.replaceText("Ведите JsonPath!");
            }
        }

    }

    public TextField getJsonPath() {
        return jsonPath;
    }

    public CodeArea getJsonText() {
        return jsonText;
    }

    public CodeArea getResult() {
        return result;
    }

    @FunctionalInterface
    public interface RunFunction {

        void run();
    }
}
