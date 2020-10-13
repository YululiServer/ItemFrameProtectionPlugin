package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

class UUID2UserName {

    static public String getUser(UUID uuid){
        HttpURLConnection urlconn;
        BufferedReader reader;

        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            if (player.getUniqueId().equals(uuid)){
                return player.getName();
            }
        }

        for (Player player : Bukkit.getServer().getOnlinePlayers()){
            if (player.getUniqueId().equals(uuid))
                return player.getName();
        }

        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replaceAll("-",""));

            urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.setInstanceFollowRedirects(false);
            urlconn.connect();

            reader = new BufferedReader(
                    new InputStreamReader(urlconn.getInputStream(), StandardCharsets.UTF_8));

            String json = CharStreams.toString(reader);

            JsonObject jsonArray = new Gson().fromJson(json, JsonObject.class);

            return jsonArray.get("name").getAsString();

        } catch (IOException e) {
            // e.printStackTrace();
            return null;
        }


    }
}
