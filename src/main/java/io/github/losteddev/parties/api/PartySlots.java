package io.github.losteddev.parties.api;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import io.github.losteddev.parties.Main;

public class PartySlots {

  private int limit;
  private String permission;

  public PartySlots(int limit, String permission) {
    this.limit = limit;
    this.permission = permission;
  }

  public int getLimit() {
    return limit;
  }

  public boolean has(Player player) {
    return permission.equals("none") || player.hasPermission(permission);
  }

  @Override
  public String toString() {
    return "PartySlots{limit=" + limit + ", permission=" + permission + "}";
  }

  private static List<PartySlots> slots = new ArrayList<>();

  public static void setupSlots() {
    FileConfiguration config = Main.getInstance().getConfig();

    config.getConfigurationSection("slots").getKeys(false)
        .forEach(key -> slots.add(new PartySlots(config.getInt("slots." + key + ".limit"), config.getString("slots." + key + ".permission"))));
  }

  public static int getSlots(Player player) {
    PartySlots ps = slots.stream().sorted((p1, p2) -> Integer.compare(p2.getLimit(), p1.getLimit())).filter(slots -> slots.has(player)).findFirst().orElse(null);
    if (ps != null) {
      return ps.getLimit();
    }

    return 5;
  }
}
