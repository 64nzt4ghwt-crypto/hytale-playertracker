package com.howlstudio.playertracker;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
public class TrackerListener {
    private final TrackerManager mgr;
    public TrackerListener(TrackerManager m){this.mgr=m;}
    public void register(){
        HytaleServer.get().getEventBus().registerGlobal(PlayerReadyEvent.class,e->{Player p=e.getPlayer();if(p==null)return;PlayerRef r=p.getPlayerRef();if(r!=null)mgr.getOrCreate(r.getUuid(),r.getUsername()).onJoin();});
        HytaleServer.get().getEventBus().registerGlobal(PlayerDisconnectEvent.class,e->{PlayerRef r=e.getPlayerRef();if(r!=null){var rec=mgr.find(r.getUsername());if(rec!=null)rec.onLeave();}mgr.save();});
    }
}
