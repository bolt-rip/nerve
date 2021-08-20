package rip.bolt.nerve.document;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class DocumentGenerator implements InvocationHandler {

    private Object base, proxy;
    private Map<String, Object> data;
    private LoadingCache<Method, MethodHandle> cache;

    private static Constructor<MethodHandles.Lookup> lookupConstructor;
    private static MethodHandle SETTER_SET;

    static {
        try {
            SETTER_SET = MethodHandles.lookup().findVirtual(Setter.class, "set", MethodType.methodType(void.class, Object.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DocumentGenerator(Class<?> clazz, DocumentInformation info, Map<String, Object> data) {
        this.base = new Object();
        this.proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
        this.data = data;

        this.cache = CacheBuilder.newBuilder().build(new CacheLoader<Method, MethodHandle>() {

            @Override
            public MethodHandle load(Method method) throws Exception {
                DocumentField field = info.find(method);

                if (field != null) {
                    Object storedData = data.get(field.getName());
                    if (storedData != null && method.equals(field.getGetter()))
                        return MethodHandles.constant(method.getReturnType(), storedData);
                    else if (method.equals(field.getSetter()))
                        return SETTER_SET.bindTo(new Setter(field));
                }

                Class<?> declaring = method.getDeclaringClass();
                if (declaring == Object.class)
                    return MethodHandles.lookup().unreflect(method).bindTo(base);
                if (method.isDefault())
                    return lookup(declaring).unreflectSpecial(method, declaring).bindTo(proxy);

                if (field.isNullable())
                    return MethodHandles.constant(method.getReturnType(), null);

                throw new IllegalArgumentException(method.getName() + " in " + method.getDeclaringClass().getName() + " has no implementation for " + clazz.getName() + ".");
            }
        });
    }

    public static Object generate(Class<?> clazz, DocumentInformation info, Map<String, Object> data) {
        return new DocumentGenerator(clazz, info, data).proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return cache.getUnchecked(method).invokeWithArguments(args);
    }

    private static MethodHandles.Lookup lookup(Class<?> clazz) {
        try {
            if (lookupConstructor == null) {
                lookupConstructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookupConstructor.setAccessible(true);
            }

            return lookupConstructor.newInstance(clazz, MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected class Setter {

        private DocumentField field;

        public Setter(DocumentField field) {
            this.field = field;
        }

        public void set(Object object) {
            DocumentGenerator.this.set(field, object);
        }

    }

    public void set(DocumentField field, Object value) {
        data.put(field.getName(), value);
        cache.invalidate(field.getGetter());
    }

}
