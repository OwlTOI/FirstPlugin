package me.toi.firstplugin.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static me.toi.firstplugin.FirstPlugin.doesPlayerHaveRole;


public class PlayerMovementListener implements Listener {
    private final HashMap<UUID, Long> cooldown = new HashMap<>(); //Create Hashmap for player/cooldown

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) { //Check for Entity dmg
        if (!(event.getEntity().getType() == EntityType.PLAYER)) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (doesPlayerHaveRole(player, "thief")) { //if player has role Thief
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void Leap(PlayerInteractEvent event) { //Check for player interaction
        Player player = event.getPlayer();
        Material[] itemList = {
                Material.WOODEN_SWORD,Material.STONE_SWORD,Material.IRON_SWORD,Material.GOLDEN_SWORD,Material.DIAMOND_SWORD,
                Material.NETHERITE_SWORD,Material.WOODEN_AXE,Material.STONE_AXE,Material.IRON_AXE,Material.GOLDEN_AXE,
                Material.DIAMOND_AXE,Material.NETHERITE_AXE,Material.SHIELD,Material.BOW};
        if (doesPlayerHaveRole(player, "thief")) { //if player has role Thief
            boolean itemListCheck = false;
            for (Material material : itemList) {
                if (player.getInventory().getItemInMainHand().getType() == material || player.getInventory().getItemInOffHand().getType() == material){
                    itemListCheck = true;
                    break;
                }
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && itemListCheck) {
                if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    long remaining = cooldown.get(player.getUniqueId()) - System.currentTimeMillis();
                    player.sendMessage(ChatColor.RED + "This spell is on cooldown (" + remaining / 1000 + "s)");
                } else {
                    player.setVelocity(player.getLocation().getDirection().multiply(2.5).setY(1)); //leap ability
                    cooldown.put(player.getUniqueId(), System.currentTimeMillis() + (20 * 1000));
                }

            }
        }
    }

    @EventHandler
    public void onEntityDamage(PlayerTeleportEvent event) { //Check for Player being teleported
        Player player = event.getPlayer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (doesPlayerHaveRole(player, "ender")) { //To be reworked, Legacy
                event.setCancelled(true); //Cancel teleport event

                player.teleport(Objects.requireNonNull(event.getTo())); // teleport player to event position (to avoid pearl teleport dmg)
            }
        }
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) { //May cause lag ? Lots of checking
        Location from = event.getFrom();
        Location to = event.getTo();
        assert to != null;
        if (!Objects.requireNonNull(to.getWorld()).getBlockAt(to).equals(Objects.requireNonNull(from.getWorld()).getBlockAt(from))) // checks if you moved at least one block
        {
            Block checkWater = event.getPlayer().getLocation().getBlock();
            if (checkWater.isLiquid()) {
                if (doesPlayerHaveRole(event.getPlayer(), "shark")) {
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 1000, 1, false));
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 1000, 2, false));
                }
                if (doesPlayerHaveRole(event.getPlayer(), "ender")) {
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 25, 0, false));
                }
            }
            if (doesPlayerHaveRole(event.getPlayer(), "thief")) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 0, false));
            }
        }
    }
}
