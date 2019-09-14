package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class KickCommand extends SubCommand {

  public KickCommand() {
    super("kick");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(Language.command$kick$args);
      return;
    }

    String target = args[0];
    Party party = Party.getPartyByMember(player);
    if (party == null) {
      player.sendMessage(Language.command$kick$are_not_in_party);
      return;
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage(Language.command$kick$only_the_leader_can_kick);
      return;
    }

    if (target.equalsIgnoreCase(player.getName())) {
      player.sendMessage(Language.command$kick$cant_kick_yourself);
      return;
    }

    if (!party.contains(target)) {
      player.sendMessage(Language.command$kick$user_are_not_member_of_your_party.replace("{player}", target));
      return;
    }

    target = party.getCorrectName(target);
    party.kick(target);
    party.broadcast(Language.command$kick$kicked.replace("{player}", target).replace("{owner}", player.getName()));
  }

  @Override
  public String getUsage() {
    return "kick <player>";
  }

  @Override
  public String getDescription() {
    return "Kick a party member.";
  }
}
