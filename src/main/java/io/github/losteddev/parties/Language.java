package io.github.losteddev.parties;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import io.github.losteddev.parties.utils.ConfigUtils;
import io.github.losteddev.parties.utils.LanguageWriter;
import io.github.losteddev.parties.utils.LostLogger;
import io.github.losteddev.parties.utils.LostLogger.LostLevel;
import io.github.losteddev.parties.utils.StringUtils;

@SuppressWarnings("rawtypes")
public class Language {
  
  public static String party$member$kick = "§d[Party] §7{player} §aKicked you from him party.";
  public static String party$member$invite_expires = " \n§aYou party invite from §7{player} §ahas expired!\n ";
  public static String party$owner$player_reject = "§d[Party] §7{player} §arejected your party invite.";
  public static String party$broadcast$join = "§d[Party] §7{player} §ajoined the party!";
  public static String party$broadcast$leave = "§d[Party] §7{player} §aleft the party!";
  public static String party$broadcast$new_leaver_after_old_leader_leave = "§d[Party] §7{player} §ais the new Party Leader.";
  public static String party$broadcast$deleted = "§d[Party] §7{player} §adeleted the party!";
  
  public static String command$accept$args = "§cUse /party accept <player>";
  public static String command$accept$cant_accept_from_yourself = "§cYou can't accept party invites from yourself.";
  public static String command$accept$already_have_party = "§cYou already have a party.";
  public static String command$accept$user_are_not_party_leader = "§c{player} aren't leader of a party.";
  public static String command$accept$user_not_invited_you = "§c{player} not have invited you to him party.";
  public static String command$accept$user_party_is_full = "§c{player}'s Party is already full.";
  public static String command$accept$accepted = "§aYou joined §7{player}'s §aParty.";
  
  public static String command$delete$are_not_in_party = "§cYou aren't in a party.";
  public static String command$delete$only_leader_can_delete = "§cOnly the Party Leader can delete the party.";
  public static String command$delete$deleted = "§aYou deleted the Party.";
  
  public static String command$invite$args = "§cUse /party invite <player>";
  public static String command$invite$user_not_found = "§cUser not found.";
  public static String command$invite$cant_invite_yourself = "§cYou can't invite yourself for a party.";
  public static String command$invite$only_leader_can_invite = "§cOnly the Leader of Party can invite players.";
  public static String command$invite$full = "§cYour party is already full.";
  public static String command$invite$already_invited = "§c{player} has already been invited to Party.";
  public static String command$invite$user_already_have_party = "§c{player} already are into a party.";
  public static String command$invite$invited = "§aYou have invited §7{player} §ato your party.";
  
  public static String command$kick$args = "§cUse /party kick <player>";
  public static String command$kick$are_not_in_party = "§cYou aren't in a party.";
  public static String command$kick$only_the_leader_can_kick = "§cOnly the Party Leader can kick members.";
  public static String command$kick$cant_kick_yourself = "§cYou can't kick yourself from party.";
  public static String command$kick$user_are_not_member_of_your_party = "§7{player} §aaren't member of your party.";
  public static String command$kick$kicked = "§d[Party] §7{owner} §akicked §7{player} §afrom the party.";

  public static String command$leave$are_not_in_party = "§cYou aren't in a party.";
  public static String command$leave$left = "§aYou left the party.";
  
  public static String command$reject$args = "§cUse /party reject <player>";
  public static String command$reject$cant_reject_from_yourself = "§cYou can't reject party invites from yourself.";
  public static String command$reject$already_have_party = "§cYou already have a party.";
  public static String command$reject$user_not_a_party_leader = "§7{player} §anot are leader of a party.";
  public static String command$reject$user_not_invited_you = "§7{player} §anot have invited you to him party.";
  public static String command$reject$rejected = "§aYou rejected the Party invite from §7{player}§a.";
  
  public static String command$transfer$args = "§cUse /party transfer <player>";
  public static String command$transfer$are_not_in_party = "§cYou aren't in a party.";
  public static String command$transfer$only_the_leader_can_transfer = "§cOnly the Party Leader can transfer the leadership.";
  public static String command$transfer$cant_transfer_to_yourself = "§cYou can't promote yourself.";
  public static String command$transfer$user_are_not_member_of_your_party = "§7{player} §aaren't member of your party.";
  public static String command$transfer$transferred = "§d[Party] §7{player} §ais the new Party Leader.";

  public static final LostLogger LOGGER = Main.LOGGER.getModule("Language");
  private static final ConfigUtils CONFIG = ConfigUtils.getConfig("lang");

  public static void setupLanguage() {
    boolean save = false;
    LanguageWriter writer = new LanguageWriter(CONFIG.getFile());
    for (Field field : Language.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
        String nativeName = field.getName().replace("$", ".").replace("_", "-");

        try {
          Object value = null;

          if (CONFIG.contains(nativeName)) {
            value = CONFIG.get(nativeName);
            if (value instanceof String) {
              value = StringUtils.formatColors((String) value).replace("\\n", "\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.formatColors((String) v).replace("\\n", "\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            field.set(null, value);
            writer.set(nativeName, CONFIG.get(nativeName));
          } else {
            value = field.get(null);
            if (value instanceof String) {
              value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
            } else if (value instanceof List) {
              List l = (List) value;
              List<Object> list = new ArrayList<>(l.size());
              for (Object v : l) {
                if (v instanceof String) {
                  list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
                } else {
                  list.add(v);
                }
              }

              l = null;
              value = list;
            }

            save = true;
            writer.set(nativeName, value);
          }
        } catch (ReflectiveOperationException e) {
          LOGGER.log(LostLevel.WARNING, "Unexpected error on language file: ", e);
        }
      }
    }

    if (save) {
      writer.write();
      LOGGER.info("Lang.yml modified or created.");
      CONFIG.reload();
    }
  }

  public static void reload() {
    LanguageWriter writer = new LanguageWriter(CONFIG.getFile());
    for (Field field : Language.class.getDeclaredFields()) {
      if (Modifier.isStatic(field.getModifiers()) && Modifier.isPublic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
        String nativeName = field.getName().replace("$", ".").replace("_", "-");

        try {
          Object value = field.get(null);
          if (value instanceof String) {
            value = StringUtils.deformatColors((String) value).replace("\n", "\\n");
          } else if (value instanceof List) {
            List l = (List) value;
            List<Object> list = new ArrayList<>(l.size());
            for (Object v : l) {
              if (v instanceof String) {
                list.add(StringUtils.deformatColors((String) v).replace("\n", "\\n"));
              } else {
                list.add(v);
              }
            }

            l = null;
            value = list;
          }

          writer.set(nativeName, value);
        } catch (ReflectiveOperationException e) {
          LOGGER.log(LostLevel.WARNING, "Unexpected error on language file: ", e);
        }
      }
    }

    writer.write();
  }
}
