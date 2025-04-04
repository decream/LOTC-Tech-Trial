package creamy.plugins.techtrial;

import commands.GivePlayerModCmd;
import commands.ToggleTrampleCmd;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class TechTrialPlugin extends JavaPlugin {

    public static TechTrialPlugin plugin;

    public static TechTrialPlugin getPlugin() {
        return TechTrialPlugin.plugin;
    }
    public static LuckPerms luckperms;

    public static LuckPerms getLuckperms() {
        return luckperms;
    }


    @Override
    public void onEnable() {
        // Plugin startup login
        plugin = this;


        try {
            luckperms = LuckPermsProvider.get();
            getLogger().info("Connected to LuckPerms");
        } catch (IllegalStateException e) {
            getLogger().severe("LuckPerms is not loaded! I'm going to blow a fat one on my screen");
        }

        //setup config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // load hoe event
        getServer().getPluginManager().registerEvents(new RightClickHoe(), this);

        // load config commands
        Objects.requireNonNull(getCommand("ToggleTrample")).setExecutor(new ToggleTrampleCmd(this));
        getServer().getPluginManager().registerEvents(new CropBreakPrevention(), this);
        getCommand("modgrant").setExecutor(new GivePlayerModCmd(this));
        }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}


