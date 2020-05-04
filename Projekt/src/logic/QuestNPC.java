package logic;

import java.awt.Point;
import java.util.List;

/**
 * This class represents a certain NPC that contains different Quests for the player to obtain and complete for a reward.
 */
public class QuestNPC extends AbstractNPC
{
    private List <Integer> questList;

      public QuestNPC(final int id, final int mapID, final Point pos, final List<Integer> questIDs) {
	  super(id, "npc", mapID, pos);
	  this.questList = questIDs;
      }

    @Override
    public void interaction(Player player){
        if(!questList.isEmpty()) {
	    int i = questList.get(0);
	    Quest nextQuest = World.getQuest(i);
	    if (nextQuest.canPickUP(player)) {
		player.addQuest(i);
		System.out.println("Picked up " + nextQuest.getName());
	    } else if (nextQuest.checkForCompletion(player.getQuestProgress(i))) {
		System.out.println("Completed quest " + nextQuest.getName());
		nextQuest.gainRewards(player);
		this.questList.remove(0);
		player.completeQuest(i);
	    }
	}
    }

    public int getQuestStatus(Player player){
      	if (questList.isEmpty()){ return -1; }

      	else if(World.getQuest(questList.get(0)).canPickUP(player)){return 1; }

      	else if (player.getQuestProgress(questList.get(0)) == null){ return 0; }

      	else if(!World.getQuest(questList.get(0)).checkForCompletion(player.getQuestProgress(questList.get(0)))) {
			return 2;
		}
      	else { return 3; }
	}

    @Override
    public int getmapID() {
	    return mapID;
    }
}
