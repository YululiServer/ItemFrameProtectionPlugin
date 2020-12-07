package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.inventory.*;

import java.util.UUID;

public class BoxData {

    private UUID BoxDataUUID;
    private UUID BoxUseUUID;
    private Inventory Inventory;

    public BoxData(){
        this.BoxDataUUID = UUID.randomUUID();
        this.Inventory = null;
    }

    public BoxData(UUID boxDataUUID, UUID boxUseUUID, Inventory inventory){
        this.BoxDataUUID = boxDataUUID;
        this.BoxUseUUID = boxUseUUID;
        this.Inventory = inventory;
    }

    public BoxData(UUID boxDataUUID){
        this.BoxDataUUID = boxDataUUID;
        this.BoxUseUUID = null;
        this.Inventory = null;
    }

    public BoxData(UUID boxDataUUID, UUID boxUseUUID){
        this.BoxDataUUID = boxDataUUID;
        this.BoxUseUUID = boxUseUUID;
        this.Inventory = null;
    }

    public BoxData(UUID boxUseUUID, Inventory inventory){
        this.BoxDataUUID = UUID.randomUUID();
        this.BoxUseUUID = boxUseUUID;
        this.Inventory = inventory;
    }

    public UUID getBoxDataUUID() {
        return BoxDataUUID;
    }

    public void setBoxDataUUID(UUID boxDataUUID) {
        BoxDataUUID = boxDataUUID;
    }

    public UUID getBoxUseUUID() {
        return BoxUseUUID;
    }

    public void setBoxUseUUID(UUID boxUseUUID) {
        BoxUseUUID = boxUseUUID;
    }

    public Inventory getInventory() {
        return Inventory;
    }

    public void setInventory(org.bukkit.inventory.Inventory inventory) {
        Inventory = inventory;
    }
}
