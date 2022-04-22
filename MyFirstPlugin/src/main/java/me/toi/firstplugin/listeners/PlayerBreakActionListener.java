package me.toi.firstplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static me.toi.firstplugin.FileToHashmap.getRole;
import static me.toi.firstplugin.FirstPlugin.doesPlayerHaveRole;

public class PlayerBreakActionListener implements Listener {
    private final HashMap<UUID, Long> cooldown = new HashMap<>(); //Create Hashmap for player/cooldown // if error remove final
    @EventHandler
    public static void isBreakingStone(PlayerInteractEvent event) { //Check for block breaking action
        Player player = event.getPlayer();
        if (doesPlayerHaveRole(player,"mole")) { //if player has role Mole
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (event.getClickedBlock().getType().equals(Material.STONE)) {
                    event.getClickedBlock().breakNaturally(new ItemStack(Material.NETHERITE_PICKAXE));
                }
            }
        }
    }

    @EventHandler
    public void onLaunchPearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (doesPlayerHaveRole(player, "ender")) { //if player has role Ender
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (player.getInventory().getItemInMainHand().getType() == Material.ENDER_PEARL || (player.getInventory().getItemInOffHand().getType() == Material.ENDER_PEARL)) { //Check for pearl in hand
                    if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        long remaining = cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
                        player.sendMessage(ChatColor.RED + "This spell is on cooldown (" + remaining / 1000 + "s)");
                        event.setCancelled(true);
                    } else {
                        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                        cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (30 * 1000));
                    }
                }
            }
        }
    }
    @EventHandler
    public void OnEntityDamage(EntityDamageByEntityEvent event){
        if (!(event.getDamager().getType() == EntityType.PLAYER)) {
            return;
        }
        Player player = (Player) event.getDamager();
        String role = getRole(String.valueOf(player.getUniqueId()));
        if (Objects.equals(role, "ice")){
            event.setDamage(event.getDamage()-2);
        }
    }
}
