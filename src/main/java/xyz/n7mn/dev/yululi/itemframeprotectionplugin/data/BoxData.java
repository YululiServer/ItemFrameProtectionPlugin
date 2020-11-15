package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.inventory.*;

import java.util.UUID;

public class BoxData {

    private UUID BoxDataUUID;
    private Inventory Inventory;

    public BoxData(){
        this.BoxDataUUID = UUID.randomUUID();
        this.Inventory = null;
    }

    public BoxData(UUID boxDataUUID, Inventory inventory){
        this.BoxDataUUID = boxDataUUID;
        this.Inventory = inventory;
    }

    public BoxData(UUID boxDataUUID){
        this.BoxDataUUID = boxDataUUID;
        this.Inventory = null;
    }

    public BoxData(Inventory inventory){
        this.BoxDataUUID = UUID.randomUUID();
        this.Inventory = inventory;
    }

    public UUID getBoxDataUUID() {
        return BoxDataUUID;
    }

    public void setBoxDataUUID(UUID boxDataUUID) {
        BoxDataUUID = boxDataUUID;
    }

    public org.bukkit.inventory.Inventory getInventory() {
        return Inventory;
    }

    public void setInventory(org.bukkit.inventory.Inventory inventory) {
        Inventory = inventory;
    }
}
