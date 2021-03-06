package me.toi.firstplugin;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;

import static me.toi.firstplugin.FirstPlugin.*;


public class FileToHashmap {
    final static String filePath = "plugins/FirstPlugin/";

    public static HashMap<String, String> queryFullFile() throws IOException { //Create Hashmap from file
        HashMap<String, String> map = new HashMap<>();
        BufferedReader br = null;

        Path path = Paths.get(filePath);
        File file = new File(filePath + "data.data");

        if (!Files.exists(path)) {      //Create directories if they don't exist
            Files.createDirectories(path);
        }
        try {
           boolean created = file.createNewFile(); //Try to create file if it does not exist
            if (created){
                System.out.println("Data.data did not exist, file created");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {   //Checking every line
                String[] parts = line.split(":");

                String uuid = parts[0].trim(); //Before ":" = key/uuid
                String role = parts[1].trim(); //After ":" = value/role

                if (!uuid.equals("") && !role.equals("")) //If not null add to Hashmap
                    map.put(uuid, role);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Always close the BufferedReader because I was told we should
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static String getRole(String uuid) { //return value from key
        HashMap map = roleHashMap;
        if (!map.containsKey(uuid)) {
            //If key does not exist in map = uuid not in data.data
            return null;
        }
        return map.get(uuid).toString(); //Return value for uuid
    }

    public static void addEntry(String key, String value) { //add key and value to Hashmap then file
        if (key == null) {
            return;
        }
        String[] list = getRoleList();
        boolean check = false;
        for (String s : list) { //Is value in role list ?
            if (Objects.equals(s, value)) {
                check = true;
                break;
            }
        }
        if (!check) { //If not make it empty
            value = " ";
        }
        roleHashMap.put(key, value); //Add value for key in map
        try {
            updateData();
            updateMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateData() throws IOException { //Write the Hashmap in file
        FileWriter fw = new FileWriter(filePath + "data.data");
        PrintWriter pw = new PrintWriter(fw);
        HashMap map = roleHashMap;
        map.forEach((k, v) -> pw.printf("%s:%s", k, v)); //Rewrite every line with content of Hashmap

        if (pw != null) { //probably overkill security
            try {
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fw != null) { //probably overkill security
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void applyBuff(Player player) {
        String uuid = String.valueOf(player.getUniqueId());
        String playerRole = getRole(uuid);
        //Reset modifiers
        if (Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue() == 16) {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).addModifier(new AttributeModifier("Health Buff", 4, AttributeModifier.Operation.ADD_NUMBER));
        }
        //If no role -> return
        if (Objects.equals(playerRole, " ") || playerRole == null) {
            return;
        }
        if (playerRole.equals("thief")) {
            System.out.println("Changing speed and health");
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).addModifier(new AttributeModifier("Health Debuff", -4, AttributeModifier.Operation.ADD_NUMBER));
        }
    }
    public static void applyBuffs(){
        for (Player p : Bukkit.getServer().getOnlinePlayers()){
            applyBuff(p);
        }

    }
}