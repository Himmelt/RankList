package org.soraworld.ranklist.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.soraworld.ranklist.manager.RankManager;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Himmelt
 */
@EventListener
public class RankListener implements Listener {
    @Inject
    private RankManager manager;
    private static final HashMap<UUID, Long> JOIN_TIME = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        if (damager instanceof Player) {
            UUID uuid = damager.getUniqueId();
            double damage = event.getFinalDamage();
            Bukkit.getScheduler().runTaskAsynchronously(manager.getPlugin(), () -> {
                RankManager.updateDamage(uuid, manager.getMonsterType(damagee.getType()), damage, damage >= ((Creature) damagee).getHealth());
            });
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Bukkit.getScheduler().runTask(manager.getPlugin(), () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                JOIN_TIME.put(uuid, System.currentTimeMillis());
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (JOIN_TIME.containsKey(uuid)) {
            long time = System.currentTimeMillis() - JOIN_TIME.remove(uuid);
            Bukkit.getScheduler().runTaskAsynchronously(manager.getPlugin(), () -> RankManager.updateGameTime(uuid, time / 60000));
        }
    }
}
