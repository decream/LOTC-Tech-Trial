package commands;

import creamy.plugins.techtrial.TechTrialPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ToggleTrampleCmd implements CommandExecutor {
    private final TechTrialPlugin plugin;

    public ToggleTrampleCmd(TechTrialPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command cmd,@NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (!player.hasPermission("techtrial.moderator")) {
            player.sendMessage("Error. You do not have permission to execute this command.");
            return true;
        }

        String uuid = Objects.requireNonNull(target).getUniqueId().toString();
        FileConfiguration config = plugin.getConfig();
        boolean current = config.getBoolean("cropProtection." + uuid, false);
        boolean updated = !current;

        config.set("cropProtection." + uuid, updated);
        plugin.saveConfig();

        String status = updated ? "disabled" : "enabled";
        if (updated) {
            player.sendMessage("Crop trampling " + status + " for " + target);
        } else {
            player.sendMessage("Crop trampling enabled for " + status + " for " + target);
        }

    return true;
    }
}
