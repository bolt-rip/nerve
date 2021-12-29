package rip.bolt.nerve.utils;

public class ClassUtils {

  /**
   * Finds the 'youngest' interface (assuming a superclass is a parent) in a class' superclasses &
   * interfaces
   *
   * @param needle The interface implementing the needle to look for
   * @param haystack The class whose superclasses & interfaces should be searched
   * @return The 'youngest' class implementing the needle
   */
  public static Class<?> findYoungestInterface(Class<?> needle, Class<?> haystack) {
    if (haystack.isInterface()) return haystack;

    Class<?> superclass = haystack;
    while (superclass != null) {
      for (Class<?> iface : superclass.getInterfaces()) {
        if (needle.isAssignableFrom(iface)) return iface;
      }
      superclass = superclass.getSuperclass();
    }

    return null;
  }

  public static Class<?> findClassExtendingSuperclass(Class<?> superclass, Class<?> haystack) {
    Class<?> current = haystack;
    while (current != null) {
      if (superclass.equals(current.getSuperclass())) return current;
    }

    return null;
  }
}
