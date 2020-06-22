package rip.bolt.nerve.utils;

public class DurationFormatter {

    public static String format(long duration) {
        StringBuilder builder = new StringBuilder();
        long absSeconds = duration;

        long years = absSeconds / 31536000;
        long months = (absSeconds % 31536000) / 2628000;
        long days = (absSeconds % 2628000) / 86400;
        long hours = (absSeconds % 86400) / 3600;
        long minutes = (absSeconds % 3600) / 60;
        long seconds = absSeconds % 60;

        append(builder, years, "year");
        append(builder, months, "month");
        append(builder, days, "day");
        append(builder, hours, "hour");
        append(builder, minutes, "minute");
        append(builder, seconds, "second");

        return builder.toString();
    }

    private static void append(StringBuilder builder, long time, String unit) {
        if (time == 0)
            return;

        builder.append(time).append(" ").append(unit);
        if (time > 1)
            builder.append("s");
        builder.append(" ");
    }

}
