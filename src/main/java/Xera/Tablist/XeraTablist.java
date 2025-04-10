package Xera.Tablist;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class XeraTablist extends JavaPlugin implements Listener {
    public static long startTime;
    public static boolean hasPapi = false;
    private static XeraTablist instance;

    @Getter
    public String header;
    @Getter
    public String footer;

    @Override
    public void onLoad() {
        instance = this;
    }

    public static XeraTablist getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Logger log = getLogger();

        log.info("Loading config");
        saveDefaultConfig();
        loadConfig();

        startTime = System.currentTimeMillis();
        this.getCommand("tabrconfig").setExecutor(new ReloadCommand(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            hasPapi = true;
        }

        // Folia-compatible scheduler
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(this, task -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Bukkit.getRegionScheduler().run(this, player.getLocation(), scheduledTask -> {
                    Tablist.sendTablist(player);
                });
            }
        }, 0L, getConfig().getInt("delay"));

        log.info("XeraTablist enabled");
    }

    public static String parseText(Player player, String text) {
        int ping = player.getPing();

        // PAPI
        if (hasPapi) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        // ChatColor
        text = ChatColor.translateAlternateColorCodes('&', text);

        // Custom Placeholders
        text = text
                .replace("%tps%", TabUtil.getTps())
                .replace("%ping%", String.valueOf(ping))
                .replace("%uptime%", TabUtil.getFormattedInterval(System.currentTimeMillis() - XeraTablist.startTime))
                .replace("%players%", String.valueOf(Bukkit.getOnlinePlayers().size()));

        return text;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        loadConfig();
    }

    public void loadConfig() {
        StringBuilder header = new StringBuilder();
        StringBuilder footer = new StringBuilder();
        List<String> headerList = getConfig().getStringList("tablist.header");
        List<String> footerList = getConfig().getStringList("tablist.footer");

        for (int i = 0; i < headerList.size(); i++) {
            header.append(headerList.get(i));
            if (i != (headerList.size() - 1)) header.append("\n");
        }

        for (int i = 0; i < footerList.size(); i++) {
            footer.append(footerList.get(i));
            if (i != (footerList.size() - 1)) footer.append("\n");
        }

        this.header = header.toString();
        this.footer = footer.toString();
    }
}
