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
		BufferedImage tileImage = tile.getImage();
		g.drawImage(tileImage, x * World.TILESIZE, y * World.TILESIZE, this);
		if (editor && tile.getCollision()) {
		    g.setColor(Color.RED);
		    g.drawRect(x * World.TILESIZE, y * World.TILESIZE, World.TILESIZE-1, World.TILESIZE-1);
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
		    Double[] imageOffset = GameObject.objectData.getImageOffset(tile.getStaticObjectID(), "static");
		    Integer[] objSize = tile.getSize();
		    g.drawImage(staticObject, x * World.TILESIZE + (World.TILESIZE - objSize[0]) + (int)Math.round(imageOffset[0]), y * World.TILESIZE + (World.TILESIZE - objSize[1]) + (int)Math.round(imageOffset[1]), this);
		}
	    }
	}
    }

    public void paintNPCs(Graphics g){
		for (NPC npc : currentMap.getNpcList()) {
			Point npcPos = (Point)npc.getPos();
			int x = (int) npcPos.getX();
			int y = (int) npcPos.getY();
			Double[] imageOffset = GameObject.objectData.getImageOffset(npc.getId(), "npc");
			g.drawImage(npc.getImage(), x * World.TILESIZE + (int)Math.round(imageOffset[0]), y * World.TILESIZE + (int)Math.round(imageOffset[1]), this);
			UIVisuals.drawCenteredString(g, npc.getName(), Color.WHITE, true, false, false, new Rectangle(x * World.TILESIZE, y * World.TILESIZE,  World.TILESIZE, 10), Color.BLACK, UIVisuals.NAMEFONT);
			if(npc instanceof QuestNPC){
				String symbol = "?";
				g.setColor(Color.YELLOW);
				g.setFont(UIVisuals.QUESTSYMBOL);
				int i = ((QuestNPC) npc).getQuestStatus(currentMap.getPlayer());
				switch (i){
					case 0:
						g.setColor(Color.WHITE);
					case 1:
						symbol = "!";
						break;
					case 2:
						g.setColor(Color.WHITE);
						break;
				}
				if (i > -1) {
					g.drawString(symbol, x * World.TILESIZE + 25, y * World.TILESIZE - 3);
				}
			}
		}
	}

    public void setCurrentMap(final MapArea currentMap) {
	this.currentMap = currentMap;
    }

    public MapArea getCurrentMap() {
	return currentMap;
    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(currentMap.getWidth()*World.TILESIZE, currentMap.getHeight()*World.TILESIZE);
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
    }
}
