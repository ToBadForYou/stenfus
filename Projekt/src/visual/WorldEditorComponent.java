package visual;


import logic.ExitPoint;
import logic.MapArea;
import logic.SpawnPoint;
import logic.World;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This class handles the world editors visuals, it is more or less the same as MapComponent but with certain
 * visual differences, it also communicates with the current selected MapArea to announce when changes are made.
 * It also has a MouseListener which uses the left, right and scroll click and depending on settings set in the
 * WorldEditorOption it runs functions accordingly
 */

public class WorldEditorComponent extends AbstractComponent implements MouseListener
{
    private boolean collision;
    private int tile, object, misc, miscVal, teamID;
    private List<Integer> questIDs;

    public WorldEditorComponent(final World world, final MapArea mapArea) {
	super(mapArea, world);
	collision = false;
		miscVal = 0;
		teamID = 1;
	misc = 0;
	tile = 0;
	object = 0;
	questIDs = new ArrayList<>();
	this.addMouseListener(this);
    }

    public void finishEditor(){
	world.saveWorld();
	System.exit(0);
    }

    public void setMisc(int miscID){
	misc = miscID;
    }

    public void setMiscVal(final int miscVal) {
        this.miscVal = miscVal;
    }

    public void setCollision(final boolean collision) {
	this.collision = collision;
    }

    public void setTile(final int tile) {
	this.tile = tile;
    }

    public void setObject(final int object) {
	this.object = object;
    }

    public void setQuest(int questID, boolean toggle){
        if(toggle && !questIDs.contains(questID)){
            questIDs.add(questID);
	} else if (questIDs.contains(questID)){
            questIDs.remove(Integer.valueOf(questID));
	}
    }

    public void setLevel(final int level){
        currentMap.setLevel(level);
    }

    public void setMapName(final String mapName){currentMap.setName(mapName);}

    public void setTeamID(final int teamID) {
	this.teamID = teamID;
    }

    public void toggleEnemyType(int id, boolean toggle){
    	currentMap.toggleEnemyType(id, toggle);
	}

	public List<Integer> getEnemyTypes(){return currentMap.getEnemyTypes();}

    private void onMouseClicked(int mouseX, int mouseY, int buttonClicked){
        int x = mouseX/World.TILESIZE;
	int y = mouseY/World.TILESIZE;
	if (x >= 0 && x <= currentMap.getWidth() && y >= 0 && y <= currentMap.getHeight()) {
	    if (buttonClicked == 1) {
		currentMap.setTile(x, y, tile, collision);
	    } else if (buttonClicked == 3) {
		currentMap.setObject(x, y, object, collision);
	    } else if (buttonClicked == 2) {
	        if (misc == 2){
	            if(miscVal != currentMap.getId()) {
					currentMap.toggleExitPoint(x, y, miscVal);
				}
			} else if (misc == 3){
				world.toggleNPC(currentMap.getId(), x, y, miscVal, questIDs);
			} else {
				currentMap.toggleSpawnPoint(x, y, misc, teamID);
			}
	    }
	    repaint();
	}
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	paintTiles(g, true);
	paintNPCs(g);
	paintObjects(g);

	for (SpawnPoint spawnPoint: currentMap.getBattleStartPos()) {
	    int x = (int)spawnPoint.getX();
	    int y = (int)spawnPoint.getY();
	    UIVisuals.drawCenteredString(g, "Battle " + spawnPoint.getTeamID(), Color.BLACK, true, true, true, new Rectangle(x * World.TILESIZE, y * World.TILESIZE,World.TILESIZE, World.TILESIZE/2-6), Color.WHITE, UIVisuals.WORLDEDITORFONT);
	}

	for (SpawnPoint spawnPoint: currentMap.getEnemySpawns()) {
	    int x = (int)spawnPoint.getX();
	    int y = (int)spawnPoint.getY();
	    UIVisuals.drawCenteredString(g, "Mob " + spawnPoint.getTeamID(), Color.BLACK, true, true, true, new Rectangle(x * World.TILESIZE, y * World.TILESIZE+World.TILESIZE/2+6,World.TILESIZE, World.TILESIZE/2-6), Color.WHITE, UIVisuals.WORLDEDITORFONT);
	}

	for (ExitPoint exitPoint: currentMap.getExitPoints()) {
	    int x = (int)exitPoint.getX();
	    int y = (int)exitPoint.getY();
	    UIVisuals.drawCenteredString(g, "Exit " + exitPoint.getMapAreaID(), Color.BLACK, true, true, true, new Rectangle(x * World.TILESIZE, y * World.TILESIZE+World.TILESIZE/4,World.TILESIZE, World.TILESIZE/2-6), Color.WHITE, UIVisuals.WORLDEDITORFONT);
	}
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
	onMouseClicked(arg0.getX(), arg0.getY(), arg0.getButton());
    }

    @Override
    public void mouseEntered(MouseEvent arg0) { }

    @Override
    public void mouseExited(MouseEvent arg0) { }

    @Override
    public void mousePressed(MouseEvent arg0) { }

    @Override
    public void mouseReleased(MouseEvent arg0) { }
}
