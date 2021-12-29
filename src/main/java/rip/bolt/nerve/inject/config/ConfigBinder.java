package rip.bolt.nerve.inject.config;

import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.gson.Gson;
import com.google.inject.Binder;
import com.google.inject.Singleton;

import rip.bolt.nerve.config.Config;
import rip.bolt.nerve.config.ConfigSection;
import rip.bolt.nerve.document.Document;

public class ConfigBinder {

    private Binder binder;

    public ConfigBinder(Binder binder) {
        this.binder = binder;
    }

    public <T extends Document> void register(Class<T> clazz) {
        binder.bind(clazz).toProvider(new ConfigProvider<T>(clazz)).in(Singleton.class);
    }

    private static class ConfigProvider<T extends Document> implements Provider<T> {

        private Gson gson;
        private Config config;
        private Class<T> clazz;

        public ConfigProvider(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Inject
        public void init(Gson gson, Config config) {
            this.gson = gson;
            this.config = config;
        }

        @Override
        public T get() {
            String sectionName = clazz.getAnnotation(Section.class).value();
            ConfigSection section = Objects.requireNonNull(config.getSection(sectionName), "section " + sectionName + " missing from config!");

            return gson.fromJson(gson.toJsonTree(section.getData()), clazz);
        }

    }

}
