package org.soraworld.ranklist;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.soraworld.ranklist.expansion.RankExpansion;
import org.soraworld.ranklist.manager.RankManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

/**
 * @author Himmelt
 */
public final class RankList extends SpigotPlugin<RankManager> {

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
        Bukkit.getScheduler().runTaskAsynchronously(this, manager::loadOfflineInfo);
    }
}
