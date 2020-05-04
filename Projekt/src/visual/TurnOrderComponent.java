package visual;

import logic.BattleCharacter;
import logic.MapArea;
import logic.MapListener;
import logic.World;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class handles the representation of turn order inside battles
 */

public class TurnOrderComponent extends JPanel implements MapListener
{
    private MapArea currentMap;
    private World world;

    public TurnOrderComponent(MapArea map, World world) {
	currentMap = map;
	this.world = world;
	currentMap.addMapListener(this);
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	if(currentMap.getCurrentBattle() != null){
	    List<BattleCharacter> battlers = currentMap.getCurrentBattle().getBattlers();
	    for (int i = 0; i < battlers.size(); i++) {
		BattleCharacter bc = battlers.get(i);

		if (currentMap.getCurrentBattle().getcurrentBattler().equals(bc)) {
		    g.setColor(Color.RED);
		} else {
		    g.setColor(Color.BLACK);
		}
		g.fillRect((i % 4) * (World.TILESIZE + 5) + 5, 5 + (i / 4) * (World.TILESIZE + 5), World.TILESIZE + 4,
			   World.TILESIZE + 4);
		g.setColor(Color.WHITE);
		g.fillRect(7 + (i % 4) * (World.TILESIZE + 5), 7 + (i / 4) * (World.TILESIZE + 5), World.TILESIZE, World.TILESIZE);

		g.drawImage(bc.getImage(),5 + (i % 4) * (World.TILESIZE + 5), 5 + (i / 4) * (World.TILESIZE + 5), this);
	    }
	}
    }

    @Override public void mapChanged(final int i) {
        if (i > -1){
            currentMap = world.getMapArea(i);
	    currentMap.addMapListener(this);
	}

	    repaint();


    }

    @Override public Dimension getPreferredSize(){
        return new Dimension(PlayerInformation.STATSWIDTH, (World.TILESIZE) + 40);
    }
}
