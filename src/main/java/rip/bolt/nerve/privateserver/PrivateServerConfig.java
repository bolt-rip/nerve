package rip.bolt.nerve.privateserver;

import rip.bolt.nerve.document.Document;
import rip.bolt.nerve.document.DocumentFieldName;
import rip.bolt.nerve.inject.config.Section;

@Section("private-server-messages")
public interface PrivateServerConfig extends Document {

    @DocumentFieldName("no-perms-message")
    String no_perms_message();

    @DocumentFieldName("no-perms-link")
    String no_perms_link();

}
