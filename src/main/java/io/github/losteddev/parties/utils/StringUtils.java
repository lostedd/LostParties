package io.github.losteddev.parties.utils;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  private static final DecimalFormat df = new DecimalFormat("#,###"), df2 = new DecimalFormat("###.#");
  private static final Pattern COLOR_PATTERN = Pattern.compile("(?i)(§)[0-9A-FK-OR]");

  public static String stripColors(final String input) {
    if (input == null) {
      return null;
    }

    return COLOR_PATTERN.matcher(input).replaceAll("");
  }

  public static String formatColors(String textToFormat) {
    return translateAlternateColorCodes('&', textToFormat);
  }

  public static String deformatColors(String textToFormat) {
    Matcher matcher = COLOR_PATTERN.matcher(textToFormat);
    while (matcher.find()) {
      String color = matcher.group();
      textToFormat = textToFormat.replaceFirst(Pattern.quote(color),
          Matcher.quoteReplacement("&" + color.substring(1)));
    }

    return textToFormat;
  }

  public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
    Pattern pattern = Pattern.compile("(?i)(" + String.valueOf(altColorChar) + ")[0-9A-FK-OR]");

    Matcher matcher = pattern.matcher(textToTranslate);
    while (matcher.find()) {
      String color = matcher.group();
      textToTranslate = textToTranslate.replaceFirst(Pattern.quote(color),
          Matcher.quoteReplacement("§" + color.substring(1)));
    }

    return textToTranslate;
  }


  public static String getFirstColor(String input) {
    Matcher matcher = COLOR_PATTERN.matcher(input);
    String first = "";
    if (matcher.find()) {
      first = matcher.group();
    }

    return first;
  }

  public static String getLastColor(String input) {
    Matcher matcher = COLOR_PATTERN.matcher(input);
    String last = "";
    while (matcher.find()) {
      last = matcher.group();
    }

    return last;
  }
  
  public static String formatNumber(int amount) {
    return df.format(amount);
  }
  
  public static String formatNumber(double amount) {
    return df.format(amount);
  }
  
  public static String formatOneDecimal(double amount) {
    return df2.format(amount);
  }

  public static String repeat(String repeat, int amount) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < amount; i++) {
      sb.append(repeat);
    }

    return sb.toString();
  }

  public static <T> String join(T[] array, int index, String separator) {
    StringBuilder joined = new StringBuilder();
    for (int slot = index; slot < array.length; slot++) {
      joined.append(array[slot].toString() + (slot + 1 == array.length ? "" : separator));
    }

    return joined.toString();
  }

  public static <T> String join(T[] array, String separator) {
    return join(array, 0, separator);
  }

  public static <T> String join(Collection<T> collection, String separator) {
    return join(collection.toArray(new Object[collection.size()]), separator);
  }
}
