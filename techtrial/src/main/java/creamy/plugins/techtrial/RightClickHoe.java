package creamy.plugins.techtrial;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RightClickHoe implements org.bukkit.event.Listener {

    @EventHandler
    public void getClickedBlock(PlayerInteractEvent event) {
        TechTrialPlugin.getPlugin().getConfig().getBoolean("path");
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block cropBlocks = event.getClickedBlock();
            Player player = event.getPlayer();
            ItemStack heldItem = player.getInventory().getItemInMainHand();

            // if air
            if (cropBlocks == null) {
                return;
            }
            // if correct type of block
            if (cropBlocks.getType() == Material.WHEAT || cropBlocks.getType() == Material.BEETROOTS || cropBlocks.getType() == Material.CARROTS
                    || cropBlocks.getType() == Material.POTATOES) {

                Location location = cropBlocks.getLocation();
                World world = location.getWorld();
                Ageable ageable = (Ageable) cropBlocks.getBlockData();

                // if crop is max age and player is holding a hoe
                if (ageable.getAge() == ageable.getMaximumAge() && hoeCheck(heldItem)) {
                    int extraDrops = hoeTiers(heldItem);
                    extraDrops = applyHoeFortune(heldItem, extraDrops);
                    switch (cropBlocks.getType()) {
                        case Material.WHEAT:
                            world.dropItemNaturally(location, new ItemStack(cropBlocks.getType(), 1 + extraDrops));
                            world.dropItemNaturally(location, new ItemStack(Material.WHEAT_SEEDS, 1));
                            break;
                        case Material.BEETROOTS:
                            world.dropItemNaturally(location, new ItemStack(Material.BEETROOT, 1 + extraDrops));
                            world.dropItemNaturally(location, new ItemStack(Material.BEETROOT_SEEDS, 1));
                            break;
                        case Material.CARROTS:
                            world.dropItemNaturally(location, new ItemStack(Material.CARROT, 1 + extraDrops));
                            break;
                        case Material.POTATOES:
                            world.dropItemNaturally(location, new ItemStack(Material.POTATO, 1 + extraDrops));
                            break;
                    }
                    ageable.setAge(0);
                    cropBlocks.setBlockData(ageable);
                    hoeDamage(player, heldItem);
                }
            }
        }
    }

    private boolean hoeCheck(ItemStack item) {

        if (item == null) {
            return false;
        }
        Material hoe = item.getType();
        return hoe == Material.WOODEN_HOE || hoe == Material.STONE_HOE ||
                hoe == Material.IRON_HOE || hoe == Material.GOLDEN_HOE ||
                hoe == Material.DIAMOND_HOE || hoe == Material.NETHERITE_HOE;
    }

    private int hoeTiers(ItemStack item) {

        if (item == null) return 0;

        return switch (item.getType()) {
            case Material.WOODEN_HOE, Material.STONE_HOE -> 0;
            case Material.IRON_HOE, Material.GOLDEN_HOE -> 1;
            case Material.DIAMOND_HOE, Material.NETHERITE_HOE -> 2;
            default -> 0;
        };

    }

    private void hoeDamage(Player player, ItemStack hoe) {

        if (hoe.getItemMeta() instanceof Damageable itemMeta) {
            int unbreakingLevel = hoe.getEnchantmentLevel(Enchantment.UNBREAKING);
            boolean canTakeDamage = unbreakingLevel == 0 || Math.random() >= (1.0 / (unbreakingLevel + 1));

            if (canTakeDamage) {
                itemMeta.setDamage(itemMeta.getDamage() + 1);
                hoe.setItemMeta(itemMeta);
            }
            if (itemMeta.getDamage() >= hoe.getType().getMaxDurability()) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }
        }
    }

    private int applyHoeFortune(ItemStack hoe, int extraDrops) {

        if (hoe == null) {
            return extraDrops;
        }

        int fortuneLevel = hoe.getEnchantmentLevel(Enchantment.FORTUNE);
        if (fortuneLevel > 0) {
            int fortuneDrops = (int) (Math.random() * (fortuneLevel + 1));
            return extraDrops + fortuneDrops;
        }
        return extraDrops;
    }
}





