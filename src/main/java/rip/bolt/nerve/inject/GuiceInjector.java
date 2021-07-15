package rip.bolt.nerve.inject;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

public class GuiceInjector implements com.sk89q.minecraft.util.commands.Injector {

    public com.google.inject.Injector injector;

    @Inject
    public GuiceInjector(com.google.inject.Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object getInstance(Class<?> clazz) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        return injector.getInstance(clazz);
    }

}
