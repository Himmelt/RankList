package org.soraworld.ranklist;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.ranklist.expansion.RankExpansion;
import org.soraworld.ranklist.manager.RankManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Himmelt
 */
public final class RankList extends SpigotPlugin<RankManager> {

    private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("OnlineTimer-%d").build();
    private final ScheduledExecutorService timerService = new ScheduledThreadPoolExecutor(1, threadFactory);

    @Override
    public void afterEnable() {
        try {
            PlaceholderAPIPlugin placeholderPlugin = (PlaceholderAPIPlugin) Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
            if (placeholderPlugin != null && placeholderPlugin.getExpansionManager().registerExpansion(RankExpansion.class.getConstructor(RankManager.class).newInstance(manager))) {
                manager.consoleKey("placeholder.expansionSuccess");
            } else {
                manager.consoleKey("placeholder.expansionFailed");
            }
        } catch (Throwable ignored) {
            manager.console(ChatColor.RED + "RankExpansion Construct Instance failed !!!");
            manager.consoleKey("placeholder.notHook");
        }
        timerService.scheduleAtFixedRate(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                manager.updateGameTime(uuid, 1);
                System.out.println("updateGameTime");
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void beforeDisable() {
        manager.saveAllRank();
        timerService.shutdown();
    }
}
