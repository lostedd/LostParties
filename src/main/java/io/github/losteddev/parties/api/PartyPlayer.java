package io.github.losteddev.parties.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PartyPlayer {
  
  private String name;
  private PartyRole role;
  
  public PartyPlayer(Player player, PartyRole role) {
    this.name = player.getName();
    this.role = role;
  }
  
  public void changeRole(PartyRole role) {
    this.role = role;
  }
  
  public Player getPlayer() {
    return Bukkit.getPlayerExact(name);
  }
  
  public PartyRole getRole() {
    return role;
  }
  
  public String getName() {
    return name;
  }
}
