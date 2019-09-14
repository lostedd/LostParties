package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class TransferCommand extends SubCommand {

  public TransferCommand() {
    super("transfer");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage(Language.command$transfer$args);
      return;
    }

    String target = args[0];
    Party party = Party.getPartyByMember(player);
    if (party == null) {
      player.sendMessage(Language.command$transfer$are_not_in_party);
      return;
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage(Language.command$transfer$only_the_leader_can_transfer);
      return;
    }

    if (target.equalsIgnoreCase(player.getName())) {
      player.sendMessage(Language.command$transfer$cant_transfer_to_yourself);
      return;
    }

    if (!party.contains(target)) {
      player.sendMessage(Language.command$transfer$user_are_not_member_of_your_party.replace("{player}", target));
      return;
    }

    target = party.getCorrectName(target);
    party.transfer(target);
    party.broadcast(Language.command$transfer$transferred.replace("{player}", target));
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
