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

    private static final Color BEIGE = new Color(211, 188, 141);
    private static final Font LEVEL_FONT = new Font("Arial", Font.PLAIN, 8);
    private static final Font MAP_NAME = new Font("Arial", Font.PLAIN, 20);
    private static final Font HEALTH_BAR_FONT = new Font("Arial", Font.PLAIN, 10);
    private static final int AI_TIMER = 500;
    private Player player;
    private static final int CIRCLE_RADIUS = 15;
    private FontMetrics mapNameFontMetrics = null;

    public MapComponent(final MapArea mapArea, final World world) {
        super(mapArea, world);
        player = currentMap.getPlayer();

	final AbstractKeyBind[] keyBinds = new AbstractKeyBind[]
	{new MoveKeyBind("UP", DirectionMapper.Direction.NORTH), new MoveKeyBind("DOWN", DirectionMapper.Direction.SOUTH),
	 new MoveKeyBind("RIGHT", DirectionMapper.Direction.EAST), new MoveKeyBind("LEFT", DirectionMapper.Direction.WEST),
	 new OtherKeyBind("ENTER"), new OtherKeyBind("SPACE"), new OtherKeyBind("Q")};
	for (AbstractKeyBind keyBind : keyBinds) {
	    keyBind.bindKey();
	}

	for (int i = 0; i < player.getInventory().length; i++) {
	    ItemKeyBind keyBind = new ItemKeyBind(i);
	    keyBind.bindKey();
	}
	mapArea.addMapListener(this);

	Timer timer = new Timer(AI_TIMER, new AbstractAction() {
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
	boolean isBattling = currentMap.getCurrentBattle() != null;

	for (List<BattleCharacter> battleCharacters : currentMap.getTeams()) {
	    if (!battleCharacters.isEmpty() && (!isBattling || currentMap.getCurrentBattle().isBattling(battleCharacters.get(0)))){
		for(BattleCharacter bc : battleCharacters) {
		    int x = (int) bc.getPos().getX();
		    int y = (int) bc.getPos().getY();
		    int tileXPos = x * World.TILE_SIZE;
		    int tileYPos = y * World.TILE_SIZE;

		    double[] imageOffset = GameObject.OBJECT_DATA_HANDLER.getImageOffset(bc.getID(), bc.getType());
		    g.drawImage(bc.getImage(), tileXPos + (int)Math.round(imageOffset[0]), tileYPos+ (int)Math.round(imageOffset[1]), this);

		    final int hpBarXSize = World.TILE_SIZE - 8;
		    final int hpBarYSize = 12;
		    UIVisuals.drawBar(g, bc.getStatusColor(), tileXPos, tileYPos, hpBarXSize, hpBarYSize,
				      bc.getHP() / (double)bc.getMaxHP(), Integer.toString(bc.getHP()), HEALTH_BAR_FONT);
		    UIVisuals.drawCenteredString(g, bc.getName(), Color.WHITE, true, false, false,
						 new Rectangle(tileXPos + 2, tileYPos - hpBarYSize, hpBarXSize - 4, 10), Color.BLACK, UIVisuals.NAME_FONT);

		    // Drawing level icon
		    g.setColor(BEIGE);
		    g.fillOval(tileXPos + hpBarXSize - 5, tileYPos - CIRCLE_RADIUS / 6, CIRCLE_RADIUS, CIRCLE_RADIUS);
		    g.setColor(Color.BLACK);
		    g.fillOval(tileXPos + hpBarXSize - 4,tileYPos + 1 - CIRCLE_RADIUS / 6, CIRCLE_RADIUS - 2, CIRCLE_RADIUS - 2);
		    UIVisuals.drawCenteredString(g, Integer.toString(bc.getLevel()), Color.WHITE, true, false, false,
						 new Rectangle(tileXPos + hpBarXSize - 4, tileYPos - CIRCLE_RADIUS / 6 + 1, CIRCLE_RADIUS, CIRCLE_RADIUS), Color.BLACK, LEVEL_FONT);
		}
	    }
	}
	if (!isBattling) { paintNPCs(g); }

	paintObjects(g);

	// Drawing Map Title
	if (mapNameFontMetrics == null){mapNameFontMetrics = g.getFontMetrics(MAP_NAME);}
	String mapName = currentMap.getName();
	int textSizeX = mapNameFontMetrics.stringWidth(mapName);
	final int textSizeY = 20;
	UIVisuals.drawCenteredString(g, mapName, Color.WHITE, true, true, false,
				     new Rectangle(getWidth()/2, 5, textSizeX, textSizeY), Color.BLACK, MAP_NAME);
    }

    @Override public void mapChanged(int i) {
        if (i > -1) {
            currentMap = world.getMapArea(i);
            currentMap.addMapListener(this);
        }
        repaint();
    }

    private abstract class AbstractKeyBind extends AbstractAction{
        protected String key;

	protected AbstractKeyBind(final String key) {
	    this.key = key;
	}
	protected void bindKey() {
	    getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
	    getActionMap().put(key, this);
	}
    }

    private class MoveKeyBind extends AbstractKeyBind
    {
	private DirectionMapper.Direction direction;

	private MoveKeyBind(final String key, final DirectionMapper.Direction direction) {
	    super(key);
	    this.direction = direction;
	}

	@Override public void actionPerformed(final ActionEvent actionEvent) {

	    if(currentMap.getCurrentBattle() != null){
	     	if (player.getAttackState()){
	     	    currentMap.attemptAttack(player, currentMap.getBattleCharacterAt(player.getAdjacentPoint(direction)));
		}
	     	else currentMap.moveInBattle(direction, player);
	    }
	    else {
	        currentMap.moveCharacter(player.getAdjacentPoint(direction), player);
	    }
	}
    }
    private class OtherKeyBind extends AbstractKeyBind {

	private OtherKeyBind(final String key) {
	   super(key);
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

    private class ItemKeyBind extends AbstractKeyBind
    {
        private int i;

	private ItemKeyBind(final int i) {
	    super(Integer.toString(i + 1));
	    this.i = i;
	}


	@Override public void actionPerformed(final ActionEvent e){ currentMap.useItem(i); }
    }

}
