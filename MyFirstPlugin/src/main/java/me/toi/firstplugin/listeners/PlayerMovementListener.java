package me.toi.firstplugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

import static me.toi.firstplugin.FirstPlugin.doesPlayerHaveRole;



public class PlayerMovementListener implements Listener {

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
        if (Objects.requireNonNull(to.getWorld()).getBlockAt(to).equals(Objects.requireNonNull(from.getWorld()).getBlockAt(from))) {// checks if you moved at least one block
            return;
        }
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
        if (!(Objects.requireNonNull(Bukkit.getWorld("world")).isClearWeather()) && doesPlayerHaveRole(event.getPlayer(), "shark")) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 0, false));
        }
        if (doesPlayerHaveRole(event.getPlayer(), "thief")) {
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 0, false));
        }
    }
}