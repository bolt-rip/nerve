package rip.bolt.nerve.protocol;

import com.velocitypowered.proxy.connection.client.ConnectedPlayer;

import rip.bolt.nerve.utils.Vector;

public class PlayerPacketHandler {

    private ConnectedPlayer player;
    private Vector position;
    private float pitch, yaw;

    public PlayerPacketHandler(ConnectedPlayer player) {
        this.player = player;
    }

    public void handle(PlayerLookPacket packet) {
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
    }

    public void handle(PlayerPositionPacket packet) {
        this.position = packet.getPosition();
    }

    public void handle(PlayerPositionLookPacket packet) {
        this.position = packet.getPosition();
        this.yaw = packet.getYaw();
        this.pitch = packet.getPitch();
    }

    public void playDing(float volume, float pitch) {
        player.getConnection().write(new DingSoundEffectPacket(position.x, position.y, position.z, volume, pitch));
    }

    public ConnectedPlayer getPlayer() {
        return player;
    }

    public Vector getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

}
