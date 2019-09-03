package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class TransferCommand extends SubCommand {

  public TransferCommand() {
    super("transfer");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage("§cUse /party " + this.getUsage());
      return;
    }

    String target = args[0];
    Party party = Party.getPartyByMember(player);
    if (party == null) {
      player.sendMessage("§cYou aren't in a party.");
      return;
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage("§cOnly the Party Leader can transfer the leadership.");
      return;
    }

    if (target.equalsIgnoreCase(player.getName())) {
      player.sendMessage("§cYou can't promote yourself.");
      return;
    }

    if (!party.contains(target)) {
      player.sendMessage("§7" + target + " §aaren't member of your party.");
      return;
    }

    target = party.getCorrectName(target);
    party.transfer(target);
    party.broadcast("§d[Party] §7" + target + " §ais the new Party Leader.");
  }

  @Override
  public String getUsage() {
    return "transfer <player>";
  }

  @Override
  public String getDescription() {
    return "Transfer the party leadership.";
  }
}
