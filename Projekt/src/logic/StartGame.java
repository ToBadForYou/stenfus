package logic;

import resources.TextResource;
import visual.GameViewer;

/**
 * This class is simply used to start the game, it attempts to load the world data, npcs and players and then starts the game
 */

public class StartGame
{
    private StartGame() {
    }

    public static void main(String[] args) {
	World world = new World("test");
	TextResource worldLoader = new TextResource();
	String worldData = worldLoader.getJSONData("world", "test");
	String npcData = worldLoader.getJSONData("worldnpcs", "test");
	    if (worldData == null) {
	    System.out.println("Tried to load non-existing world.");
	} else {
		if (npcData == null){
			npcData = "[]";
		}
	    world.loadWorld(worldData, npcData);
	    String playerData = worldLoader.getJSONData("playerdata", "1");
	    MapArea currentMap = world.loadPlayer(playerData);
	    //MapArea map1 = world.getMapArea(0);

	    GameViewer window = new GameViewer();
	    window.showGame(currentMap, world);

	}

    }

}
