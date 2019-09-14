package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class RejectCommand extends SubCommand {

  public RejectCommand() {
    super("reject");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(Language.command$reject$args);
      return;
    }

    String owner = args[0];
    if (owner.equalsIgnoreCase(player.getName())) {
      player.sendMessage(Language.command$reject$cant_reject_from_yourself);
      return;
    }

    Party party = Party.getPartyByMember(player);
    if (party != null) {
      player.sendMessage(Language.command$reject$already_have_party);
      return;
    }

    party = Party.getPartyByOwner(owner);
    if (party == null) {
      player.sendMessage(Language.command$reject$user_not_a_party_leader.replace("{player}", owner));
      return;
    }

    owner = party.getCorrectName(owner);
    if (!party.hasInvite(player.getName())) {
      player.sendMessage(Language.command$reject$user_not_invited_you.replace("{player}", owner));
      return;
    }

    party.reject(player.getName());
    player.sendMessage(Language.command$reject$rejected.replace("{player}", owner));
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
