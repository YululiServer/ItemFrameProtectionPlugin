package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import java.util.Objects;
import java.util.UUID;

class ChestInData {

    private UUID ChestUUID;
    private UUID UseUserUUID;

    public ChestInData(UUID chestUUID, UUID useUserUUID){

        this.ChestUUID = chestUUID;
        this.UseUserUUID = useUserUUID;

    }

    public UUID getChestUUID() {
        return ChestUUID;
    }

    public void setChestUUID(UUID chestUUID) {
        ChestUUID = chestUUID;
    }

    public UUID getUseUserUUID() {
        return UseUserUUID;
    }

    public void setUseUserUUID(UUID useUserUUID) {
        UseUserUUID = useUserUUID;
    }

    @Override
    public boolean equals(Object data){

        if (data instanceof ChestInData){
            return equals((ChestInData) data);
        }

        return false;
    }


    public boolean equals(ChestInData data) {
        if (this == data) return true;
        if (data == null || getClass() != data.getClass()) return false;

        return Objects.equals(ChestUUID, data.ChestUUID) &&
                Objects.equals(UseUserUUID, data.UseUserUUID);
    }

}

