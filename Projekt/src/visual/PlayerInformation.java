package visual;

import logic.MapArea;
import logic.MapListener;
import logic.Player;
import logic.World;
import resources.ObjectDataHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used to display information about the player (stats etc) and creates the
 * StatsComponent, TurnOrderComponent and QuestLog classes, this is what is displayed on the
 * leftside of the screen inside the game
 */

public class PlayerInformation extends JPanel implements MapListener
{
    /**
     * Size player information width, used by more than one class
     * */
    public static final int PLAYER_INFO_WIDTH = 285;
    /**
     * Icon size should be same for every type of object, use by more than one class
     */
    public static final double ICON_SIZE = 16.0;
    private static final Color PURPLE = new Color(138, 43, 226);
    private static final Font STATS_HEADER_FONT = new Font("Arial", Font.PLAIN, 20);
    private static final int X_PADDING = 11, Y_PADDING = 24, Y_PADDING_INCREASE = 30;
    private Player player;
    private World world;
    private List<StatsComponent> statsComps = new ArrayList<>();
    private FontMetrics statsFontMetrics = null;

    public PlayerInformation(MapArea map, World world) {
        player = map.getPlayer();
        this.world = world;
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxLayout);

        add(Box.createRigidArea(new Dimension(0, 135)));

        for (Map.Entry<String,Integer> attribute: player.getAttributes().entrySet()) {
            BufferedImage icon = ObjectDataHandler.RESOURCES
		    .getImage(attribute.getKey() + ".png", new double[] { ICON_SIZE, ICON_SIZE });
            StatsComponent stat = new StatsComponent(player, attribute.getKey(), attribute.getValue(), icon);
            add(stat);
            map.addMapListener(stat);
            statsComps.add(stat);
        }
        add(Box.createRigidArea(new Dimension(0, 5)));

        QuestLog questLog = new QuestLog(player.getQuestProgresses());
        add(questLog);

        TurnOrderComponent turnOrder = new TurnOrderComponent(map, world);
        add(turnOrder);

        ArmorComponent armor = new ArmorComponent(player.getEquipped());
        add(armor);

        ItemComponent inventory = new ItemComponent(player.getInventory());
        add(inventory);

        map.addMapListener(armor);
        map.addMapListener(questLog);
        map.addMapListener(inventory);
        map.addMapListener(this);
        add(Box.createVerticalGlue());
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int yPos = 6;
        int barWidth = PLAYER_INFO_WIDTH - X_PADDING;
        int xPos = 6;
        //Drawing the HP bar
        UIVisuals.drawBar(g, Color.RED, xPos, yPos, barWidth, Y_PADDING, player.getHP() / (double)player.getMaxHP(), "HP: " + player.getHP() + "/" + player.getMaxHP(), UIVisuals.STATS_FONT);
        yPos += Y_PADDING_INCREASE;

        //Drawing the AP bar
        UIVisuals.drawBar(g, Color.YELLOW, xPos, yPos, barWidth, Y_PADDING, player.getAP() / (double)player.getMaxAP(), "AP: " + player.getAP() + "/" + player.getMaxAP(), UIVisuals.STATS_FONT);
        yPos += Y_PADDING_INCREASE;

        //Drawing the XP bar
        UIVisuals.drawBar(g, PURPLE, xPos, yPos, barWidth, Y_PADDING, player.getXp() / (double)player.getXpToLevelUp(), "XP: " + player.getXp() + "/" + player.getXpToLevelUp(), UIVisuals.STATS_FONT);
        yPos += Y_PADDING_INCREASE + 7;

        String state;
        if(player.getAttackState()){
            state = "Attacking";
        }
        else {state= "Moving";}
        g.setFont(UIVisuals.STATS_FONT);
        g.drawString("State: " + state, xPos, yPos);
        yPos += Y_PADDING_INCREASE;

        g.setFont(STATS_HEADER_FONT);
        g.drawString("Stats", xPos, yPos);

        if (statsFontMetrics == null){statsFontMetrics = g.getFontMetrics(UIVisuals.STATS_FONT);}
        g.setFont(UIVisuals.STATS_FONT);
        String text = "Available Points: " + player.getAttributePoints();
        g.drawString(text, PLAYER_INFO_WIDTH - statsFontMetrics.stringWidth(text) - 5, yPos);

    }

    @Override public void mapChanged(final int i) {
        if (i > -1){
            world.getMapArea(i).addMapListener(this);
        }
        for (StatsComponent stat: statsComps) {
            if (player.getAttributePoints() > 0) {
                stat.toggleButtonVisibility(true);
            } else stat.toggleButtonVisibility(false);
        }
        repaint();
    }

    @Override public Dimension getPreferredSize() {
	    return new Dimension(PLAYER_INFO_WIDTH, 300);
    }
}
