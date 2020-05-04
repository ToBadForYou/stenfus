package logic;

import resources.TextResource;
import visual.GameViewer;

import javax.swing.*;

/**
 * This class is used to start the world editor, it takes user input for the Worlds name, if it exists it will load
 * the existing world so you can edit it, otherwise it will generate a new world with the given name
 */

public class WorldEditor
{
    public static void main(String[] args) {
	JFrame frame = new JFrame("World Editor");

	String name = JOptionPane.showInputDialog(frame, "Enter name for the world");

	World world = new World(name);
	TextResource worldLoader = new TextResource();
	String worldData = worldLoader.getJSONData("world", name);
	String npcData = worldLoader.getJSONData("worldnpcs", name);
	if (worldData != null) {
		if (npcData == null){
			npcData = "[]";
		}
	    world.loadWorld(worldData, npcData);
	} else {
	    world.addEmptyMapArea();
    	}

	MapArea map1 = world.getMapArea(0);

	GameViewer window = new GameViewer();
	window.showWorldEditor(world, map1);
    }
}
