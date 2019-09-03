package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class RejectCommand extends SubCommand {

  public RejectCommand() {
    super("reject");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage("§cUse /party " + this.getUsage());
      return;
    }

    String owner = args[0];
    if (owner.equalsIgnoreCase(player.getName())) {
      player.sendMessage("§cYou can't reject party invites from yourself.");
      return;
    }

    Party party = Party.getPartyByMember(player);
    if (party != null) {
      player.sendMessage("§cYou already have a party.");
      return;
    }

    party = Party.getPartyByOwner(owner);
    if (party == null) {
      player.sendMessage("§7" + owner + " §anot are leader of a party.");
      return;
    }

    owner = party.getCorrectName(owner);
    if (!party.hasInvite(player.getName())) {
      player.sendMessage("§7" + owner + " §anot have invited you to him party.");
      return;
    }

    party.reject(player.getName());
    player.sendMessage("§aYou rejected the Party invite from §7" + owner + "§a.");
  }

  @Override
  public String getUsage() {
    return "reject <player>";
  }

  @Override
  public String getDescription() {
    return "Reject a party invite.";
  }
}
