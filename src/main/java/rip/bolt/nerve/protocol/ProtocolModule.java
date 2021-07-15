package rip.bolt.nerve.protocol;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import rip.bolt.nerve.inject.FacetBinder;

public class ProtocolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RegisterPackets.class).in(Singleton.class);
        bind(PacketHandlerTracker.class).in(Singleton.class);

        binder().requestStaticInjection(PlayerLookPacket.class);
        binder().requestStaticInjection(PlayerPositionPacket.class);
        binder().requestStaticInjection(PlayerPositionLookPacket.class);

        FacetBinder listener = new FacetBinder(binder());
        listener.register(PacketHandlerTracker.class);
    }

}
