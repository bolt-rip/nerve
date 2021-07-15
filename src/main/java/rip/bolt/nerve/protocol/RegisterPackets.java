package rip.bolt.nerve.protocol;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.StateRegistry;
import com.velocitypowered.proxy.protocol.StateRegistry.PacketMapping;
import com.velocitypowered.proxy.protocol.StateRegistry.PacketRegistry;

public class RegisterPackets {

    private Method registerPacket;
    private Constructor<PacketMapping> newPacketMapping;

    public RegisterPackets() {
        try {
            this.registerPacket = PacketRegistry.class.getDeclaredMethod("register", Class.class, Supplier.class, PacketMapping[].class);
            this.registerPacket.setAccessible(true);

            this.newPacketMapping = PacketMapping.class.getDeclaredConstructor(int.class, ProtocolVersion.class, ProtocolVersion.class, boolean.class);
            this.newPacketMapping.setAccessible(true);

            register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register() {
        register(StateRegistry.PLAY.serverbound, PlayerLookPacket.class, PlayerLookPacket::new, //
                map(0x05, ProtocolVersion.MINECRAFT_1_7_2, false), //
                map(0x0E, ProtocolVersion.MINECRAFT_1_9, false), //
                map(0x10, ProtocolVersion.MINECRAFT_1_12, false), //
                map(0x0F, ProtocolVersion.MINECRAFT_1_12_1, false), //
                map(0x12, ProtocolVersion.MINECRAFT_1_13, false), //
                map(0x13, ProtocolVersion.MINECRAFT_1_14, false), //
                map(0x14, ProtocolVersion.MINECRAFT_1_16, false), //
                map(0x13, ProtocolVersion.MINECRAFT_1_17, false) //
        );
        register(StateRegistry.PLAY.serverbound, PlayerPositionPacket.class, PlayerPositionPacket::new, //
                map(0x04, ProtocolVersion.MINECRAFT_1_7_2, false), //
                map(0x0C, ProtocolVersion.MINECRAFT_1_9, false), //
                map(0x0E, ProtocolVersion.MINECRAFT_1_12, false), //
                map(0x0D, ProtocolVersion.MINECRAFT_1_12_1, false), //
                map(0x10, ProtocolVersion.MINECRAFT_1_13, false), //
                map(0x11, ProtocolVersion.MINECRAFT_1_14, false), //
                map(0x12, ProtocolVersion.MINECRAFT_1_16, false), //
                map(0x11, ProtocolVersion.MINECRAFT_1_17, false) //
        );
        register(StateRegistry.PLAY.serverbound, PlayerPositionLookPacket.class, PlayerPositionLookPacket::new, //
                map(0x06, ProtocolVersion.MINECRAFT_1_7_2, false), //
                map(0x0D, ProtocolVersion.MINECRAFT_1_9, false), //
                map(0x0F, ProtocolVersion.MINECRAFT_1_12, false), //
                map(0x0E, ProtocolVersion.MINECRAFT_1_12_1, false), //
                map(0x11, ProtocolVersion.MINECRAFT_1_13, false), //
                map(0x12, ProtocolVersion.MINECRAFT_1_14, false), //
                map(0x13, ProtocolVersion.MINECRAFT_1_16, false), //
                map(0x12, ProtocolVersion.MINECRAFT_1_17, false) //
        );
        register(StateRegistry.PLAY.clientbound, DingSoundEffectPacket.class, DingSoundEffectPacket::new, //
                map(0x29, ProtocolVersion.MINECRAFT_1_7_2, false), //
                map(0x19, ProtocolVersion.MINECRAFT_1_9, false), //
                map(0x1A, ProtocolVersion.MINECRAFT_1_13, false), //
                map(0x19, ProtocolVersion.MINECRAFT_1_14, false), //
                map(0x1A, ProtocolVersion.MINECRAFT_1_15, false), //
                map(0x19, ProtocolVersion.MINECRAFT_1_16, false), //
                map(0x18, ProtocolVersion.MINECRAFT_1_16_2, false), //
                map(0x19, ProtocolVersion.MINECRAFT_1_17, false) //
        );
    }

    private <P extends MinecraftPacket> void register(PacketRegistry instance, Class<P> clazz, Supplier<P> supplier, PacketMapping... mappings) {
        try {
            this.registerPacket.invoke(instance, clazz, supplier, mappings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PacketMapping map(int id, ProtocolVersion version, boolean encodeOnly) {
        return map(id, version, null, encodeOnly);
    }

    private PacketMapping map(int id, ProtocolVersion version, ProtocolVersion lastValidProtocolVersion, boolean encodeOnly) {
        try {
            return newPacketMapping.newInstance(id, version, lastValidProtocolVersion, encodeOnly);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
