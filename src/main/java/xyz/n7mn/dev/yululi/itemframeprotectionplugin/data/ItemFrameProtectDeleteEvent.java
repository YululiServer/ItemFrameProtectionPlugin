package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ItemFrameProtectDeleteEvent extends Event {

    private static HandlerList handlerList = new HandlerList();
    private final UUID itemFrameUUID;

    public ItemFrameProtectDeleteEvent(UUID itemFrameUUID){

        this.itemFrameUUID = itemFrameUUID;

    }

    public UUID getItemFrameUUID() {
        return itemFrameUUID;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList(){
        return handlerList;
    }
}
