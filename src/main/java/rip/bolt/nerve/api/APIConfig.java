package rip.bolt.nerve.api;

import rip.bolt.nerve.document.Document;
import rip.bolt.nerve.inject.config.Section;

@Section("api")
public interface APIConfig extends Document {

    String url();

    String key();

}
