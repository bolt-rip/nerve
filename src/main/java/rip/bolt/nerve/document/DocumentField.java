package rip.bolt.nerve.document;

import java.lang.reflect.Method;
import javax.annotation.Nullable;

public class DocumentField {

    private final String name;
    private final Method method;
    private final boolean nullable;

    public DocumentField(Method method) {
        this.name = getFieldName(method);
        this.method = method;
        this.nullable = method.getAnnotation(Nullable.class) != null;
    }

    public String getName() {
        return name;
    }

    public Method getGetter() {
        return method;
    }

    public boolean isNullable() {
        return nullable;
    }

    public static String getFieldName(Method method) {
        DocumentFieldName name = method.getAnnotation(DocumentFieldName.class);
        if (name == null)
            return method.getName();

        return name.value();
    }

}
