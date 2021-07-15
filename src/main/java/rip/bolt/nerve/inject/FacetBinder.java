package rip.bolt.nerve.inject;

import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public class FacetBinder {

    private Binder binder;
    private Multibinder<Facet> multibinder;

    public FacetBinder(Binder binder) {
        this.binder = binder;
        this.multibinder = Multibinder.newSetBinder(binder, Facet.class);
    }

    public void register(Class<? extends Facet> listener) {
        this.multibinder.addBinding().to(listener);
        this.binder.bind(listener).in(Singleton.class);
    }

}
