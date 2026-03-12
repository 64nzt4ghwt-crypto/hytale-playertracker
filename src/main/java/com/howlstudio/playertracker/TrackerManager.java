package com.howlstudio.playertracker;
import com.hypixel.hytale.component.Ref; import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.nio.file.*; import java.util.*; import java.time.*;
import java.time.format.DateTimeFormatter;
public class TrackerManager {
    private final Path dataDir;
    private final Map<UUID,PlayerRecord> records=new LinkedHashMap<>();
    private final Map<String,UUID> nameLookup=new HashMap<>();
    private static final DateTimeFormatter FMT=DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    public TrackerManager(Path d){this.dataDir=d;try{Files.createDirectories(d);}catch(Exception e){}load();}
    public int getRecordCount(){return records.size();}
    public PlayerRecord getOrCreate(UUID uid,String name){
        PlayerRecord r=records.computeIfAbsent(uid,k->new PlayerRecord(k,name));
        r.setName(name); nameLookup.put(name.toLowerCase(),uid); return r;
    }
    public PlayerRecord find(String name){UUID uid=nameLookup.get(name.toLowerCase());return uid!=null?records.get(uid):null;}
    public void save(){try{StringBuilder sb=new StringBuilder();for(PlayerRecord r:records.values())sb.append(r.toConfig()).append("\n");Files.writeString(dataDir.resolve("players.txt"),sb.toString());}catch(Exception e){}}
    private void load(){try{Path f=dataDir.resolve("players.txt");if(!Files.exists(f))return;for(String l:Files.readAllLines(f)){PlayerRecord r=PlayerRecord.fromConfig(l);if(r!=null){records.put(r.getUuid(),r);nameLookup.put(r.getName().toLowerCase(),r.getUuid());}}}catch(Exception e){}}
    private String fmtTime(long ms){return Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).format(FMT)+" UTC";}
    public AbstractPlayerCommand getSeenCommand(){
        return new AbstractPlayerCommand("seen","Check when a player was last online. /seen <player>"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                String name=ctx.getInputString().trim();if(name.isEmpty()){playerRef.sendMessage(Message.raw("Usage: /seen <player>"));return;}
                PlayerRecord r=find(name);if(r==null){playerRef.sendMessage(Message.raw("[Seen] No record for: "+name));return;}
                if(r.isOnline()){playerRef.sendMessage(Message.raw("[Seen] §6"+r.getName()+"§r is §aCURRENTLY ONLINE§r ("+r.getPlaytimeStr()+" total playtime)"));}
                else{playerRef.sendMessage(Message.raw("[Seen] §6"+r.getName()+"§r was last seen §e"+fmtTime(r.getLastSeen())+"§r"));}
            }
        };
    }
    public AbstractPlayerCommand getWhoisCommand(){
        return new AbstractPlayerCommand("whois","Full player profile. /whois <player>"){
            @Override protected void execute(CommandContext ctx,Store<EntityStore> store,Ref<EntityStore> ref,PlayerRef playerRef,World world){
                String name=ctx.getInputString().trim();if(name.isEmpty()){playerRef.sendMessage(Message.raw("Usage: /whois <player>"));return;}
                PlayerRecord r=find(name);if(r==null){playerRef.sendMessage(Message.raw("[Whois] No record: "+name));return;}
                playerRef.sendMessage(Message.raw("=== "+r.getName()+" ==="));
                playerRef.sendMessage(Message.raw("  Status: "+(r.isOnline()?"§aONLINE":"§cOFFLINE")));
                playerRef.sendMessage(Message.raw("  First seen: §e"+fmtTime(r.getFirstSeen())));
                playerRef.sendMessage(Message.raw("  Last seen: §e"+fmtTime(r.getLastSeen())));
                playerRef.sendMessage(Message.raw("  Playtime: §6"+r.getPlaytimeStr()+"§r | Logins: "+r.getLoginCount()));
            }
        };
    }
}
