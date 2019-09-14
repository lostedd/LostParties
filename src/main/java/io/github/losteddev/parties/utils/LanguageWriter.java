package io.github.losteddev.parties.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class LanguageWriter {

  private File file;
  private Map<String, Object> map = new LinkedHashMap<>();

  public LanguageWriter(File file) {
    this.file = file;
  }

  public void write() {
    try {
      Writer fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
      fw.append(this.toSaveString());
      fw.close();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  public void set(String path, Object value) {
    String[] splitter = path.split("\\.");

    Map<String, Object> currentMap = this.map;
    for (int slot = 0; slot < splitter.length; slot++) {
      String p = splitter[slot];
      if (slot + 1 == splitter.length) {
        currentMap.put(p, value);
        continue;
      } else {
        if (currentMap.containsKey(p)) {
          currentMap = (Map<String, Object>) currentMap.get(p);
        } else {
          currentMap.put(p, new LinkedHashMap<String, Object>());
          currentMap = (Map<String, Object>) currentMap.get(p);
        }
      }
    }
  }

  public String toSaveString() {
    StringBuilder join = new StringBuilder();
    for (Entry<String, Object> entry : map.entrySet()) {
      join.append(toSaveString(entry.getKey(), entry.getValue(), 0));
    }

    return join.toString();
  }

  @SuppressWarnings("unchecked")
  private String toSaveString(String key, Object object, int spaces) {
    StringBuilder join = new StringBuilder(repeat(spaces) + key + ":");
    if (object instanceof String) {
      join.append(" '" + object.toString().replace("'", "''").replace("\"", "\"\"") + "'\n");
    } else if (object instanceof Integer) {
      join.append(" " + object + "\n");
    } else if (object instanceof Double) {
      join.append(" " + object + "\n");
    } else if (object instanceof Long) {
      join.append(" " + object + "\n");
    } else if (object instanceof Boolean) {
      join.append(" " + object + "\n");
    } else if (object instanceof List) {
      join.append("\n");
      for (Object obj : (List<?>) object) {
        if (obj instanceof Integer) {
          join.append(repeat(spaces) + "- " + obj.toString() + "\n");
        } else {
          join.append(repeat(spaces) + "- '" + obj.toString() + "'\n");
        }
      }
    } else if (object instanceof Map) {
      join.append("\n");
      for (Entry<String, Object> entry : ((Map<String, Object>) object).entrySet()) {
        join.append(toSaveString(entry.getKey(), entry.getValue(), spaces + 1));
      }
    }

    return join.toString();
  }

  private String repeat(int spaces) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < spaces; i++) {
      sb.append(" ");
    }

    return sb.toString();
  }
}
