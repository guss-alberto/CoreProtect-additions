package com.alisaa.coreprotectadditions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Updater {
    static final String GITHUB_URL = "https://api.github.com/repos/guss-alberto/CoreProtect-additions/releases";
    static final String MODRINTH_URL = "https://modrinth.com/project/OSx6M73S/versions";

    public static boolean checkForUpdates(JavaPlugin plugin) {
        String latestVersionTag;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GITHUB_URL))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject latestRelease = JsonParser.parseString(response.body()).getAsJsonArray().get(0)
                    .getAsJsonObject();

            latestVersionTag = latestRelease.get("tag_name").getAsString();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            e.printStackTrace();
            Bukkit.getLogger().severe("Unable to fetch latest version tag from GitHub");
            return false;
        }

        String currentVersion = "v" + plugin.getPluginMeta().getVersion();

        if (latestVersionTag.equals(currentVersion)) {
            Bukkit.getLogger()
                    .info("You are using the latest version of CoreProtect-additions (" + latestVersionTag + ").");
            return false;
        }

        Bukkit.getLogger()
                .warning("You are using an outdated version of CoreProtect-additions (" + currentVersion
                        + ") latest version is " + latestVersionTag);
        Bukkit.getLogger()
                .warning("Download the latest version from " + MODRINTH_URL);
        return true;
    }
}