package jsonpathui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import jsonPath.JsonPath;

public class JsonPathUIController {

    @FXML
    private TextField jsonPath;

    @FXML
    private TextArea jsonText;

    @FXML
    private TextArea result;

    private JsonElement json;

    @FXML
    void initialize() {
        result.setText("Ведите Json!");
        result.setEditable(false);
        jsonText.setStyle("-fx-font-size: 13");
        result.setStyle("-fx-font-size: 13");
        jsonPath.setStyle("-fx-font-size: 13");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        jsonText.textProperty().addListener((observable, oldValue, newValue) -> listnerJson(gson, newValue));
        jsonPath.setOnKeyTyped(keyEvent -> listnerJsonPath(gson));
    }


    private void listnerJson(Gson gson, String newValue) {
        {
            result.setText("");
            try {
                json = gson.fromJson(newValue, JsonElement.class);
            } catch (Exception e) {
                result.setText("Не валидный верный Json!");
                return;
            }
            if (json != null) {
                if (!jsonPath.getText().isEmpty())
                    listnerJsonPath(gson);
                else
                    result.setText("Ведите JsonPath!");
            } else
                result.setText("Ведите Json!");
        }
    }

    private void listnerJsonPath(Gson gson) {
        if (json != null) {
            jsonText.setText(gson.toJson(json));
            try {
                Gson gsonResult = new GsonBuilder().setPrettyPrinting().create();
                String resultStr = JsonPath.getValue(json, jsonPath.getText());
                try {
                    JsonElement elem = gsonResult.fromJson(resultStr, JsonElement.class);
                    if (!elem.isJsonPrimitive())
                        resultStr = gsonResult.toJson(elem);
                } catch (Exception ignored) {
                }
                result.setText(resultStr);
            } catch (Exception e) {
                if (e.getMessage() != null)
                    result.setText(e.getMessage());
                else if (!jsonPath.getText().isEmpty())
                    result.setText("Не валидный JsonPath!");
                else
                    result.setText("Ведите JsonPath!");
            }
        }

    }
}
