package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import java.util.Date;
import java.util.UUID;

public class DropItemData {
    private UUID DropItemUUID;
    private UUID WorldUUID;
    private UUID DropUser;
    private Date DropDate;

    public DropItemData(){
        this.DropItemUUID = null;
        this.WorldUUID = null;
        this.DropUser = null;
        this.DropDate = null;
    }

    public DropItemData(UUID dropItemUUID, UUID worldUUID, UUID dropUser, Date dropDate){
        this.DropItemUUID = dropItemUUID;
        this.WorldUUID = worldUUID;
        this.DropUser = dropUser;
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

    public UUID getDropUser(){
        return DropUser;
    }

    public void setDropUser(UUID dropUser){
        DropUser = dropUser;
    }

    public Date getDropDate() {
        return DropDate;
    }

    public void setDropDate(Date dropDate) {
        DropDate = dropDate;
    }
}
