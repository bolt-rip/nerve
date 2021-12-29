package rip.bolt.nerve.protocol;

import javax.inject.Inject;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils.Direction;

import io.netty.buffer.ByteBuf;
import rip.bolt.nerve.utils.Vector;

public class PlayerPositionLookPacket implements MinecraftPacket {

    private Vector position;
    private float yaw, pitch;
    private double headY;
    private boolean onGround;

    @Inject private static PacketHandlerTracker tracker;

    @Override
    public void decode(ByteBuf buf, Direction direction, ProtocolVersion version) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) < 0)
            headY = buf.readDouble(); // head Y
        double z = buf.readDouble();

        yaw = buf.readFloat();
        pitch = buf.readFloat();

        position = new Vector(x, y, z);
        onGround = buf.readBoolean();
    }

    @Override
    public void encode(ByteBuf buf, Direction direction, ProtocolVersion version) {
        buf.writeDouble(position.x);
        buf.writeDouble(position.y);
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) < 0)
            buf.writeDouble(headY);
        buf.writeDouble(position.z);

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

    public Vector getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public double getHeadY() {
        return headY;
    }

    public boolean isOnGround() {
        return onGround;
    }

}
