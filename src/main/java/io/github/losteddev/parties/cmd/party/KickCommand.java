package io.github.losteddev.parties.cmd.party;

import org.bukkit.entity.Player;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class KickCommand extends SubCommand {

  public KickCommand() {
    super("kick");
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
      player.sendMessage("§cOnly the Party Leader can kick members.");
      return;
    }

    if (target.equalsIgnoreCase(player.getName())) {
      player.sendMessage("§cYou can't kick yourself from party.");
      return;
    }

    if (!party.contains(target)) {
      player.sendMessage("§7" + target + " §aaren't member of your party.");
      return;
    }

    target = party.getCorrectName(target);
    party.kick(target);
    party.broadcast("§d[Party] §7" + player.getName() + " §akicked §7" + target + " §afrom the party.");
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
