package org.soraworld.ranklist.manager;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.soraworld.hocon.node.FileNode;
import org.soraworld.hocon.node.Node;
import org.soraworld.hocon.node.NodeBase;
import org.soraworld.hocon.node.Setting;
import org.soraworld.ranklist.core.MonsterType;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Himmelt
 */
@MainManager
public class RankManager extends VManager {

    @Setting
    private final HashSet<String> types = new HashSet<>();
    @Setting
    private final HashSet<EntityType> bossTypes = new HashSet<>();
    @Setting
    private final HashSet<EntityType> monsterTypes = new HashSet<>();

    private static final String KILL_MONSTER_KEY = "killMonsters";
    private static final String ONLINE_KEY = "onlineMinutes";
    private static final String KILL_BOSS_KEY = "killBosses";
    private static final String DAMAGE_KEY = "damageAmount";

    private static final ConcurrentHashMap<String, ConcurrentHashMap<UUID, Long>> PLAYER_INFO_MAP = new ConcurrentHashMap<>();

    public RankManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
    }

    @Override
    public void afterLoad() {
        types.add(KILL_MONSTER_KEY);
        types.add(ONLINE_KEY);
        types.add(KILL_BOSS_KEY);
        types.add(DAMAGE_KEY);
        types.forEach(typ -> PLAYER_INFO_MAP.putIfAbsent(typ, new ConcurrentHashMap<>()));
        loadAllRank();
    }

    @Override
    public ChatColor defChatColor() {
        return ChatColor.YELLOW;
    }

    public void loadAllRank() {
        types.forEach(this::loadRank);
    }

    public void saveAllRank() {
        types.forEach(this::saveRank);
    }

    public void loadRank(String type) {
        if (types.contains(type)) {
            Path path = getPath().resolve("rank_" + type + ".conf");
            if (Files.notExists(path)) {
                saveRank(type);
                return;
            }
            FileNode node = new FileNode(path.toFile(), options);
            try {
                node.load(false);
                ConcurrentHashMap<UUID, Long> map = PLAYER_INFO_MAP.computeIfAbsent(type, typ -> new ConcurrentHashMap<>());
                map.clear();
                for (String uuid : node.keys()) {
                    Node base = node.get(uuid);
                    if (base instanceof NodeBase) {
                        map.put(UUID.fromString(uuid), ((NodeBase) base).getLong());
                    }
                }
            } catch (Exception e) {
                debug(e);
                console(ChatColor.RED + "Rank " + type + " file load exception !!!");
            }
        }
    }

    public void saveRank(String type) {
        if (types.contains(type)) {
            ConcurrentHashMap<UUID, Long> map = PLAYER_INFO_MAP.get(type);
            if (map != null && map.size() > 0) {
                FileNode node = new FileNode(getPath().resolve("rank_" + type + ".conf").toFile(), options);
                for (Map.Entry<UUID, Long> entry : map.entrySet()) {
                    node.add(entry.getKey().toString(), entry.getValue());
                }
                try {
                    node.save();
                } catch (Exception e) {
                    debug(e);
                    console(ChatColor.RED + "&cRank " + type + " file save exception !!!");
                }
            }
        }
    }

    public void updateDamage(UUID uuid, MonsterType type, double damage, boolean kill) {
        if (kill) {
            if (type == MonsterType.BOSS) {
                giveValue(uuid, KILL_BOSS_KEY, 1);
            } else if (type == MonsterType.MONSTER) {
                giveValue(uuid, KILL_MONSTER_KEY, 1);
            }
        }
        giveValue(uuid, DAMAGE_KEY, Math.round(damage));
    }

    public void giveValue(UUID uuid, String type, long value) {
        if (types.contains(type)) {
            ConcurrentHashMap<UUID, Long> map = PLAYER_INFO_MAP.computeIfAbsent(type, typ -> new ConcurrentHashMap<>());
            long target = map.computeIfAbsent(uuid, uid -> 0L) + value;
            map.put(uuid, target);
        }
    }

    public void updateGameTime(UUID uuid, long minutes) {
        giveValue(uuid, ONLINE_KEY, minutes);
    }

    public MonsterType getMonsterType(EntityType type) {
        if (bossTypes.contains(type)) {
            return MonsterType.BOSS;
        } else if (monsterTypes.contains(type)) {
            return MonsterType.MONSTER;
        } else {
            return MonsterType.NORMAL;
        }
    }

    public void showRank(CommandSender sender, String type, int page) {
        if (page < 1) {
            page = 1;
        }
        ConcurrentHashMap<UUID, Long> map = PLAYER_INFO_MAP.get(type);
        if (map != null) {
            ArrayList<Map.Entry<UUID, Long>> list = new ArrayList<>(map.entrySet());
            list.sort(Comparator.comparingLong(e -> -e.getValue()));
            sendKey(sender, "rankHead");
            for (int i = (page - 1) * 10; i < page * 10 && i < list.size(); i++) {
                Map.Entry<UUID, Long> entry = list.get(i);
                UUID uuid = entry.getKey();
                long value = entry.getValue();
                String player = Bukkit.getOfflinePlayer(uuid).getName();
                sendKey(sender, "rankLine", i + 1, player, value);
            }
            sendKey(sender, "rankFoot", page, list.size() / 10 + 1);
        } else {
            send(sender, "     Rank for " + type + " NOT exist !!!");
        }
    }
}
