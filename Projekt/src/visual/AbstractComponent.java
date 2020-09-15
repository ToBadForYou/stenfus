package visual;

import logic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Abstract class for the MapComponent and WorldEditorComponent
 * Handles the painting of tiles, objects and NPCs
 */

public abstract class AbstractComponent extends JComponent
{
    private static final Font QUEST_SYMBOL = new Font("Arial", Font.BOLD, 23);
    protected MapArea currentMap;
    protected World world;

    protected AbstractComponent(final MapArea currentMap, final World world) {
        this.world = world;
    	this.currentMap = currentMap;
    }

    public void paintTiles(Graphics g, boolean editor){
	for (int y = 0; y < currentMap.getHeight(); y++) {
	    for (int x = 0; x < currentMap.getWidth(); x++) {
		MapTile tile = currentMap.getMapTile(x, y);
		g.drawImage(tile.getImage(), x * World.TILE_SIZE, y * World.TILE_SIZE, this);
		if (editor && tile.getCollision()) {
		    g.setColor(Color.RED);
		    g.drawRect(x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE-1, World.TILE_SIZE-1);
		}
	    }
	}
    }

    public void paintObjects(Graphics g){
	for (int y = 0; y < currentMap.getHeight(); y++) {
	    for (int x = 0; x < currentMap.getWidth(); x++) {
		MapTile tile = currentMap.getMapTile(x, y);
		BufferedImage staticObject = tile.getObjectImage();
		if (staticObject != null) {
		    double[] imageOffset = GameObject.OBJECT_DATA_HANDLER.getImageOffset(tile.getStaticObjectID(), "static");
		    int[] objSize = tile.getSize();
		    g.drawImage(staticObject, x * World.TILE_SIZE + (World.TILE_SIZE - objSize[0]) + (int)Math.round(imageOffset[0]),
				y * World.TILE_SIZE + (World.TILE_SIZE - objSize[1]) + (int)Math.round(imageOffset[1]), this);
		}
	    }
	}
    }

    public void paintNPCs(Graphics g){
	for (InteractableCharacter interactableCharacter : currentMap.getInteractableCharacters()) {
	    Point npcPos = (Point) interactableCharacter.getPos();
	    int x = (int) npcPos.getX();
	    int y = (int) npcPos.getY();
	    double[] imageOffset = GameObject.OBJECT_DATA_HANDLER
		    .getImageOffset(interactableCharacter.getID(), "npc");
	    g.drawImage(interactableCharacter.getImage(), x * World.TILE_SIZE + (int)Math.round(imageOffset[0]),
			y * World.TILE_SIZE + (int)Math.round(imageOffset[1]), this);
	    UIVisuals.drawCenteredString(g, interactableCharacter.getName(), Color.WHITE, true, false, false,
					 new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE, World.TILE_SIZE, 10), Color.BLACK, UIVisuals.NAME_FONT);
	    if(interactableCharacter instanceof QuestGiver){
		String symbol = "?";
		g.setColor(Color.YELLOW);
		Color color = Color.YELLOW;
		g.setFont(QUEST_SYMBOL);
		int i = ((QuestGiver) interactableCharacter).getQuestStatus(currentMap.getPlayer());
		switch (i){
		    case 0:
			    color = Color.WHITE;
		    case 1:
			    symbol = "!";
			    break;
		    case 2:
			    color = Color.WHITE;
			    break;
		}
		if (i > -1) {
		    UIVisuals.drawCenteredString(g, symbol, color, true,false, false,
						 new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE - 10, World.TILE_SIZE, 10), Color.BLACK, QUEST_SYMBOL);
		}
	    }
	}
    }

    public void setCurrentMap(final MapArea currentMap) {
	this.currentMap = currentMap;
    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(currentMap.getWidth()*World.TILE_SIZE, currentMap.getHeight()*World.TILE_SIZE);
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
    }
}
