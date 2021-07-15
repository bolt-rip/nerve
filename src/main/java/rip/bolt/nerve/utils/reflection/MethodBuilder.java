package rip.bolt.nerve.utils.reflection;

import java.lang.reflect.Method;

public class MethodBuilder {

  private Method method;

  public MethodBuilder(Method method) {
    this.method = method;
  }

  public MethodBuilder setAccessible(boolean accessible) {
    method.setAccessible(accessible);
    return this;
  }

  public Object invoke(Object obj, Object... args) {
    try {
      return method.invoke(obj, args);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Method getMethod() {
    return method;
  }
}
