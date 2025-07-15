package rip.bolt.nerve.protocol;

import javax.inject.Inject;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils.Direction;

import io.netty.buffer.ByteBuf;

public class PlayerLookPacket implements MinecraftPacket {

    private float yaw, pitch;
    private boolean onGround;

    @Inject private static PacketHandlerTracker tracker;

    @Override
    public void decode(ByteBuf buf, Direction direction, ProtocolVersion version) {
        yaw = buf.readFloat();
        pitch = buf.readFloat();
        onGround = buf.readBoolean();
    }

    @Override
    public void encode(ByteBuf buf, Direction direction, ProtocolVersion version) {
        buf.writeFloat(yaw);
        buf.writeFloat(pitch);
        buf.writeBoolean(onGround);
    }

    @Override
    public boolean handle(MinecraftSessionHandler handler) {
        if (handler instanceof ClientPlaySessionHandler)
            tracker.get(handler).handle(this);

        return false;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

}
