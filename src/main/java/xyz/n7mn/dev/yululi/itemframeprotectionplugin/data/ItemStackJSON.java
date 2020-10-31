package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

public class ItemStackJSON {

    private Material type;
    private int amount;
    private ItemMeta itemMeta;
    private MaterialData materialData;
    private List<String> lore;


    public ItemStackJSON(){
        type = Material.AIR;
        amount = 0;
        itemMeta = null;
        materialData = null;
        lore = new ArrayList<>();
    }

    public ItemStackJSON(Material type, int amount, ItemMeta itemMeta, MaterialData materialData, List<String> lore){
        this.type = type;
        this.amount = amount;
        this.itemMeta = itemMeta;
        this.materialData = materialData;
        this.lore = lore;
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

    public ItemMeta getItemMeta() {
        return itemMeta;
    }

    public void setItemMeta(ItemMeta itemMeta) {
        this.itemMeta = itemMeta;
    }

    public MaterialData getMaterialData() {
        return materialData;
    }

    public void setMaterialData(MaterialData materialData) {
        this.materialData = materialData;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }
}
