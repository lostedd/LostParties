package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class LeaveCommand extends SubCommand {

  public LeaveCommand() {
    super("leave");
  }

  @Override
  public void perform(Player player, String[] args) {
    Party party = Party.getPartyByMember(player);
    if (party == null) {
      player.sendMessage(Language.command$leave$are_not_in_party
          );
      return;
    }

    party.remove(player.getName());
    player.sendMessage(Language.command$leave$left);
  }

  @Override
  public String getUsage() {
    return "leave";
  }

  @Override
  public String getDescription() {
    return "Leave from your party.";
  }
}
