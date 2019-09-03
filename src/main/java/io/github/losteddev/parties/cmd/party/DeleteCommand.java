package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class DeleteCommand extends SubCommand {

  public DeleteCommand() {
    super("delete");
  }

  @Override
  public void perform(Player player, String[] args) {
    Party party = Party.getPartyByMember(player);
    if (party == null) {
      player.sendMessage("§cYou aren't in a party.");
      return;
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage("§cOnly the Party Leader can delete the party.");
      return;
    }

    party.delete();
    player.sendMessage("§aYou deleted the Party.");
  }

  @Override
  public String getUsage() {
    return "delete";
  }

  @Override
  public String getDescription() {
    return "Delete your party.";
  }
}
