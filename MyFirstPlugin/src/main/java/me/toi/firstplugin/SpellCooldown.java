package me.toi.firstplugin;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpellCooldown {
    private BossBar spellBar;;
    private int taskID = -1;
    private final FirstPlugin plugin;

    public SpellCooldown(FirstPlugin plugin) {
        this.plugin = plugin;
    }

    public BossBar getBar(){
        return spellBar;
    }

    public void createBar(Player player, int cooldown){
         spellBar = Bukkit.createBossBar("Spell is on cooldown", BarColor.WHITE, BarStyle.SOLID);
         spellBar.setVisible(true);
         spellBar.addPlayer(player);
         cast(cooldown);
    }

    public void cast(int cooldown){
        spellBar.setTitle("Spell is on cooldown");

        /*taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            double progress = 1.0;
            double time = 1.0/ (cooldown);

            @Override
            public void run() {
                spellBar.setProgress(progress);
                progress = progress-time;
                if (progress <= 0){
                    spellBar.removeAll();
                    return;
                }
            }
        },0,20);

         */

        new BukkitRunnable(){
            int i =0;
            double progress = 1.0;
            double time = 1.0/ (cooldown);

            @Override
            public void run(){
                if (progress<=0){
                    spellBar.removeAll();
                    cancel();
                    return;
                }else{
                spellBar.setProgress(progress);
                progress = progress-time;
                }
            }
        }.runTaskTimer(plugin,0,20);
    }
}















