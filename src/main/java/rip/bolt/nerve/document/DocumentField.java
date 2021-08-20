package rip.bolt.nerve.document;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

public class DocumentField {

    private final String name;
    private Method getter, setter;
    private boolean nullable;

    public DocumentField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Method getGetter() {
        return getter;
    }

    public void setGetter(Method getter) {
        this.getter = getter;
        this.nullable = getter.getAnnotation(Nullable.class) != null;
    }

    public Method getSetter() {
        return setter;
    }

    public void setSetter(Method setter) {
        this.setter = setter;
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
