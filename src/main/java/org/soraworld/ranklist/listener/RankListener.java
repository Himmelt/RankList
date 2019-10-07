package org.soraworld.ranklist.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.soraworld.ranklist.manager.RankManager;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;

import java.util.UUID;

/**
 * @author Himmelt
 */
@EventListener
public class RankListener implements Listener {
    @Inject
    private RankManager manager;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        if (damager instanceof Player) {
            UUID uuid = damager.getUniqueId();
            double damage = event.getFinalDamage();
            Bukkit.getScheduler().runTaskAsynchronously(manager.getPlugin(), () -> manager.updateDamage(uuid, manager.getMonsterType(damagee.getType()), damage, damage >= ((Creature) damagee).getHealth()));
        }
    }
}
