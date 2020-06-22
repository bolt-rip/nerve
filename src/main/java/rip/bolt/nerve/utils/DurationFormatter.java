package rip.bolt.nerve.utils;

public class DurationFormatter {

    public static String format(long duration) {
        return format(duration, true);
    }

    public static String format(long duration, boolean plural) {
        StringBuilder builder = new StringBuilder();
        long absSeconds = duration;

        long years = absSeconds / 31536000;
        long months = (absSeconds % 31536000) / 2628000;
        long days = (absSeconds % 2628000) / 86400;
        long hours = (absSeconds % 86400) / 3600;
        long minutes = (absSeconds % 3600) / 60;
        long seconds = absSeconds % 60;

        append(builder, years, "year", plural);
        append(builder, months, "month", plural);
        append(builder, days, "day", plural);
        append(builder, hours, "hour", plural);
        append(builder, minutes, "minute", plural);
        append(builder, seconds, "second", plural);

        builder.setLength(builder.length() - 1); // remove space at the end
        return builder.toString();
    }

    private static void append(StringBuilder builder, long time, String unit, boolean plural) {
        if (time == 0)
            return;

        builder.append(time).append(" ").append(unit);
        if (time > 1 && plural)
            builder.append("s");
        builder.append(" ");
    }

}
