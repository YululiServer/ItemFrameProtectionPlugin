package xyz.n7mn.dev.yululi.itemframeprotectionplugin.data;

import com.destroystokyo.paper.Namespaced;
import com.google.common.collect.Multimap;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemStackJSON {

    private Material type;
    private int amount;

    private String displayName;
    private Multimap<Attribute, AttributeModifier> attributeModifiers;
    private int customModelData;
    private Set<Namespaced> destroyableKeys;
    private BaseComponent[] displayNameComponent;
    private Set<ItemFlag> itemFlags;
    private String localizedName;
    private List<BaseComponent[]> loreComponents;
    private Set<Namespaced> placeableKeys;
    boolean unbreakable;

    private MaterialData materialData;
    private List<String> lore;


    public ItemStackJSON(){
        type = Material.AIR;
        amount = 0;
        materialData = null;
        lore = new ArrayList<>();
    }

    public ItemStackJSON(Material type, int amount, String displayName, Multimap<Attribute, AttributeModifier> attributeModifiers, int customModelData, Set<Namespaced> destroyableKeys, BaseComponent[] displayNameComponent, Set<ItemFlag> itemFlags, String localizedName, List<BaseComponent[]> loreComponents, Set<Namespaced> placeableKeys, boolean unbreakable, MaterialData materialData, List<String> lore) {
        this.type = type;
        this.amount = amount;
        this.displayName = displayName;
        this.attributeModifiers = attributeModifiers;
        this.customModelData = customModelData;
        this.destroyableKeys = destroyableKeys;
        this.displayNameComponent = displayNameComponent;
        this.itemFlags = itemFlags;
        this.localizedName = localizedName;
        this.loreComponents = loreComponents;
        this.placeableKeys = placeableKeys;
        this.unbreakable = unbreakable;
        this.materialData = materialData;
        this.lore = lore;


    }

    public ItemStackJSON(ItemStack stack){

        this.type = stack.getType();
        this.amount = stack.getAmount();

        this.displayName = stack.getItemMeta().getDisplayName();
        this.attributeModifiers = stack.getItemMeta().getAttributeModifiers();
        if (stack.getItemMeta().hasCustomModelData()){
            this.customModelData = stack.getItemMeta().getCustomModelData();
        }
        this.destroyableKeys = stack.getItemMeta().getDestroyableKeys();
        this.displayNameComponent = stack.getItemMeta().getDisplayNameComponent();
        this.itemFlags = stack.getItemMeta().getItemFlags();
        this.localizedName = stack.getItemMeta().getLocalizedName();
        this.loreComponents = stack.getItemMeta().getLoreComponents();
        this.placeableKeys = stack.getItemMeta().getPlaceableKeys();
        this.unbreakable = stack.getItemMeta().isUnbreakable();
        this.materialData = stack.getData();
        this.lore = stack.getLore();

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

    public ItemMeta getItemMeta(){

        ItemStack itemStack = new ItemStack(type);
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setAttributeModifiers(attributeModifiers);
        itemMeta.setCustomModelData(customModelData);

        if (destroyableKeys != null){
            itemMeta.setDestroyableKeys(destroyableKeys);
        }

        itemMeta.setDisplayName(displayName);

        if (displayNameComponent != null){
            itemMeta.setDisplayNameComponent(displayNameComponent);
        }

        itemMeta.setLocalizedName(localizedName);
        itemMeta.setLore(lore);
        itemMeta.setLoreComponents(loreComponents);

        if (placeableKeys != null){
            itemMeta.setPlaceableKeys(placeableKeys);
        }

        itemMeta.setUnbreakable(unbreakable);

        return itemMeta;


    }

}
