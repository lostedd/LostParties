package io.github.losteddev.parties;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import io.github.losteddev.parties.api.PartySlots;
import io.github.losteddev.parties.cmd.PartyCommand;

public class Main extends JavaPlugin {

  private static Main instance;

  public Main() {
    instance = this;
  }

  @Override
  public void onEnable() {
    new PartyCommand();
    PartySlots.setupSlots();
    
    Bukkit.getConsoleSender().sendMessage("§a[Losted:PARTIES] The plugin has been enabled!");
  }

  @Override
  public void onDisable() {
    instance = null;

    Bukkit.getConsoleSender().sendMessage("§a[Losted:PARTIES] The plugin has been disabled!");
  }

  public static Main getInstance() {
    return instance;
  }
}
