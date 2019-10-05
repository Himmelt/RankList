package org.soraworld.ranklist.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.ranklist.manager.RankManager;
import org.soraworld.violet.command.Sub;
import org.soraworld.violet.command.SubExecutor;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Inject;

/**
 * @author Himmelt
 */
@Command(name = "ranklist", usage = "/rank ", aliases = {"rank"})
public final class CommandRank {

    @Inject
    private RankManager manager;

    @Sub(perm = "admin", usage = "/rank give <player> <type> <value>")
    public final SubExecutor give = (cmd, sender, args) -> {
        if (args.size() == 3) {
            Player player = Bukkit.getPlayer(args.first());
            if (player != null) {
                try {
                    manager.giveValue(player.getUniqueId(), args.get(1), Integer.parseInt(args.get(2)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else {
            cmd.sendUsage(sender);
        }
    };

    @Sub(perm = "admin", usage = "/rank take <player> <type> <value>")
    public final SubExecutor take = (cmd, sender, args) -> {
        if (args.size() == 3) {
            Player player = Bukkit.getPlayer(args.first());
            if (player != null) {
                try {
                    manager.giveValue(player.getUniqueId(), args.get(1), -Integer.parseInt(args.get(2)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else {
            cmd.sendUsage(sender);
        }
    };

    @Sub(path = "save.rank", perm = "admin", usage = "/rank save rank")
    public final SubExecutor save_rank = (cmd, sender, args) -> {
        Bukkit.getScheduler().runTaskAsynchronously(manager.getPlugin(), manager::saveAllRank);
    };

    @Sub(usage = "/rank info <type> [page]")
    public final SubExecutor info = (cmd, sender, args) -> {
        if (args.notEmpty()) {
            String type = args.first();
            int page = 1;
            if (args.size() >= 2) {
                try {
                    page = Integer.parseInt(args.get(1));
                } catch (Throwable ignored) {
                }
            }
            manager.showRank(sender, type, page);
        } else {
            manager.sendKey(sender, "emptyArgs");
        }
    };
}
