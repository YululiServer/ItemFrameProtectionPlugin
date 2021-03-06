package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import java.util.UUID;

class IFPData {

    private UUID ItemFrameUUID;
    private UUID UserUUID;

    public IFPData(){}

    public IFPData(UUID itemFrameUUID, UUID userUUID){
        this.ItemFrameUUID = itemFrameUUID;
        this.UserUUID = userUUID;
    }

    public UUID getItemFrameUUID() {
        return ItemFrameUUID;
    }

    public void setItemFrameUUID(UUID itemFrameUUID) {
        ItemFrameUUID = itemFrameUUID;
    }

    public UUID getUserUUID() {
        return UserUUID;
    }

    public void setUserUUID(UUID userUUID) {
        UserUUID = userUUID;
    }
}
