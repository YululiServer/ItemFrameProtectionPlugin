package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class InventoryData {

    private UUID uuid;
    private PlayerInventory playerInventory;

    public InventoryData() {
        uuid = null;
        playerInventory = null;
    }

    public InventoryData(UUID uuid, PlayerInventory playerInventory){

        this.uuid = uuid;
        this.playerInventory = playerInventory;

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    public void setPlayerInventory(PlayerInventory playerInventory) {
        this.playerInventory = playerInventory;
    }
}
