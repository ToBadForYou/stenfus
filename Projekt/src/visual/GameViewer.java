package visual;

import logic.MapArea;
import logic.World;

import javax.swing.*;
import java.awt.*;

/**
 * This class handles the creation of every component required to show the game
 * and the world editor properly, it is called from WorldEditor and StartGame classes
 */

public class GameViewer
{
    public GameViewer() {

    }

    public void showGame(MapArea map, World world){
	JFrame frame = new JFrame();
	frame.setLayout(new BorderLayout());

	MapComponent mapComp = new MapComponent(map, world);
	PlayerInformation playerComp = new PlayerInformation(map, world);
	frame.add(mapComp, BorderLayout.CENTER);
	frame.add(playerComp, BorderLayout.WEST);

	frame.pack();
	frame.setVisible(true);
    }

    public void showWorldEditor(World world, MapArea map){
	JFrame frame = new JFrame();
	frame.setLayout(new BorderLayout());

	WorldEditorComponent worldEdit = new WorldEditorComponent(world, map);
	WorldEditorOption worldOptions = new WorldEditorOption(world, worldEdit);
	frame.add(worldEdit, BorderLayout.CENTER);
	frame.add(worldOptions, BorderLayout.EAST);

	frame.pack();
	frame.setVisible(true);
    }



}
