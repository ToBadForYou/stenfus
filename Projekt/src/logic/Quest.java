package logic;

import logging.Logging;
import visual.MessageWindow;

import java.util.List;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

/**
 * This class represents a quest in the game, which are all stored in the World object
 * It also helps the Player to update its progress of the Quest
 */
public class Quest
{
    private static final Logging LOGGER = new Logging("inventory");

    private int levelRequirement;
    private List<Quest> questsRequirements;
    private KillObjective[] killObjectives;
    private int xpGain, goldGain; // This field is not being used, but should be in the future.
    private int[] itemRewards;
    private String name;
    private int id;

    public Quest(final int levelRequirement, final List<Quest> questsRequirements, final KillObjective[] killObjectives,
                 final int xpGain, final int goldGain, final int[] itemRewards, final String name, final int id)
    {
        this.levelRequirement = levelRequirement;
        this.questsRequirements = questsRequirements;
        this.killObjectives = killObjectives;
        this.xpGain = xpGain;
        this.goldGain = goldGain;
        this.itemRewards = itemRewards;
        this.name = name;
        this.id = id;
    }

    public boolean canPickUP(Player player){
        return player.getLevel() >= levelRequirement && player.getCompletedQuests().containsAll(questsRequirements) && player.getQuestProgress(id) == null;
    }

    public boolean isCompleted(List<Integer> questProgresses) {
        for (int i = 0; i < killObjectives.length; i++) {
            if(!killObjectives[i].isCompleted(questProgresses.get(i))){ return false; }
        }
        return true;
    }

    public void gainRewards(Player player){
        player.gainXp(xpGain);
        for (int itemReward : itemRewards) {
            try {
                player.addItemToInventory(ItemFactory.createItem(itemReward));
            } catch (DataFormatException e){
                String logMessage = "Failed to add item to inventory: " + e.getMessage();
                LOGGER.log(Level.WARNING, logMessage);
                MessageWindow.showMessage(logMessage, "Item creation error, itemid: " + itemReward);
            }
        }
    }

    public String getName(){ return name; }

    public KillObjective[] getKillObjectives() { return killObjectives; }

    public void updateProgress(List<Integer> questProgress, List<Enemy> enemiesKilled){
        for (int i = 0; i < questProgress.size(); i++) {
            questProgress.set(i, killObjectives[i].updateCompletion(enemiesKilled, questProgress.get(i)));
        }
    }
}
