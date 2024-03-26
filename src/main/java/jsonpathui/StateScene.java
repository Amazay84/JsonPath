package jsonpathui;

public class StateScene {

    private String json;

    private String result;

    private String jsonPath;

    private String tabName;

    public StateScene(String json, String result, String jsonPath, String tabName) {
        this.json = json;
        this.result = result;
        this.jsonPath = jsonPath;
        this.tabName = tabName;
    }

    public String getJson() {
        return json;
    }

    public String getResult() {
        return result;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getTabName() {
        return tabName;
    }
}
