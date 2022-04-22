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

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static me.toi.firstplugin.FileToHashmap.*;


public final class FirstPlugin extends JavaPlugin implements Listener {
    public static HashMap roleHashMap;

        @Override
        public void onEnable () { //Initializing
            Server server = getServer();
            // Plugin startup logic
            System.out.println("[FirstPlugin] Starting !");
            //Event register
            server.getPluginManager().registerEvents(this, this);
            server.getPluginManager().registerEvents(new ProjectileListener(), this);
            server.getPluginManager().registerEvents(new PlayerMovementListener(), this);
            server.getPluginManager().registerEvents(new PlayerBreakActionListener(), this);
            Objects.requireNonNull(getCommand("fplug")).setExecutor(new AdminInfoCommands());
            try {
                updateMap();        //Give vale to roleHashMap
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onDisable () { //On Server stop save roleHashmap to file
            try {
                updateData();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        @EventHandler
        public void onPlayerJoin (PlayerJoinEvent event){
            Player player = event.getPlayer();
            String uuid = String.valueOf(player.getUniqueId());
            String playerRole = getRole(uuid);
            //Query for roles data
            if (playerRole == null) { //If uuid not found => write new line "uuid":" "
                addEntry(uuid, null);
            }
            applyBuffs();
        }

        public static boolean doesPlayerHaveRole (Player player, String role) { //  return result of : uuid's value == role ?
            String uuid = String.valueOf(player.getUniqueId());
            return Objects.equals(getRole(uuid), role);
        }

        public static String[] getRoleList () { //List of all role names
            return new String[]{"thief", "ice", "ender", "mole", "shark"};
        }
        public static void updateMap() throws IOException {
            roleHashMap = queryFullFile();
        }
    }
/*
List of roles
thief / Leap ability (20s cd) / Immune to fall (and pearl) dmg/ Buffed speed (Speed 1 while moving) \ Less Heart(-2 hearts)
ice / Snowball deals dmg (-2 hearts) \ Deals Less dmg (-1 heart per hit)
mole / Can insta break stone \ no debuff
ender / Do not get pearls consumed / Immune to pearls dmg / has cooldown on pearls(30s) \ Get dmged in water (Poison 1)
shark / Conduit power (3) and Dolphin's grace (2) effect when in water \ no debuff
*/