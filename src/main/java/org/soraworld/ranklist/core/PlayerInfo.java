package org.soraworld.ranklist.core;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * @author Himmelt
 */
public class PlayerInfo implements Comparable<PlayerInfo> {
    private long value = 0;
    private UUID uuid = null;

    public PlayerInfo(UUID uuid, long value) {
        this.uuid = uuid;
        this.value = value;
    }

    @Override
    public int compareTo(@NotNull PlayerInfo info) {
        return uuid == info.uuid ? 0 : this.value > info.value ? -1 : 1;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof PlayerInfo && this.uuid.equals(((PlayerInfo) obj).uuid);
    }

    public String getName() {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        if (op != null) {
            return op.getName();
        } else {
            return "UnknownPlayer";
        }
    }

    public long getValue() {
        return value;
    }
}
