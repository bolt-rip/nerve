package rip.bolt.nerve.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public abstract class NullableTypeAdapter<T> extends TypeAdapter<T> {

    public abstract void writeValue(JsonWriter writer, T value) throws IOException;

    public abstract T readValue(JsonReader reader) throws IOException;

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null)
            out.nullValue();
        else
            writeValue(out, value);
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL)
            return null;
        else
            return readValue(in);
    }

}
