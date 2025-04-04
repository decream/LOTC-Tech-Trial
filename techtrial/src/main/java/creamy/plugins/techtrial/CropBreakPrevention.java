package creamy.plugins.techtrial;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import java.util.Set;

import static creamy.plugins.techtrial.TechTrialPlugin.plugin;

public class CropBreakPrevention implements Listener {

    private static final Set<Material> unbreakableCrops = Set.of(Material.POTATOES, Material.CARROTS, Material.WHEAT, Material.BEETROOTS);

    @EventHandler
    public void onCropBreak(BlockPhysicsEvent event) {
        final Material type = event.getBlock().getType();
        if (!unbreakableCrops.contains(type)) return;
        Block underCrop = event.getBlock();
        if (underCrop.getRelative(BlockFace.DOWN).getType().isAir()) {
            underCrop.setType(Material.AIR);
        } // checks if the block under the crop is gone, if so, removes crop
    }

    @EventHandler
    public void onCropPlayerBreak(BlockBreakEvent event) {
        final Material type = event.getBlock().getType();
        if (!unbreakableCrops.contains(type)) return;
        event.setCancelled(true);
    }

    // below makes sure the crop does not drop when exploded
    @EventHandler
    public void onCropExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> unbreakableCrops.contains(block.getType())); // Make crop explode unbreakable
    }

    // anti piston break
    @EventHandler
    public void onCropPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (unbreakableCrops.contains(block.getType())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCropInteract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (unbreakableCrops.contains(block.getType())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onCropTrample(EntityChangeBlockEvent event) {
        if (event.getBlock().getType() == Material.FARMLAND) {
            Entity entity = event.getEntity();

            if (entity instanceof Player player) {
                String uuid = player.getUniqueId().toString();
                FileConfiguration config = plugin.getConfig();
                boolean isProtected = config.getBoolean("cropProtection." + uuid, false);

                if (isProtected) {
                    event.setCancelled(true);
                    player.sendMessage("§aYour crop trampling is disabled. You didn’t damage the farmland!");
                }
            }
        }
    }

    // prevents water flow breaking crops regularly, replaces crops with air instead on event
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getToBlock();
        if (block.getType() == Material.WHEAT || block.getType() == Material.CARROTS || block.getType() == Material.POTATOES || block.getType() == Material.BEETROOTS) {
            event.setCancelled(true);
            block.setType(Material.AIR);
        }
    }
}
