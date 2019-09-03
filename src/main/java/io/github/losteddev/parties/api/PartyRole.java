package io.github.losteddev.parties.api;

public enum PartyRole {
  MEMBER("Member"), LEADER("Leader");
  
  private String name;
  
  PartyRole(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}
