package rip.bolt.nerve.gson;

import java.time.Instant;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import rip.bolt.nerve.document.Document;
import rip.bolt.nerve.document.DocumentSerialiser;

public class GsonModule extends AbstractModule {

    @Provides
    @Singleton
    public Gson gson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, new InstantTypeAdapter());
        builder.registerTypeHierarchyAdapter(Document.class, new DocumentSerialiser());

        return builder.create();
    }

}
