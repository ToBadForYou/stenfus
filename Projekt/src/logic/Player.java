package logic;

import logging.Logging;
import visual.MessageWindow;
import io.gsonfire.annotations.PostDeserialize;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

/**
 * This class represents the player object, all player related methods are defined here for example equiping items,
 * level related, quests and inventory etc
 */

public class Player extends AbstractBattleCharacter
{
    private static final Logging LOGGER = new Logging("inventory");
    private static final int BASE_XP = 500;
    private static final double XP_POWER = 0.9;

    private transient boolean attackState;
    private int currentMap;
    private int xp, xpToLevelUp, attributePoints;
    private Set<Integer> completedQuests;
    private List<Integer> [] questProgresses;
    private transient Item[] inventory;
    private List<Integer[]> savedInventory = null;
    private HashMap<Armor.Type, Armor> equipped;
    private HashMap<String, Integer> attributeIncrements;

    public Player(final String name, final int hp, final int ap, final int atk, final int sp, final int dodgeChance, final int critChance,
		  final int physicalDef, final int magicalDef, final Point pos, final int id, final MapArea map)
    {
	super(name, hp, ap, atk, sp, dodgeChance, critChance, physicalDef, magicalDef, pos, id, "player", 0, 1, Color.GREEN);
        attackState = false;
	currentMap = map.getId();
	questProgresses = new ArrayList[World.getQuestsSize()];
	completedQuests = new HashSet<>();
        xp = 0;
        inventory = new Item[6];
        attributePoints = 0;
        // We need a HashMap instead of EnumMap for our Gson library to work
        equipped = new HashMap<>();
        calcXpToLevelUp();
        attributeIncrements = new HashMap<>();
        for (String attribute : attributes.keySet()) {
            attributeIncrements.put(attribute, 0);
        }
    }

    public void copyData(Player copyPlayer){
        currentMap = copyPlayer.currentMap;
        questProgresses = copyPlayer.questProgresses;
        completedQuests = copyPlayer.completedQuests;
        xp = copyPlayer.xp;
        attributes = copyPlayer.attributes;
        hp = copyPlayer.hp;
        ap = copyPlayer.ap;
        level = copyPlayer.level;
        pos = copyPlayer.pos;
        inventory = copyPlayer.inventory;
        attributePoints = copyPlayer.attributePoints;
    }

    private void calcXpToLevelUp() {
        xpToLevelUp = (int)Math.pow(level * BASE_XP, XP_POWER);
    }

    public void switchState(){
        attackState = !attackState;
    }

    public boolean getAttackState(){
        return attackState;
    }

    public void gainXp(int xp){
        this.xp += xp;
        if (this.xp >= xpToLevelUp){
            levelUp();
        }
    }

    public void levelUp() {
        level ++;
        attributePoints += 10;
        hp = attributes.get("Max HP");
        xp -= xpToLevelUp;
        calcXpToLevelUp();
    }

    public void upgradeAttribute(String stat){
        if (attributePoints > 0) {
            attributePoints -= 1;
            int oldValue = attributes.get(stat);
            attributes.put(stat, oldValue + 1);
            updateHPFromStamina(name, 1);
        }
    }

    public void changeAttribute(String name, double value){
        int oldValue = attributes.get(name);
        attributes.put(name, oldValue + (int) value);
        oldValue = attributeIncrements.get(name);
        attributeIncrements.put(name, oldValue + (int) value);
        updateHPFromStamina(name, (int) value);
    }

    public void updateHPFromStamina(String name, int value){
        if(name.equals("Stamina")) {
            maxHP += value * 10;
            hp += value * 10;
        }
    }

    public int getAttributeIncrement(String name){return attributeIncrements.get(name); }

    public void addQuest(int questID){
        List<Integer> newProgress = new ArrayList<>();
        for (int i = 0; i < World.getQuest(questID).getKillObjectives().length; i++) {
            newProgress.add(0);
        }
        questProgresses[questID] = newProgress;
    }

    public Set<Integer> getCompletedQuests() {
        return completedQuests;
    }

    public List<Integer> getQuestProgress(int i){ return questProgresses[i]; }

    public List<Integer>[] getQuestProgresses(){ return questProgresses; }

    public void updateQuests(List<Enemy> enemiesKilled){
        for (int i = 0; i < questProgresses.length; i++) {
            if (questProgresses[i] != null) {
               World.getQuest(i).updateProgress(questProgresses[i], enemiesKilled);
            }
        }
    }

    public void completeQuest(int i){
        completedQuests.add(i);
        questProgresses[i] = null;
    }

    public int getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(int currentMap) {
        this.currentMap = currentMap;
    }

    public int getXp() {
        return xp;
    }

    public int getXpToLevelUp() {
        return xpToLevelUp;
    }

    public int getAttributePoints() {
        return attributePoints;
    }

    public void addItemToInventory(Item item){
        int emptySlot = -1;
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null ){
                if(emptySlot == -1){ emptySlot = i; }
            }

            else if(inventory[i].getID() == item.getID() && inventory[i].getStackSize() < inventory[i].getStackLimit()){
                inventory[i].addToStack();
                return;
            }
        }
        if(emptySlot != -1){
           inventory[emptySlot] = item;
        }
    }

    public Item[] getInventory() {
        return inventory;
    }

    public void loadInventory()  {
        inventory = new Item[6];
        for (Integer[] data : savedInventory) {
            try {
                Item item = ItemFactory.createItem(data[0]);
                item.setStackSize(data[1]);
                inventory[data[2]] = item;
            } catch (DataFormatException e){
                String logMessage = "Failed to load inventory: " + e.getMessage();
                LOGGER.log(Level.WARNING, logMessage);
                MessageWindow.showMessage(logMessage, "Loading error, itemid: " + data[0] + ", slotid: " + data[2]);
            }
        }
    }

    public void saveInventory(){
        savedInventory = new ArrayList<>();
        for (int i = 0; i < inventory.length; i++) {
            if(inventory[i] != null){
                Integer[] saveData = new Integer[]{inventory[i].getID(), inventory[i].getStackSize(), i};
                savedInventory.add(saveData);
            }
        }
    }

    public void useItem(int i) {
        Item item = inventory[i];
        if (item != null) {
            item.onUse(this);
            if(item.getStackSize() == 0){ inventory[i] = null; }
        } else {
            System.out.println("Slot is empty");
        }
    }

    @PostDeserialize
    public void postDeserializeLogic(){
        loadInventory();
        statusColor = Color.GREEN;
        setImage(id, "player");
    }

    public HashMap<Armor.Type, Armor> getEquipped() {
        return equipped;
    }

    public void equipItem(final Armor armor) {
        int i = Arrays.asList(inventory).indexOf(armor);
        if(equipped.get(armor.getArmorType()) != null){
            Armor oldArmor = equipped.get(armor.getArmorType());
            inventory[i] = oldArmor;

            for (Map.Entry<String, Double> buff : oldArmor.getAttributeBuffs().entrySet()) {
                changeAttribute(buff.getKey(), - buff.getValue());
            }
        }
        else{
            inventory[i] = null;
        }
        equipped.put(armor.getArmorType(), armor);
        for (Map.Entry<String, Double> buff : armor.getAttributeBuffs().entrySet()) {
            double buffValue = buff.getValue();
            changeAttribute(buff.getKey(), buffValue);
        }
    }
}
