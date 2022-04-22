package me.toi.firstplugin;

import me.toi.firstplugin.commands.AdminInfoCommands;
import me.toi.firstplugin.listeners.PlayerBreakActionListener;
import me.toi.firstplugin.listeners.PlayerMovementListener;
import me.toi.firstplugin.listeners.ProjectileListener;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static me.toi.firstplugin.FileToHashmap.*;


public final class FirstPlugin extends JavaPlugin implements Listener {
    @Override
    public void onEnable() { //Initializing
        Server server = getServer();
        // Plugin startup logic
        System.out.println("[FirstPlugin] Starting !");
        //Event register
        server.getPluginManager().registerEvents(this, this);
        server.getPluginManager().registerEvents(new ProjectileListener(), this);
        server.getPluginManager().registerEvents(new PlayerMovementListener(), this);
        server.getPluginManager().registerEvents(new PlayerBreakActionListener(), this);
        Objects.requireNonNull(getCommand("fplug")).setExecutor(new AdminInfoCommands());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = String.valueOf(player.getUniqueId());
        String playerRole = getRole(uuid);
        //Query for roles data
        if (playerRole == null) { //If uuid not found => write new line "uuid":" "
            addEntry(uuid, null);
        }
    }

    public static boolean doesPlayerHaveRole(Player player, String role) { //  return result of : uuid's value == role ?
        String uuid = String.valueOf(player.getUniqueId());
        return Objects.equals(getRole(uuid), role);
    }

    public static String[] getRoleList() {
        return new String[]{"thief", "ice", "ender", "mole", "shark", "admin"};
    }
}

/*List of roles
thief / Leap ability / Immune to fall dmg / Buffed speed \ Less Heart
ice / Snowball deals dmg \ Less dmg
mole / can insta break stone \
ender / Do not get pearls consumed / Immune to pearls dmg/ has cooldown on pearls \ Get dmged in water
*/