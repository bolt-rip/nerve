package rip.bolt.nerve.document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The name of this field in the document
 *
 * <p>
 * If absent, the name of the method is used.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentFieldName {

    public String value();

}
