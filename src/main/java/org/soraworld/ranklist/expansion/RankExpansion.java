package org.soraworld.ranklist.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.soraworld.ranklist.manager.RankManager;

/**
 * @author Himmelt
 */
public class RankExpansion extends PlaceholderExpansion {

    private final RankManager manager;

    public RankExpansion(RankManager manager) {
        this.manager = manager;
    }

    @Override
    public String getIdentifier() {
        return manager.getPlugin().getId();
    }

    @Override
    public String getAuthor() {
        return "Himmelt";
    }

    @Override
    public String getVersion() {
        return manager.getPlugin().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        return "";
    }
}
