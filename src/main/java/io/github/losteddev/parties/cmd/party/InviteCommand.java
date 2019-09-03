package io.github.losteddev.parties.cmd.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import io.github.losteddev.parties.api.Party;
import io.github.losteddev.parties.cmd.SubCommand;

public class InviteCommand extends SubCommand {

  public InviteCommand() {
    super("invite");
  }

  @Override
  public void perform(Player player, String[] args) {
    if (args.length == 0) {
      player.sendMessage("§cUse /party " + this.getUsage());
      return;
    }

    String target = args[0];
    Player tplayer = Bukkit.getPlayer(target);
    if (tplayer == null) {
      player.sendMessage("§cUser not found.");
      return;
    }

    target = tplayer.getName();

    if (target.equalsIgnoreCase(player.getName())) {
      player.sendMessage("§cYou can't invite yourself for a party.");
      return;
    }

    Party party = Party.getPartyByMember(player);
    if (party == null) {
      party = Party.createParty(player);
    }

    if (!party.isOwner(player.getName())) {
      player.sendMessage("§aOnly the Leader of Party can invite players.");
      return;
    }

    if (party.getSize() >= party.getSlots()) {
      player.sendMessage("§aYour party is already full.");
      return;
    }

    if (party.hasInvite(player.getName())) {
      player.sendMessage("§aOnly the Leader of Party can invite players.");
      return;
    }

    if (Party.getPartyByMember(target) != null) {
      player.sendMessage("§7" + target + " §aalready are into a party.");
      return;
    }

    if (party.hasInvite(target)) {
      player.sendMessage("§7" + target + " §aalready are invited to your party.");
      return;
    }

    party.invite(tplayer);
    player.sendMessage("§aYou have invited §7" + target + " §ato your party.");
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
