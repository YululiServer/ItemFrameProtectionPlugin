package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public class FrameData {

    private UUID ItemFrameUUID;
    private ItemStack FrameItem;
    private UUID ProtectUser;
    private Date CreateDate;
    private boolean Active;

    public FrameData(){
        this.ItemFrameUUID = null;
        this.FrameItem = null;
        this.ProtectUser = null;
        this.CreateDate = null;
        this.Active = false;
    }

    public FrameData(UUID itemFrameUUID, ItemStack frameItem, UUID protectUser, Date date, boolean active){
        this.ItemFrameUUID = itemFrameUUID;
        this.FrameItem = frameItem;
        this.ProtectUser = protectUser;
        this.CreateDate = date;
        this.Active = active;
    }

    public UUID getItemFrameUUID() {
        return ItemFrameUUID;
    }

    public void setItemFrameUUID(UUID itemFrameUUID) {
        ItemFrameUUID = itemFrameUUID;
    }

    public ItemStack getFrameItem() {
        return FrameItem;
    }

    public void setFrameItem(ItemStack frameItem) {
        FrameItem = frameItem;
    }

    public UUID getProtectUser() {
        return ProtectUser;
    }

    public void setProtectUser(UUID protectUser) {
        ProtectUser = protectUser;
    }

    public Date getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(Date createDate) {
        CreateDate = createDate;
    }

    public boolean isActive(){
        return this.Active;
    }

    public void setActive(boolean active){
        this.Active = active;
    }
}
