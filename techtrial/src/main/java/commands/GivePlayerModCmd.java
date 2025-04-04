package commands;

import creamy.plugins.techtrial.TechTrialPlugin;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.UUID;




public class GivePlayerModCmd implements CommandExecutor {
    private final TechTrialPlugin plugin;

    public GivePlayerModCmd(TechTrialPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender,@NotNull Command command,@NotNull String s, String[] strings) {

        if (!(commandSender instanceof Player player)) return true;
        if (!player.hasPermission("techtrial.moderator")) return true;
        if (strings.length != 1) {
            player.sendMessage("Usage: /modgrant <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(strings[0]);
        if (target == null) {
            player.sendMessage("Target player is not online.");
            return true;
        }

        UUID targetUUID = target.getUniqueId();
        FileConfiguration config = plugin.getConfig();
        List<String> mods = config.getStringList("moderators");

        if (mods.contains(targetUUID.toString())) {
            player.sendMessage("Removing moderator flags from player.");
            mods.remove(targetUUID.toString());
            config.set("moderators", mods);
            TechTrialPlugin.getLuckperms().getUserManager().modifyUser(targetUUID, user -> {
                user.data().remove(Node.builder("techtrial.moderator").build());
            });

            plugin.saveConfig();
            plugin.reloadConfig();
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage("You lack the permission to use this command.");
            return true;
        }

        mods.add(targetUUID.toString());
        config.set("moderators", mods);

        TechTrialPlugin.getLuckperms().getUserManager().modifyUser(targetUUID, user -> {
            user.data().add(Node.builder("techtrial.moderator").build());
        });

        plugin.saveConfig();
        plugin.reloadConfig();

        player.sendMessage("Granting moderator flags to " + target.getName());
        target.sendMessage("You have received moderator flags.");

        return true;
    }
}
