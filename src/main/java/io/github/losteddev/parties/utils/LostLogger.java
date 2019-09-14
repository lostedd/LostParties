package io.github.losteddev.parties.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class LostLogger {

  private String prefix;
  private CommandSender sender;

  public LostLogger() {
    this.prefix = "[Losted:PARTIES] ";
    this.sender = Bukkit.getConsoleSender();
  }

  public LostLogger(String prefix) {
    this.prefix = prefix;
    this.sender = Bukkit.getConsoleSender();
  }

  public void info(String message) {
    this.log(LostLevel.INFO, message);
  }

  public void warning(String message) {
    this.log(LostLevel.WARNING, message);
  }

  public void severe(String message) {
    this.log(LostLevel.SEVERE, message);
  }

  public void log(LostLevel level, String message) {
    this.log(level, message, null);
  }

  public void log(LostLevel level, String message, Throwable throwable) {
    StringBuilder result = new StringBuilder(this.prefix + message);
    if (throwable != null) {
      result.append("\n" + throwable.getMessage());
      for (StackTraceElement ste : throwable.getStackTrace()) {
        if (ste.toString().contains("io.github.losteddev.skywars")) {
          result.append("\n" + ste.toString());
        }
      }
    }

    this.sender.sendMessage(level.format(result.toString()));
  }

  public LostLogger getModule(String module) {
    return new LostLogger(this.prefix + "[" + module + "] ");
  }

  public static enum LostLevel {
      INFO("§a"),
      WARNING("§c"),
      SEVERE("§4");

    private String color;

    LostLevel(String color) {
      this.color = color;
    }

    public String format(String message) {
      return this.color + message;
    }
  }
}
