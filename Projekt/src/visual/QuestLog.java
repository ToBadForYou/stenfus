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
    private static final Font QUEST_FONT = new Font("Arial", Font.PLAIN, 15);
    private static final Color GREEN = new Color(34,139,34);
    private static final int START_Y = 15;

    public QuestLog(final List<Integer>[] questProgress) {
	this.questProgress = questProgress;
    }

    @Override public void paintComponent(Graphics g){
	super.paintComponent(g);

        g.setFont(UIVisuals.QUEST_HEAD);
        g.setColor(Color.ORANGE);
        int y = START_Y;
        g.drawString("Quest Log", 5, y);
        final int yPadding = 21;
        y += yPadding;
        g.setFont(QUEST_FONT);

	for (int i = 0; i < questProgress.length; i++) {
	    if (questProgress[i] != null){
		Quest quest = World.getQuest(i);
		if(quest.isCompleted(questProgress[i])){
		    g.setColor(GREEN);
		}
		else{
		    g.setColor(Color.BLACK);
		}
		final int x = 20;
		g.drawString(quest.getName(), x, y);
		final int dotSize = 8;
		final int dotY = y - 11;
		g.fillOval(9, dotY, dotSize, dotSize);

		final int yPadding2 = 16;
		y += yPadding2;
		for (int j = 0; j < quest.getKillObjectives().length; j++) {
		    g.fillRect(x - 3, y - 8, 9, 3);
		    g.drawString(quest.getKillObjectives()[j].getDescription() + " " + questProgress[i].get(j)+ " / " + quest.getKillObjectives()[j].getGoal(), x + 8, y);
		    y += 10;
		}
	    }
	}
    }

    @Override public void mapChanged(final int i) { repaint(); }

    @Override public Dimension getPreferredSize() {
	return new Dimension(PlayerInformation.PLAYER_INFO_WIDTH, 60);
    }
}
