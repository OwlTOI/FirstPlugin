package me.toi.firstplugin;

import me.toi.firstplugin.commands.AdminInfoCommands;
import me.toi.firstplugin.listeners.PlayerBreakActionListener;
import me.toi.firstplugin.listeners.PlayerMovementListener;
import me.toi.firstplugin.listeners.ProjectileListener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

import static me.toi.firstplugin.FileToHashmap.*;

public class FirstPlugin extends JavaPlugin implements Listener {
    public static HashMap roleHashMap;
    private final HashMap<UUID, Long> cooldown = new HashMap<>(); //Create Hashmap for player/cooldown

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
        public void onPlayerJoin (PlayerJoinEvent event) {
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

        //Listeners implemented here because i can't static "this"

        @EventHandler
        public void Leap(PlayerInteractEvent event) { //Listener for Right Click Air
            Player player = event.getPlayer();
            Material[] itemList = {
                    Material.WOODEN_SWORD,Material.STONE_SWORD,Material.IRON_SWORD,Material.GOLDEN_SWORD,Material.DIAMOND_SWORD,
                    Material.NETHERITE_SWORD,Material.WOODEN_AXE,Material.STONE_AXE,Material.IRON_AXE,Material.GOLDEN_AXE,
                    Material.DIAMOND_AXE,Material.NETHERITE_AXE,Material.SHIELD};
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
                        SpellCooldown bar = new SpellCooldown(this);
                        bar.createBar(player, 20);
                    }

                }
            }
        }
    @EventHandler
    public void onLaunchPearl(PlayerInteractEvent event) { //Listener for EnderPearl
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
                        SpellCooldown bar = new SpellCooldown(this);
                        bar.createBar(player, 30);;
                    }
                }
            }
        }
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