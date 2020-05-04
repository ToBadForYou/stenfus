package visual;

import logic.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * This class handles the key input from the user and the visual representation of the game
 * For example drawing objects, NPCs, enemies and the player
 */

public class MapComponent extends AbstractComponent implements MapListener
{
    private Player player;
    private static final int CIRCLERADIE = 15;

    public MapComponent(final MapArea mapArea, final World world) {
		super(mapArea, world);
		player = currentMap.getPlayer();
		//Dessa objekt behöver ej sparas då de används till lyssnare endast
		new MoveKeyBind("UP", Direction.Dir.NORTH);
		new MoveKeyBind("DOWN", Direction.Dir.SOUTH);
		new MoveKeyBind("RIGHT", Direction.Dir.EAST);
		new MoveKeyBind("LEFT", Direction.Dir.WEST);
		new OtherKeyBind("ENTER");
		new OtherKeyBind("SPACE");
		new OtherKeyBind("Q");
		for (int i = 0; i < player.getInventory().length; i++) {
		    new ItemKeyBind(i);
		}
		mapArea.addMapListener(this);

		Timer timer = new Timer(500, new AbstractAction() {
		    @Override
		    public void actionPerformed(final ActionEvent actionEvent) {
			if (currentMap.getCurrentBattle() == null) {
			    if (currentMap.getEnemyAi() != null) {
				currentMap.getEnemyAi().moveAll();
			    }
			} else {
			    currentMap.getCurrentBattle().update();
			    }
		    }
		});
		timer.start();
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	paintTiles(g, false);

	for (List<BattleCharacter> battleCharacters : currentMap.getTeams()) {
	    if (!battleCharacters.isEmpty() && (currentMap.getCurrentBattle() == null || currentMap.getCurrentBattle().isBattling(battleCharacters.get(0)))){
		for(BattleCharacter bc : battleCharacters) {
		    int x = (int) bc.getPos().getX();
		    int y = (int) bc.getPos().getY();
		    Double[] imageOffset = GameObject.objectData.getImageOffset(bc.getID(), bc.getType());
		    g.drawImage(bc.getImage(), x * World.TILESIZE + (int)Math.round(imageOffset[0]), y * World.TILESIZE + (int)Math.round(imageOffset[1]), this);

		    int xSize = (int)(World.TILESIZE/1.15);

		    g.setColor(bc.getStatusColor());
		    g.fillRect(x * World.TILESIZE + 2, y * World.TILESIZE, xSize * bc.getHP() / bc.getMaxHP()- 4, 10);

		    UIVisuals.drawCenteredString(g, Integer.toString(bc.getHP()), Color.BLACK, true, false, true, new Rectangle(x * World.TILESIZE + 2, y * World.TILESIZE, xSize - 4, 10), Color.BLACK, UIVisuals.HEALTHBARFONT);
		    UIVisuals.drawCenteredString(g, bc.getName(), Color.WHITE, true, false, false, new Rectangle(x * World.TILESIZE + 2, y * World.TILESIZE - 12, xSize - 4, 10), Color.BLACK, UIVisuals.NAMEFONT);

		    g.setColor(UIVisuals.BEIGE);
		    g.fillOval(x * World.TILESIZE + xSize - 5, y * World.TILESIZE - CIRCLERADIE/6,CIRCLERADIE,CIRCLERADIE);

		    g.setColor(Color.BLACK);
		    g.fillOval(x * World.TILESIZE + xSize - 4,y * World.TILESIZE + 1 - CIRCLERADIE/6,CIRCLERADIE - 2,CIRCLERADIE - 2);

		    UIVisuals.drawCenteredString(g, Integer.toString(bc.getLevel()), Color.WHITE, true, false, false, new Rectangle(x * World.TILESIZE + xSize - 4, y * World.TILESIZE - CIRCLERADIE/6 + 1, CIRCLERADIE, CIRCLERADIE), Color.BLACK, UIVisuals.LEVELFONT);
		}
	    }
	}
	if (currentMap.getCurrentBattle() == null) { paintNPCs(g); }
	paintObjects(g);

	FontMetrics metrics = g.getFontMetrics(UIVisuals.MAPNAME);
	String mapName = currentMap.getName();
	int textSizeX = metrics.stringWidth(mapName);

	UIVisuals.drawCenteredString(g, mapName, Color.WHITE, true, true, false, new Rectangle(getWidth()/2, 5, textSizeX, 20), Color.BLACK, UIVisuals.MAPNAME);
    }

    @Override public void mapChanged(int i) {
        if (i > -1) {
            currentMap = world.getMapArea(i);
            currentMap.addMapListener(this);
        }
        repaint();
    }

    private class MoveKeyBind extends AbstractAction
    {
	private Direction.Dir direction;

	private MoveKeyBind(final String key, final Direction.Dir direction) {
	    this.direction = direction;
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
	    getActionMap().put(key, this);

	}
	@Override public void actionPerformed(final ActionEvent actionEvent) {

	    if(currentMap.getCurrentBattle() != null){
	     	if (player.getAttackState()){
	     	    currentMap.attemptAttack(player, currentMap.getBattleCharacterAt(player.directionToPoint(direction)));
		}
	     	else currentMap.moveInBattle(direction, player);
	    }
	    else {
	        currentMap.moveCharacter(player.directionToPoint(direction), player);
	    }
	}
    }
    private class OtherKeyBind extends AbstractAction
    {
        private String key;
	private OtherKeyBind(final String key) {
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
	    getActionMap().put(key, this);
	    this.key = key;
	}
	@Override public void actionPerformed(final ActionEvent actionEvent) {
	    if(key.equals("ENTER")){
	        currentMap.getCurrentBattle().endTurn(player);
	    }
	    if (key.equals("SPACE")){
	        currentMap.switchPlayerState();
	    }
	    if(key.equals("Q")){ currentMap.cleave(player); }
	}
    }

    private class ItemKeyBind extends AbstractAction
    {
        private int i;

	private ItemKeyBind(final int i) {
	    this.i = i;
	    String key = Integer.toString(i + 1);
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
	    getActionMap().put(key, this);
	}


	@Override public void actionPerformed(final ActionEvent e){ currentMap.useItem(i); }
    }

}
