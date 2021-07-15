package rip.bolt.nerve.utils.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class Reflect {

  private Class<?> target;

  private Reflect(Class<?> target) {
    this.target = target;
  }

  public MethodBuilder findMethod(Method method) {
    return findMethod(method.getName(), method.getParameterTypes());
  }

  public MethodBuilder findMethod(String name, Class<?>... args) {
    Method method = find(name, args);
    if (method == null) return null;

    return new MethodBuilder(method);
  }

  private Method find(String name, Class<?>[] parameterTypes) {
    Class<?> superclass = target;
    while (superclass != null) {
      try {
        return superclass.getDeclaredMethod(name, parameterTypes);
      } catch (NoSuchMethodException e) {
        superclass = superclass.getSuperclass();
      } catch (SecurityException e) {
        e.printStackTrace();
        break;
      }
    }

    return null;
  }

  public FieldBuilder findField(Field field) {
    return findField(field.getName());
  }

  public FieldBuilder findField(String name) {
    Field field = find(name);
    if (field == null) return null;

    return new FieldBuilder(field);
  }

  private Field find(String name) {
    Class<?> superclass = target;
    while (superclass != null) {
      try {
        return superclass.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        superclass = superclass.getSuperclass();
      } catch (SecurityException e) {
        e.printStackTrace();
        break;
      }
    }

    return null;
  }

  public static Reflect target(Class<?> target) {
    return new Reflect(Objects.requireNonNull(target, "target cannot be null!"));
  }
}
