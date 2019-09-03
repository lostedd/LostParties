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
import io.github.losteddev.parties.Main;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Party {

  private int slots;
  private Map<String, Long> invites;
  private List<PartyPlayer> members;

  public Party(Player player) {
    this.members = new ArrayList<>();
    this.invites = new ConcurrentHashMap<>();
    this.slots = PartySlots.getSlots(player);
    this.members.add(new PartyPlayer(player, PartyRole.LEADER));
  }
  
  public void transfer(String leader) {
    members.stream().filter(pp->pp.getName().equals(getOwnerName())).findFirst().get().changeRole(PartyRole.MEMBER);
    members.stream().filter(pp -> pp.getName().equals(leader)).findFirst().get().changeRole(PartyRole.LEADER);
  }

  public void delete() {
    broadcast("§d[Party] §7" + this.getOwnerName() + " §adeleted the party!", false);
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
      owner.sendMessage("§d[Party] §7" + player + " §arejected your party invite.");
    }
  }

  public void add(String player) {
    broadcast("§d[Party] §7" + player + " §ajoined the party!");
    members.add(new PartyPlayer(Bukkit.getPlayer(player), PartyRole.MEMBER));
    invites.remove(player.toLowerCase());
  }

  public void remove(String member) {
    String owner = this.getOwnerName();
    members.remove(members.stream().filter(pp -> pp.getName().equals(member)).findFirst().get());
    if (members.size() > 0) {
      broadcast("§d[Party] §7" + member + " §aleft the party!");
      if (owner.equals(member)) {
        members.get(0).changeRole(PartyRole.LEADER);
        broadcast("§d[Party] §7" + this.getOwnerName() + " §ais the new Party Leader.");
      }
    } else {
      deleteParty(this);
    }
  }

  public void kick(String member) {
    Player target = null;
    this.members.remove(members.stream().filter(pp -> pp.getName().equals(member)).findFirst().get());
    if ((target = Bukkit.getPlayerExact(member)) != null) {
      target.sendMessage("§d[Party] §7" + this.getOwnerName() + " §aKicked you from him party.");
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
          player.sendMessage(" \n§aYou party invite from §7" + this.getOwnerName() + " §ahas expired!\n ");
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
    return members.stream().filter(member -> member.getRole().equals(PartyRole.LEADER)).findAny().get().getName();
  }

  public String getCorrectName(String player) {
    return members.stream().filter(member -> member.getName().equalsIgnoreCase(player)).findFirst().get().getName();
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
