package io.github.losteddev.parties.utils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import io.github.losteddev.parties.Main;

public class SpigotUpdater {

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Updater");

  public SpigotUpdater(Plugin plugin, int resourceId) {
    LOGGER.info("Checking updates..");

    String latest = getVersion(resourceId);
    String currentVersion = plugin.getDescription().getVersion();
    if (latest == null) {
      LOGGER.severe("Failed to connect with servers to check updates.");
    } else {
      int siteVersion = Integer.parseInt(latest.replace(".", ""));
      int current = Integer.parseInt(currentVersion.replace(".", ""));

      if (current >= siteVersion) {
        LOGGER.info("You're using the lastet version of this plugin.");
      } else {
        LOGGER.warning("You're not using the lastet version of this plugin.");
      }
    }
  }

  private String getVersion(int resourceId) {
    String version = null;
    try {
      HttpURLConnection conn =
          (HttpURLConnection) new URL("http://api.spiget.org/v2/resources/" + resourceId + "/versions/latest?ut=" + System.currentTimeMillis()).openConnection();
      version = ((JSONObject) new JSONParser().parse(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))).get("name").toString();
    } catch (Exception ex) {
      return null;
    }
    return version;
  }
}
