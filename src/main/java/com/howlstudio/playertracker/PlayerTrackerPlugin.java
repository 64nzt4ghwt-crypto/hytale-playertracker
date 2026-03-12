package com.howlstudio.playertracker;
import com.hypixel.hytale.server.core.command.system.CommandManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/** PlayerTracker — Track last seen time, total playtime, and online status for any player. */
public final class PlayerTrackerPlugin extends JavaPlugin {
    private TrackerManager mgr;
    public PlayerTrackerPlugin(JavaPluginInit init){super(init);}
    @Override protected void setup(){
        System.out.println("[Tracker] Loading...");
        mgr=new TrackerManager(getDataDirectory());
        new TrackerListener(mgr).register();
        CommandManager.get().register(mgr.getSeenCommand());
        CommandManager.get().register(mgr.getWhoisCommand());
        System.out.println("[Tracker] Ready. "+mgr.getRecordCount()+" records.");
    }
    @Override protected void shutdown(){if(mgr!=null)mgr.save();System.out.println("[Tracker] Stopped.");}
}
