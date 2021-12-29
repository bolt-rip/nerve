package rip.bolt.nerve.utils.reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldBuilder {

  private Field field;
  private Map<Object, List<Object>> pushedMap;

  public FieldBuilder(Field field) {
    this.field = field;
    this.pushedMap = new HashMap<Object, List<Object>>();
  }

  public FieldBuilder setAccessible(boolean accessible) {
    field.setAccessible(accessible);
    return this;
  }

  public Object get(Object obj) {
    try {
      return field.get(obj);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void set(Object obj, Object value) {
    try {
      field.set(obj, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void push(Object obj, Object value) {
    try {
      List<Object> pushes = pushedMap.getOrDefault(obj, new ArrayList<Object>());
      pushes.add(get(obj));
      pushedMap.put(obj, pushes);

      field.set(obj, value);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Object pop(Object obj) {
    try {
      List<Object> pushes = pushedMap.get(obj);
      if (pushes == null || pushes.size() == 0)
        throw new IllegalArgumentException("you must push before popping!");

      Object value = pushes.remove(pushes.size() - 1);

      field.set(obj, value);
      return value;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public Field getField() {
    return field;
  }
}
