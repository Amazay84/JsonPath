package jsonpathui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import jsonPath.JsonPath;
import org.fxmisc.richtext.CodeArea;
import static jsonpathui.StyleJson.highlight;

public class JsonPathUIController {

    @FXML
    private TextField jsonPath;

    @FXML
    private CodeArea jsonText;

    @FXML
    private CodeArea result;

    private JsonElement json;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @FXML
    void initialize() {
        result.replaceText("Ведите Json!");
        jsonText.setStyle("-fx-font-family: 'System Regular'; -fx-font-size: 13");
        result.setStyle("-fx-font-family: 'System Regular';-fx-font-size: 13");
        jsonPath.setStyle("-fx-font-size: 13");
        jsonText.textProperty().addListener((observable, oldValue, newValue) -> listnerJson(gson, newValue));
        jsonPath.setOnKeyTyped(keyEvent -> listnerJsonPath());
        jsonPath.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (json != null) {
                String prettyJson = gson.toJson(json);
                jsonText.replaceText(prettyJson);
                //jsonText.setStyleSpans(0, highlight(prettyJson));
            }
        });
    }

    private void listnerJson(Gson gson, String newValue) {
        try {
            json = gson.fromJson(newValue, JsonElement.class);
            jsonText.setStyleSpans(0, highlight(newValue));
        } catch (Exception e) {
            json = null;
            result.replaceText("Не валидный верный Json!");
            return;
        }
        if (json != null && !json.isJsonNull()) {
            if (!jsonPath.getText().isEmpty())
                listnerJsonPath();
            else
                result.replaceText("Ведите JsonPath!");
        } else
            result.replaceText("Ведите Json!");
    }

    private void listnerJsonPath() {
        if (json != null) {
            try {
                String resultStr = JsonPath.getValue(json, jsonPath.getText());
                try {
                    JsonElement elem = gson.fromJson(resultStr, JsonElement.class);
                    if (!elem.isJsonPrimitive())
                        resultStr = gson.toJson(elem);
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
}
