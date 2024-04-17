package jsonpathui;

public class StateScene {

    private String json;

    private String jsonPath;

    private String tabName;

    public StateScene(String json, String jsonPath, String tabName) {
        this.json = json;
        this.jsonPath = jsonPath;
        this.tabName = tabName;
    }

    public String getJson() {
        return json;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public String getTabName() {
        return tabName;
    }
}
