package rip.bolt.nerve.document;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class DocumentGenerator implements InvocationHandler {

    private Object base, proxy;
    private LoadingCache<Method, MethodHandle> cache;

    public DocumentGenerator(Class<?> clazz, DocumentInformation info, Map<String, Object> data) {
        this.base = new Object();
        this.proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, this);
        this.cache = CacheBuilder.newBuilder().build(new CacheLoader<Method, MethodHandle>() {

            @Override
            public MethodHandle load(Method method) throws Exception {
                DocumentField field = info.find(method);
                if (field != null) {
                    if (method.getParameterCount() == 0)
                        return MethodHandles.constant(method.getReturnType(), data.get(field.getName()));
                }

                Class<?> declaring = method.getDeclaringClass();
                if (declaring == Object.class)
                    return MethodHandles.lookup().unreflect(method).bindTo(base);
                if (method.isDefault())
                    return MethodHandles.lookup().in(declaring).unreflectSpecial(method, declaring).bindTo(proxy);

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

}
