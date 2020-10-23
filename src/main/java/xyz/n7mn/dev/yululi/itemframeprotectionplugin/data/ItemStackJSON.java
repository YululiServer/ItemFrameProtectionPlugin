package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.Material;
import xyz.acrylicstyle.paper.nbt.NBTTagCompound;

public class ItemStackJSON {

    private Material type;
    private int amount;
    private NBTTagCompound tag;

    public ItemStackJSON(){
        type = Material.AIR;
        amount = 0;
        tag = null;
    }

    public ItemStackJSON(Material type, int amount, NBTTagCompound tag){
        this.type = type;
        this.amount = amount;
        this.tag = tag;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public NBTTagCompound getTag() {
        return tag;
    }

    public void setTag(NBTTagCompound tag) {
        this.tag = tag;
    }
}
