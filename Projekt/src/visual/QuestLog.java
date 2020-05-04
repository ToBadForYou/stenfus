package visual;

import logic.MapListener;
import logic.Quest;
import logic.World;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class handles the visual representation of the quests
 * For example progress of current accepted quests and the information regarding the quest
 */

public class QuestLog extends JPanel implements MapListener
{
    private List<Integer>[] questProgress;

    public QuestLog(final List<Integer>[] questProgress) {
	this.questProgress = questProgress;
    }

    @Override public void paintComponent(Graphics g){
	super.paintComponent(g);

        g.setFont(UIVisuals.QUESTHEAD);
        g.setColor(Color.ORANGE);
        int y = 15;
        g.drawString("Quest Log", 5, y);
        y += 21;
        g.setFont(UIVisuals.QUEST);

	for (int i = 0; i < questProgress.length; i++) {
	    if (questProgress[i] != null){
		Quest quest = World.getQuest(i);
		if(quest.checkForCompletion(questProgress[i])){
		    g.setColor(new Color(34,139,34));
		}
		else{
		    g.setColor(Color.BLACK);
		}
		g.fillOval(9, y - 11, 8, 8);
		g.drawString(quest.getName(), 20, y);
		y += 16;
		for (int j = 0; j < quest.getKillObjectives().length; j++) {
		    g.fillRect(17, y - 8, 9, 3);
		    g.drawString(quest.getKillObjectives()[j].getDescription() + " " + questProgress[i].get(j)+ " / " + quest.getKillObjectives()[j].getGoal(), 28, y);
		    y += 10;
		}
	    }
	}
    }

    @Override public void mapChanged(final int i) { repaint(); }

    @Override public Dimension getPreferredSize() {
	return new Dimension(PlayerInformation.STATSWIDTH, 60);
    }
}
