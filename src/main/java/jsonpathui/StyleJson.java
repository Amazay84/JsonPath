package jsonpathui;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class StyleJson {
    public static StyleSpans<Collection<String>> highlight(String code) {
        JsonFactory jsonFactory = new JsonFactory();
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastPos = 0;
        try {
            JsonParser parser = jsonFactory.createParser(code);
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();
                int start = (int) parser.getTokenLocation().getCharOffset();
                int end = start + parser.getTextLength();

                if (jsonToken == JsonToken.VALUE_STRING || jsonToken == JsonToken.FIELD_NAME) {
                    end += 2;
                }

                String className = jsonTokenToClassName(jsonToken);
                if (!className.isEmpty()) {
                    Match m = new Match(className, start, end);
                    if (m.start > lastPos) {
                        int length = m.start - lastPos;
                        spansBuilder.add(Collections.emptyList(), length);
                    }

                    int length = m.end - m.start;
                    spansBuilder.add(Collections.singleton(m.kind), length);
                    lastPos = m.end;
                }
            }
        } catch (IOException ignore) {}
        if (lastPos == 0) {
            spansBuilder.add(Collections.emptyList(), code.length());
        }

        return spansBuilder.create();
    }

    public static String jsonTokenToClassName(JsonToken jsonToken) {
        if (jsonToken == null) {
            return "";
        }
        switch (jsonToken) {
            case FIELD_NAME:
                return "json-property";
            case VALUE_STRING:
                return "json-string";
            case VALUE_NUMBER_FLOAT:
            case VALUE_NUMBER_INT:
                return "json-number";
            case VALUE_TRUE:
            case VALUE_FALSE:
                return "json-boolean";
            case VALUE_NULL:
                return "json-null";
            default:
                return "";
        }
    }
}
