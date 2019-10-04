package org.soraworld.ranklist.manager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.soraworld.hocon.node.Setting;
import org.soraworld.ranklist.core.MonsterType;
import org.soraworld.ranklist.core.PlayerInfo;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Himmelt
 */
@MainManager
public class RankManager extends VManager {

/*杀怪排行榜
boss击杀数量排行榜
在线时间排行榜
造成伤害排行榜
*/

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

    private static final ConcurrentSkipListSet<String> TYPES = new ConcurrentSkipListSet<>();
    private static final ConcurrentHashMap<String, ConcurrentSkipListSet<PlayerInfo>> RANK_INFO_MAP = new ConcurrentHashMap<>();

    public RankManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
    }

    @Override
    public void afterLoad() {
        types.add(KILL_MONSTER_KEY);
        types.add(ONLINE_KEY);
        types.add(KILL_BOSS_KEY);
        types.add(DAMAGE_KEY);
        TYPES.clear();
        TYPES.addAll(types);
        TYPES.forEach(typ -> RANK_INFO_MAP.putIfAbsent(typ, new ConcurrentSkipListSet<>()));
    }

    @Override
    public ChatColor defChatColor() {
        return ChatColor.YELLOW;
    }

    public void loadOfflineInfo() {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
        if (offlinePlayers != null && offlinePlayers.length > 0) {
            for (OfflinePlayer op : offlinePlayers) {
                System.out.println(op.getName());
                UUID uuid = op.getUniqueId();
                System.out.println(uuid);
                for (String type : types) {
                    System.out.println(type);
                    long value = DataAPI.getStoreLong(uuid, "ranklist." + type);
                    System.out.println(value);
                    if (value > 0) {
                        updateInfo(uuid, type, value);
                    }
                }
            }
        }
    }

    public static void updateDamage(UUID uuid, MonsterType type, double damage, boolean kill) {
        if (kill) {
            if (type == MonsterType.BOSS) {
                giveValue(uuid, KILL_BOSS_KEY, 1);
            } else if (type == MonsterType.MONSTER) {
                giveValue(uuid, KILL_MONSTER_KEY, 1);
            }
        }
        giveValue(uuid, DAMAGE_KEY, Math.round(damage));
    }

    public static void giveValue(UUID uuid, String type, long value) {
        if (TYPES.contains(type)) {
            long target = DataAPI.getStoreLong(uuid, "ranklist." + type) + value;
            DataAPI.setStoreLong(uuid, "ranklist." + type, target);
            updateInfo(uuid, type, target);
        }
    }

    private static void updateInfo(UUID uuid, String type, long value) {
        if (TYPES.contains(type)) {
            ConcurrentSkipListSet<PlayerInfo> set = RANK_INFO_MAP.computeIfAbsent(type, typ -> new ConcurrentSkipListSet<>());
            PlayerInfo info = new PlayerInfo(uuid, value);
            boolean ok = set.remove(info);
            System.out.println(ok);
            set.add(info);
            while (set.size() > 100) {
                set.remove(set.last());
            }
        }
    }

    public static void updateGameTime(UUID uuid, long minutes) {
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
        ConcurrentSkipListSet<PlayerInfo> rank = RANK_INFO_MAP.get(type);
        if (rank != null) {
            Iterator<PlayerInfo> it = rank.iterator();
            sendKey(sender, "rankHead");
            for (int i = 1; i <= page * 10 && it.hasNext(); i++) {
                PlayerInfo info = it.next();
                if (i >= page * 10 - 9) {
                    sendKey(sender, "rankLine", i, info.getName(), info.getValue());
                }
            }
            sendKey(sender, "rankFoot", page, rank.size() / 10 + 1);
        } else {
            send(sender, "          Rank for " + type + " NOT exist !!!");
        }
    }
}
