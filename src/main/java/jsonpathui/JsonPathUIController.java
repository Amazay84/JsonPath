package jsonpathui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
}
