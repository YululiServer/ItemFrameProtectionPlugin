package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import java.util.UUID;

public class DropItem {

    private UUID DropItemUUID;
    private UUID WorldUUID;

    public DropItem(UUID dropItemUUID, UUID worldUUID){

        DropItemUUID = dropItemUUID;
        WorldUUID = worldUUID;

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
}
