package io.github.losteddev.parties.cmd.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class InviteCommand extends SubCommand {

  public InviteCommand() {
    super("invite");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(Language.command$invite$args);
      return;
    }

    String target = args[0];
    Player tplayer = Bukkit.getPlayer(target);
    if (tplayer == null) {
      player.sendMessage(Language.command$invite$user_not_found);
      return;
    }

    target = tplayer.getName();

    if (target.equalsIgnoreCase(player.getName())) {
      player.sendMessage(Language.command$invite$cant_invite_yourself);
      return;
    }

    Party party = Party.getPartyByMember(player);
    if (party == null) {
      party = Party.createParty(player);
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage(Language.command$invite$only_leader_can_invite);
      return;
    }

    if (party.getSize() >= party.getSlots()) {
      player.sendMessage(Language.command$invite$full);
      return;
    }

    if (party.hasInvite(target)) {
      player.sendMessage(Language.command$invite$already_invited.replace("{player}", target));
      return;
    }

    if (Party.getPartyByMember(target) != null) {
      player.sendMessage(Language.command$invite$user_already_have_party.replace("{player}", target));
      return;
    }

    party.invite(tplayer);
    player.sendMessage(Language.command$invite$invited.replace("{player}", target));
  }

  @Override
  public String getUsage() {
    return "invite <player>";
  }

  @Override
  public String getDescription() {
    return "Invite a player to party.";
  }
}
