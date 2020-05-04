package logic;

import com.google.gson.internal.LinkedTreeMap;

/**
 * This class is used to create an armorpiece for the user to equip to gain statbuffs
 */

public class Armor extends AbstractItem
{
    private Type armorType;
    private LinkedTreeMap<String, Double> attributeBuffs;

    public Armor(final int id, final Type type, final LinkedTreeMap<String, Double> attributeBuffs) {
        super(id, 1);
        this.armorType = type;
        this.attributeBuffs = attributeBuffs;
    }

    public Type getArmorType() {
        return armorType;
    }

    public LinkedTreeMap<String, Double> getAttributeBuffs() {
        return attributeBuffs;
    }

    public enum Type
    {
        HEAD, CHEST, LEGS, WEAPON
    }

    public void onUse(Player player){
        player.equipItem(this);
    }
}

