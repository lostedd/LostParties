package io.github.losteddev.parties.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import com.google.common.collect.ImmutableList;
import io.github.losteddev.parties.Language;
import io.github.losteddev.parties.Main;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Party {

  private int slots;
  private PartyPlayer owner;
  private Map<String, Long> invites;
  private List<PartyPlayer> members;

  public Party(Player player) {
    this.members = new ArrayList<>();
    this.invites = new ConcurrentHashMap<>();
    this.slots = PartySlots.getSlots(player);
    this.owner = new PartyPlayer(player, PartyRole.LEADER);
    this.members.add(this.owner);
  }

  public void transfer(String leader) {
    this.owner.changeRole(PartyRole.MEMBER);
    this.owner = members.stream().filter(pp -> pp.getName().equals(leader)).findFirst().get();
    this.owner.changeRole(PartyRole.LEADER);
  }

  public void delete() {
    broadcast(Language.party$broadcast$deleted.replace("{player}", this.getOwnerName()), false);
    deleteParty(this);
  }

  public void invite(Player target) {
    invites.put(target.getName().toLowerCase(), System.currentTimeMillis() + 30000);
    BaseComponent[] init = TextComponent.fromLegacyText(" \n§7" + this.getOwnerName() + " §dwants you into him party!\n");
    BaseComponent[] click = TextComponent.fromLegacyText("§dYou can ");
    TextComponent accept = new TextComponent("§a§lACCEPT");
    accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click to accept the invite of " + this.getOwnerName() + ".")));
    accept.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/party accept " + this.getOwnerName()));
    click[click.length - 1].addExtra(accept);
    for (BaseComponent or : TextComponent.fromLegacyText(" §dor ")) {
      click[click.length - 1].addExtra(or);
    }
    TextComponent reject = new TextComponent("§c§lREJECT");
    reject.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§7Click to reject the invite of " + this.getOwnerName() + ".")));
    reject.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/party reject " + this.getOwnerName()));
    click[click.length - 1].addExtra(reject);
    for (BaseComponent end : TextComponent.fromLegacyText(" §dthe invite.\n ")) {
      click[click.length - 1].addExtra(end);
    }
    for (BaseComponent cl : click) {
      init[init.length - 1].addExtra(cl);
    }

    target.spigot().sendMessage(init);
  }

  public void reject(String player) {
    invites.remove(player.toLowerCase());
    Player owner = Bukkit.getPlayerExact(this.getOwnerName());
    if (owner != null) {
      owner.sendMessage(Language.party$owner$player_reject.replace("{player}", player));
    }
  }

  public void add(String player) {
    broadcast(Language.party$broadcast$join.replace("{player}", player));
    members.add(new PartyPlayer(Bukkit.getPlayer(player), PartyRole.MEMBER));
    invites.remove(player.toLowerCase());
  }

  public void remove(String member) {
    String owner = this.getOwnerName();
    members.remove(members.stream().filter(pp -> pp.getName().equals(member)).findFirst().get());
    if (members.size() > 0) {
      broadcast(Language.party$broadcast$leave.replace("{player}", member));
      if (owner.equals(member)) {
        this.owner = members.get(0);
        this.owner.changeRole(PartyRole.LEADER);
        broadcast(Language.party$broadcast$new_leaver_after_old_leader_leave.replace("{player}", this.getOwnerName()));
      }
    } else {
      deleteParty(this);
    }
  }

  public void kick(String member) {
    Player target = null;
    this.members.remove(members.stream().filter(pp -> pp.getName().equals(member)).findFirst().get());
    if ((target = Bukkit.getPlayerExact(member)) != null) {
      target.sendMessage(Language.party$member$kick.replace("{player}", this.getOwnerName()));
    }
  }

  private long lastOnline;

  public void setLastOnline() {
    this.lastOnline = System.currentTimeMillis();
  }

  public void destroy() {
    this.slots = 0;
    this.members.clear();
    this.members = null;
    this.invites.clear();
    this.invites = null;
    this.lastOnline = 0;
  }

  public void clear() {
    if (online() == 0) {
      if (lastOnline + (180 * 1000) < System.currentTimeMillis()) {
        deleteParty(this);
        return;
      }
    } else {
      this.setLastOnline();
    }

    for (Iterator<Entry<String, Long>> itr = invites.entrySet().iterator(); itr.hasNext();) {
      Entry<String, Long> entry = itr.next();

      if (entry.getValue() < System.currentTimeMillis()) {
        Player player = Bukkit.getPlayer(entry.getKey());
        if (player != null) {
          player.sendMessage(Language.party$member$invite_expires.replace("{player}", this.getOwnerName()));
        }
        itr.remove();
      }
    }
  }

  public void broadcast(String message) {
    this.broadcast(message, true);
  }

  public void broadcast(String message, boolean owner) {
    Player target = null;
    for (PartyPlayer member : this.members) {
      if (member.getName().equals(this.getOwnerName()) && !owner) {
        continue;
      }

      if ((target = member.getPlayer()) != null) {
        target.sendMessage(message);
      }
    }
  }

  public String getOwnerName() {
    return this.owner.getName();
  }

  public String getCorrectName(String player) {
    PartyPlayer target = this.members.stream().filter(member -> member.getName().equalsIgnoreCase(player)).findFirst().orElse(null);
    if (target != null) {
      return target.getName();
    }

    return player;
  }

  public boolean hasInvite(String player) {
    return invites.containsKey(player.toLowerCase());
  }

  public boolean contains(String player) {
    return members.stream().filter(member -> member.getName().equalsIgnoreCase(player)).count() == 1;
  }

  public boolean isOwner(String player) {
    return this.getOwnerName().equalsIgnoreCase(player);
  }

  public boolean isFull() {
    return members.size() >= slots;
  }

  public int getSize() {
    return members.size();
  }

  public int getSlots() {
    return slots;
  }

  public long online() {
    return members.stream().filter(member -> Bukkit.getPlayerExact(member.getName()) != null).count();
  }

  public List<Player> getPlayers(boolean owner) {
    Player target = null;
    List<Player> result = new ArrayList<>(members.size() - (owner ? 0 : 1));
    for (PartyPlayer player : members) {
      if (!owner && isOwner(player.getName())) {
        continue;
      }

      if ((target = player.getPlayer()) != null) {
        result.add(target);
      }
    }

    return result;
  }

  public List<String> getMembers() {
    return members.stream().map(pp -> pp.getName()).collect(Collectors.toList());
  }

  private static BukkitTask cleaner;
  private static List<Party> parties = new ArrayList<>();

  public static Party createParty(Player owner) {
    Party party = new Party(owner);
    parties.add(party);
    if (cleaner == null) {
      cleaner = new BukkitRunnable() {
        @Override
        public void run() {
          listParties().forEach(Party::clear);
        }
      }.runTaskTimer(Main.getInstance(), 0, 40);
    }

    return party;
  }

  public static void deleteParty(Party party) {
    parties.remove(party);
    party.destroy();
  }

  public static Party getPartyByOwner(String owner) {
    for (Party party : listParties()) {
      if (party.isOwner(owner)) {
        return party;
      }
    }

    return null;
  }

  public static Party getPartyByMember(Player player) {
    return getPartyByMember(player.getName());
  }

  public static Party getPartyByMember(String player) {
    for (Party party : listParties()) {
      if (party.contains(player)) {
        return party;
      }
    }

    return null;
  }

  public static List<Party> listParties() {
    return ImmutableList.copyOf(parties);
  }
}
