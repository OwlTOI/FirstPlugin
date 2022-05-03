package me.toi.firstplugin.listeners;


import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static me.toi.firstplugin.FileToHashmap.getRole;
import static me.toi.firstplugin.FirstPlugin.doesPlayerHaveRole;

public class PlayerBreakActionListener implements Listener {
    private final HashMap<UUID, Long> cooldown = new HashMap<>(); //Create Hashmap for player/cooldown
    @EventHandler
    public static void isBreakingStone(PlayerInteractEvent event) { //Check for block breaking action
        Player player = event.getPlayer();
        if (doesPlayerHaveRole(player,"mole")) { //if player has role Mole
            if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                if (Objects.requireNonNull(event.getClickedBlock()).getType().equals(Material.STONE)) {
                    event.getClickedBlock().breakNaturally(new ItemStack(Material.NETHERITE_PICKAXE));
                }
            }
        }
    }
    @EventHandler
    public static void BlockBreak(BlockBreakEvent event){
        if (event.getBlock().getType().equals(Material.SPAWNER)){
            if(doesPlayerHaveRole(event.getPlayer(),"mole")){
                event.setDropItems(true);
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
