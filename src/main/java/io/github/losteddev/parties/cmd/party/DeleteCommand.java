package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
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
      player.sendMessage(Language.command$delete$are_not_in_party);
      return;
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage(Language.command$delete$only_leader_can_delete);
      return;
    }

    party.delete();
    player.sendMessage(Language.command$delete$deleted);
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
