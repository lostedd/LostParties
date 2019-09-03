package io.github.losteddev.parties.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import io.github.losteddev.parties.Main;
import io.github.losteddev.parties.cmd.party.AcceptCommand;
import io.github.losteddev.parties.cmd.party.DeleteCommand;
import io.github.losteddev.parties.cmd.party.InviteCommand;
import io.github.losteddev.parties.cmd.party.KickCommand;
import io.github.losteddev.parties.cmd.party.LeaveCommand;
import io.github.losteddev.parties.cmd.party.RejectCommand;
import io.github.losteddev.parties.cmd.party.TransferCommand;

public class PartyCommand extends Command {
  
  private List<SubCommand> commands = new ArrayList<>();
  
  public PartyCommand() {
    super("party");
    this.setAliases(Arrays.asList("p"));

    try {
      SimpleCommandMap simpleCommandMap = (SimpleCommandMap) Bukkit.getServer().getClass()
          .getDeclaredMethod("getCommandMap").invoke(Bukkit.getServer());
      simpleCommandMap.register(this.getName(), "lostparties", this);
    } catch (ReflectiveOperationException ex) {
      Main.getInstance().getLogger().log(Level.SEVERE, "Could not register command: ", ex);
    }
    
    commands.add(new InviteCommand());
    commands.add(new AcceptCommand());
    commands.add(new RejectCommand());
    commands.add(new KickCommand());
    commands.add(new TransferCommand());
    commands.add(new DeleteCommand());
    commands.add(new LeaveCommand());
  }

  @Override
  public boolean execute(CommandSender sender, String commandLabel, String[] args) {
    if (sender instanceof Player) {
      Player player = (Player) sender;
      
      if (args.length == 0) {
        sendHelp(player, 1);
        return true;
      }

      try {
        sendHelp(player, Integer.parseInt(args[0]));
      } catch (NumberFormatException ex) {
        SubCommand subCommand = commands.stream().filter(sc -> sc.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
        if (subCommand == null) {
          sendHelp(player, 1);
          return true;
        }

        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(args));
        list.remove(0);
        subCommand.perform(player, list.toArray(new String[list.size()]));
      }
      return true;
    }
    
    return true;
  }
  
  private void sendHelp(Player player, int page) {
    Map<Integer, StringBuilder> pages = new HashMap<>();

    int pagesCount = (commands.size() + 5) / 6;
    for (int index = 0; index < commands.size(); index++) {
      int currentPage = (index + 6) / 6;
      if (!pages.containsKey(currentPage)) {
        pages.put(currentPage, new StringBuilder(" \n§dHelp - " + currentPage + "/" + pagesCount + "\n \n"));
      }

      pages.get(currentPage).append("§d/party " + commands.get(index).getUsage() + " §f- §7" + commands.get(index).getDescription() + "\n");
    }

    StringBuilder sb = pages.get(page);
    if (sb == null) {
      player.sendMessage("§5[LostParties] §cPage not found.");
      return;
    }

    sb.append(" ");
    player.sendMessage(sb.toString());
  }
}
