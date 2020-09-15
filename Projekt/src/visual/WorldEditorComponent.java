package visual;

import logic.GamePoint;
import logic.MapArea;
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
    private static final Font WORLD_EDITOR_FONT = new Font("Arial", Font.PLAIN, 15);
    private boolean collision;
    private int tile, object, misc, miscValue, teamID;
    private List<Integer> questIDs;

    public WorldEditorComponent(final World world, final MapArea mapArea) {
	super(mapArea, world);
	collision = false;
	miscValue = 0;
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

    public void setMiscValue(final int miscValue) {
        this.miscValue = miscValue;
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
        int x = mouseX/World.TILE_SIZE;
	int y = mouseY/World.TILE_SIZE;
	if (x >= 0 && x <= currentMap.getWidth() && y >= 0 && y <= currentMap.getHeight()) {
	    if (buttonClicked == 1) {
		currentMap.setTile(x, y, tile, collision);
	    } else if (buttonClicked == 3) {
		currentMap.setObject(x, y, object, collision);
	    } else if (buttonClicked == 2) {
		if (misc == 2){
		    if(miscValue != currentMap.getId()) {
			currentMap.toggleExitPoint(x, y, miscValue);
		    }
		} else if (misc == 3){
		    world.toggleNPC(currentMap.getId(), x, y, miscValue, questIDs);
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

	List<GamePoint> gamePoints = new ArrayList<>(currentMap.getBattleStartPos());
	paintPoint(g, gamePoints, "Battle ", 0);

	gamePoints.clear();
	gamePoints.addAll(currentMap.getEnemySpawns());
	paintPoint(g, gamePoints, "Mob ", World.TILE_SIZE/2+6);

	gamePoints.clear();
	gamePoints.addAll(currentMap.getExitPoints());
	paintPoint(g, gamePoints, "Exit ", World.TILE_SIZE/4);
    }

    private void paintPoint(Graphics g, List<GamePoint> points, String text, int yPos){
	for (GamePoint point: points) {
	    int x = (int)point.getX();
	    int y = (int)point.getY();
	    int halfTileSize = World.TILE_SIZE/2;
	    UIVisuals.drawCenteredString(g, text + point.getID(), Color.BLACK, true, true,true,
					 new Rectangle(x * World.TILE_SIZE, y * World.TILE_SIZE + yPos,World.TILE_SIZE, halfTileSize-6), Color.WHITE, WORLD_EDITOR_FONT);
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
