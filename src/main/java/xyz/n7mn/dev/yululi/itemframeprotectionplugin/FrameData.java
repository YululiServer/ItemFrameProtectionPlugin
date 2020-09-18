package xyz.n7mn.dev.yululi.itemframeprotectionplugin;

import java.util.UUID;

class FrameData {
    private UUID CreateUser;
    private UUID ItemFrame;

    public FrameData(UUID createUser, UUID itemFrame){
        this.CreateUser = createUser;
        this.ItemFrame = itemFrame;
    }

    public UUID getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(UUID createUser) {
        CreateUser = createUser;
    }

    public UUID getItemFrame() {
        return ItemFrame;
    }

    public void setItemFrame(UUID itemFrame) {
        ItemFrame = itemFrame;
    }
}
