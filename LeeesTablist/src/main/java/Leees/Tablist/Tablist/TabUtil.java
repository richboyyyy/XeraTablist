package Leees.Tablist.Tablist;

import net.minecraft.server.v1_12_R1.MinecraftServer;

public class TabUtil {
    private static String format(double tps) {
        return (tps > 18.0D ? "§a" : (tps > 16.0D ? "§e" : "§c")).toString() + (tps > 20.0D ? "" : "") + String.format("%.2f", Math.min((double)Math.round(tps * 100.0D) / 100.0D, 20.0D));
    }

    public static String getTps() {
        return format(MinecraftServer.getServer().recentTps[0]);
    }

    public static String GetFormattedInterval(long ms) {
        long seconds = ms / 1000L % 60L;
        long minutes = ms / 60000L % 60L;
        long hours = ms / 3600000L % 24L;
        long days = ms / 86400000L;
        return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
    }
}
