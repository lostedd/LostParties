package io.github.losteddev.parties;

import org.bukkit.plugin.java.JavaPlugin;
import io.github.losteddev.parties.api.PartySlots;
import io.github.losteddev.parties.cmd.PartyCommand;
import io.github.losteddev.parties.utils.LostLogger;
import io.github.losteddev.parties.utils.LostLogger.LostLevel;

public class Main extends JavaPlugin {

  private static Main instance;
  public static final LostLogger LOGGER = new LostLogger();
  
  public Main() {
    instance = this;
  }

  @Override
  public void onEnable() {
    saveDefaultConfig();
    new PartyCommand();
    PartySlots.setupSlots();
    Language.setupLanguage();
    
    LOGGER.log(LostLevel.INFO, "The plugin has been enabled!");
  }

  @Override
  public void onDisable() {
    instance = null;

    LOGGER.log(LostLevel.INFO, "The plugin has been disabled!");
  }

  public static Main getInstance() {
    return instance;
  }
}
