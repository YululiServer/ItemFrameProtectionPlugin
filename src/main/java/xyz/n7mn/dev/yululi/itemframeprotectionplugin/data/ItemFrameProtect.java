package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import java.util.UUID;

public class ItemFrameProtect {

    private UUID ProtectUUID;
    private UUID ProtectWorldUUID;
    private UUID ProtectUserUUID;

    public ItemFrameProtect(UUID protectUUID, UUID entityUUID, UUID worldUUID, UUID protectUserUUID){

        ProtectUUID = protectUUID;
        ProtectWorldUUID = worldUUID;
        ProtectUserUUID = protectUserUUID;

    }

    public UUID getProtectUUID() {
        return ProtectUUID;
    }

    public void setProtectUUID(UUID protectUUID) {
        ProtectUUID = protectUUID;
    }

    public UUID getProtectWorldUUID() {
        return ProtectWorldUUID;
    }

    public void setProtectWorldUUID(UUID protectWorldUUID) {
        ProtectWorldUUID = protectWorldUUID;
    }

    public UUID getProtectUserUUID() {
        return ProtectUserUUID;
    }

    public void setProtectUserUUID(UUID protectUserUUID) {
        ProtectUserUUID = protectUserUUID;
    }
}
