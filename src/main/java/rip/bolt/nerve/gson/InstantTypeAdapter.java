package rip.bolt.nerve.gson;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Instant;

public class InstantTypeAdapter extends NullableTypeAdapter<Instant> {

    @Override
    public void writeValue(JsonWriter writer, Instant value) throws IOException {
        writer.value(value.toEpochMilli());
    }

    @Override
    public Instant readValue(JsonReader reader) throws IOException {
        return Instant.ofEpochMilli(reader.nextLong());
    }

}
