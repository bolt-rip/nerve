package rip.bolt.nerve.api.definitions;

import java.util.UUID;

public class Punishment {

    private String player, punisher;
    private PunishmentType type;

    private String reason;
    private long startTime, duration;

    public Punishment() {

    }

    public Punishment(UUID player, UUID punisher, PunishmentType type, String reason, long startTime, long duration) {
        this.player = player.toString();
        this.punisher = punisher.toString();
        this.type = type;
        this.reason = reason;
        this.startTime = startTime;
        this.duration = duration;
    }

    public UUID getPlayer() {
        return UUID.fromString(player);
    }

    public UUID getPunisher() {
        return UUID.fromString(punisher);
    }

    public PunishmentType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public long getEndTime() {
        if (duration == -1)
            return Long.MAX_VALUE;

        return startTime + duration;
    }

}
