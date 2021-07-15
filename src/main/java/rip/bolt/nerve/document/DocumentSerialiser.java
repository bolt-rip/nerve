package rip.bolt.nerve.document;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DocumentSerialiser implements JsonSerializer<Document>, JsonDeserializer<Document> {

    @Override
    public JsonElement serialize(Document src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject data = new JsonObject();
        Class<?> type = (Class<?>) typeOfSrc;
        DocumentInformation info = DocumentInformation.of(type);

        for (DocumentField field : info.getFields()) {
            Object value;
            try {
                value = field.getGetter().invoke(src);
            } catch (Exception e) {
                throw new IllegalArgumentException("Exception thrown while serialising " + field.getName(), e);
            }

            data.add(field.getName(), context.serialize(value));
        }

        return data;
    }

    @Override
    public Document deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> data = new HashMap<String, Object>();
        JsonObject object = (JsonObject) element;
        Class<?> type = (Class<?>) typeOfT;
        DocumentInformation info = DocumentInformation.of(type);

        for (DocumentField field : info.getFields()) {
            Method method = field.getGetter();
            JsonElement value = object.get(field.getName());

            if (value == null || value.isJsonNull()) {
                if (!field.isNullable() || !method.isDefault())
                    throw new IllegalArgumentException("Missing value for " + field.getName());

                continue;
            }

            data.put(field.getName(), context.deserialize(value, method.getReturnType()));
        }

        return (Document) DocumentGenerator.generate(type, info, data);
    }

}
