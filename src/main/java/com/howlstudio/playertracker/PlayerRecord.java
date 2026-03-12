package com.howlstudio.playertracker;
import java.util.UUID;
public class PlayerRecord {
    private final UUID uuid;
    private String name;
    private long firstSeen, lastSeen;
    private long totalPlayMs;
    private long sessionStart;
    private int loginCount;
    public PlayerRecord(UUID uid,String name){this.uuid=uid;this.name=name;this.firstSeen=System.currentTimeMillis();}
    public UUID getUuid(){return uuid;} public String getName(){return name;} public void setName(String n){name=n;}
    public long getFirstSeen(){return firstSeen;} public long getLastSeen(){return lastSeen;}
    public void onJoin(){sessionStart=System.currentTimeMillis();lastSeen=sessionStart;loginCount++;}
    public void onLeave(){if(sessionStart>0){totalPlayMs+=System.currentTimeMillis()-sessionStart;sessionStart=0;}lastSeen=System.currentTimeMillis();}
    public long getTotalPlayMs(){return totalPlayMs+(sessionStart>0?System.currentTimeMillis()-sessionStart:0);}
    public int getLoginCount(){return loginCount;}
    public boolean isOnline(){return sessionStart>0;}
    public String getPlaytimeStr(){long m=getTotalPlayMs()/60_000;return m>=60?(m/60)+"h "+(m%60)+"m":m+"m";}
    public String toConfig(){return uuid+"|"+name+"|"+firstSeen+"|"+lastSeen+"|"+totalPlayMs+"|"+loginCount;}
    public static PlayerRecord fromConfig(String s){String[]p=s.split("\\|",6);if(p.length<6)return null;PlayerRecord r=new PlayerRecord(UUID.fromString(p[0]),p[1]);r.firstSeen=Long.parseLong(p[2]);r.lastSeen=Long.parseLong(p[3]);r.totalPlayMs=Long.parseLong(p[4]);r.loginCount=Integer.parseInt(p[5]);return r;}
}
