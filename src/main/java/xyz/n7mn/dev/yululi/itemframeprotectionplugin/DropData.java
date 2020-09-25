package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import java.util.UUID;

class DropData {
    private UUID DropUser;
    private UUID ItemUUID;

    public DropData(){}

    public DropData(UUID dropUser, UUID itemUUID){
        this.DropUser = dropUser;
        this.ItemUUID = itemUUID;
    }

    public UUID getDropUser() {
        return DropUser;
    }

    public void setDropUser(UUID dropUser) {
        DropUser = dropUser;
    }

    public UUID getItemUUID() {
        return ItemUUID;
    }

    public void setItemUUID(UUID itemUUID) {
        ItemUUID = itemUUID;
    }
}
