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

		if (currentMap.getCurrentBattle().getCurrentBattler().equals(bc)) {
		    g.setColor(Color.RED);
		} else {
		    g.setColor(Color.BLACK);
		}

		int spritePosX = (i % 4) * (World.TILE_SIZE + 5);
		int spritePosY = (i / 4) * (World.TILE_SIZE + 5);

		g.fillRect(spritePosX + 5, 5 + spritePosY, World.TILE_SIZE + 4, World.TILE_SIZE + 4);
		g.setColor(Color.WHITE);
		g.fillRect(7 + spritePosX, 7 + spritePosY, World.TILE_SIZE, World.TILE_SIZE);

		g.drawImage(bc.getImage(),5 + spritePosX, 5 + spritePosY, this);
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
        final int height =  World.TILE_SIZE + 40;
        return new Dimension(PlayerInformation.PLAYER_INFO_WIDTH, height);
    }
}
