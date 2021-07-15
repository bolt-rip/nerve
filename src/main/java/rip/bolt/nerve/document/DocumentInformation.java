package rip.bolt.nerve.document;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import rip.bolt.nerve.utils.ClassUtils;

public class DocumentInformation {

    private List<DocumentField> fields;

    private DocumentInformation(Class<?> clazz) {
        this.fields = new ArrayList<DocumentField>();
        Class<?> iface = ClassUtils.findYoungestInterface(Document.class, clazz);
        Objects.requireNonNull(iface, "Could not find interface extending or class implementing Message for " + clazz.getName());

        for (Method method : iface.getMethods()) {
            if (method.isSynthetic() || Modifier.isStatic(method.getModifiers())) // we only want data
                continue;

            fields.add(new DocumentField(method));
        }
    }

    public List<DocumentField> getFields() {
        return fields;
    }

    private static final LoadingCache<Class<?>, DocumentInformation> data = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, DocumentInformation>() {
        @Override
        public DocumentInformation load(Class<?> key) throws Exception {
            return new DocumentInformation(key);
        }
    });

    public static DocumentInformation of(Class<?> clazz) {
        return data.getUnchecked(clazz);
    }

    public DocumentField find(String name) {
        return fields.stream().filter(field -> field.getName().equals(name)).findFirst().orElse(null);
    }

    public DocumentField find(Method method) {
        return find(DocumentField.getFieldName(method));
    }

}
