package rip.bolt.nerve.protocol;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import com.velocitypowered.proxy.protocol.ProtocolUtils.Direction;

import io.netty.buffer.ByteBuf;

public class DingSoundEffectPacket implements MinecraftPacket {

    private String sound;
    private int category;
    private double x, y, z;
    private float volume, pitch;
    private boolean ding;

    public DingSoundEffectPacket() {

    }

    public DingSoundEffectPacket(double x, double y, double z, float volume, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.volume = volume;
        this.pitch = pitch;

        this.category = 4;
        this.ding = true;
    }

    @Override
    public void decode(ByteBuf buf, Direction direction, ProtocolVersion version) {
        ding = false;
        sound = ProtocolUtils.readString(buf);
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) > 0)
            category = ProtocolUtils.readVarInt(buf);
        x = buf.readInt() / 8D;
        y = buf.readInt() / 8D;
        z = buf.readInt() / 8D;

        volume = buf.readFloat();
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_10) < 0)
            pitch = buf.readUnsignedByte() / 63F;
        else
            pitch = buf.readFloat();
    }

    @Override
    public void encode(ByteBuf buf, Direction direction, ProtocolVersion version) {
        if (ding)
            sound = getDingNameForVersion(version);

        ProtocolUtils.writeString(buf, sound);
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_8) > 0)
            ProtocolUtils.writeVarInt(buf, category);
        buf.writeInt((int) (x * 8));
        buf.writeInt((int) (y * 8));
        buf.writeInt((int) (z * 8));

        buf.writeFloat(volume);
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_10) < 0)
            buf.writeByte((byte) (pitch * 63) & 0xFF);
        else
            buf.writeFloat(pitch);
    }

    @Override
    public boolean handle(MinecraftSessionHandler handler) {
        return false;
    }

    private String getDingNameForVersion(ProtocolVersion version) {
        if (version.compareTo(ProtocolVersion.MINECRAFT_1_9) < 0)
            return "random.orb";
        return "entity.experience_orb.pickup";
    }

}
