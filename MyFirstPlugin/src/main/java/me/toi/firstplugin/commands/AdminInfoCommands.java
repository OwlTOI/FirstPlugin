package me.toi.firstplugin.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.toi.firstplugin.FileToHashmap.*;
import static me.toi.firstplugin.FirstPlugin.doesPlayerHaveRole;

public class AdminInfoCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if(args.length == 0) {
            System.out.println("Missing args");
            return true;
        }
        if(Bukkit.getServer().getPlayerExact(args[0]) == null) { //If player is not found
            System.out.println("Could not find specified player");
        }
        Player target = Bukkit.getServer().getPlayerExact(args[0]);
        String uuid = String.valueOf(target.getUniqueId());
        if(args[1].equals("role")){ // /admininfo [Player] role
            System.out.println("Looking for roles");
            if (args.length>2) {
                if (doesPlayerHaveRole(target, args[2])) { //admininfo [Player] role [role]
                    System.out.println(target.getName() + " is in role " + args[2]);
                } else {
                    System.out.println(target.getName() + " is not in role " + args[2]);
                }
            }else {
                if (getRole(uuid) == " " || getRole(uuid) == null) {
                    System.out.println(target.getName() + " does not have a role");
                } else {
                    System.out.println(target.getName() + " has the role " + getRole(uuid));
                }
            }
        }
        if(args[1].equals("add")){ //admininfo [Player] add [role]
            System.out.println("trying to add");
            if (args.length>2) {
                addEntry(String.valueOf(target.getUniqueId()), args[2]);
                applyBuff(target);
            }else{
                System.out.println("Missing role to add");
            }
        }
        return true;
    }
}
