package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import java.util.Date;
import java.util.UUID;

public class DropItemData {
    UUID DropItemUUID;
    UUID WorldUUID;
    Date DropDate;

    public DropItemData(){
        this.DropItemUUID = null;
        this.WorldUUID = null;
        this.DropDate = null;
    }

    public DropItemData(UUID dropItemUUID, UUID worldUUID, Date dropDate){
        this.DropItemUUID = dropItemUUID;
        this.WorldUUID = worldUUID;
        this.DropDate = dropDate;
    }

    public UUID getDropItemUUID() {
        return DropItemUUID;
    }

    public void setDropItemUUID(UUID dropItemUUID) {
        DropItemUUID = dropItemUUID;
    }

    public UUID getWorldUUID() {
        return WorldUUID;
    }

    public void setWorldUUID(UUID worldUUID) {
        WorldUUID = worldUUID;
    }

    public Date getDropDate() {
        return DropDate;
    }

    public void setDropDate(Date dropDate) {
        DropDate = dropDate;
    }
}
