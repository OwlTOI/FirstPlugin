package me.toi.firstplugin.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;

import static me.toi.firstplugin.FirstPlugin.doesPlayerHaveRole;

public final class ProjectileListener implements Listener {

    @EventHandler
    public void onSnowballHit(EntityDamageByEntityEvent event) { //When entity dmg other
        Entity source = event.getDamager();
        if (source.getType() == EntityType.SNOWBALL) { //Is snowball ?
            ProjectileSource ps = ((Snowball) event.getDamager()).getShooter(); // Get source
            if (!(ps instanceof Player)) {  // Is player ?
                return;
            }
            Player shooter = (Player) ps; //From Entity (ps) to Player (shooter)
            if (doesPlayerHaveRole(shooter, "ice")) {
                event.setDamage(4);
            }
        }
    }

    @EventHandler
    public void SnowballBoost(ProjectileLaunchEvent event) { //Check for Snowball Event
        Entity projectile = event.getEntity();
        if (projectile.getType() == EntityType.SNOWBALL) { //Is snowball ?
            projectile.setVelocity(projectile.getVelocity().multiply(3));
        }
    }
}
