package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class AcceptCommand extends SubCommand {

  public AcceptCommand() {
    super("accept");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(Language.command$accept$args);
      return;
    }

    String owner = args[0];
    if (owner.equalsIgnoreCase(player.getName())) {
      player.sendMessage(Language.command$accept$cant_accept_from_yourself);
      return;
    }

    Party party = Party.getPartyByMember(player);
    if (party != null) {
      player.sendMessage(Language.command$accept$already_have_party);
      return;
    }

    party = Party.getPartyByOwner(owner);
    if (party == null) {
      player.sendMessage(Language.command$accept$user_are_not_party_leader.replace("{player}", owner));
      return;
    }

    owner = party.getCorrectName(owner);
    if (!party.hasInvite(player.getName())) {
      player.sendMessage(Language.command$accept$user_not_invited_you.replace("{player}", owner));
      return;
    }

    if (party.getSize() >= party.getSlots()) {
      player.sendMessage(Language.command$accept$user_party_is_full.replace("{player}", owner));
      return;
    }

    party.add(player.getName());
    player.sendMessage(Language.command$accept$accepted.replace("{player}", owner));
  }

  @Override
  public String getUsage() {
    return "accept <player>";
  }

  @Override
  public String getDescription() {
    return "Accept a party invite.";
  }
}
