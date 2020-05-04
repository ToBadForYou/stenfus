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
    public static final int STATSWIDTH = 285;
    public static final double ICONSIZE = 16.0;
    private Player player;
    private World world;
    private static List<StatsComponent> statsComps = new ArrayList<>();

    public PlayerInformation(MapArea map, World world) {
        player = map.getPlayer();
        this.world = world;
        BoxLayout boxlayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(boxlayout);

        add(Box.createRigidArea(new Dimension(0, 135)));

        for (Map.Entry<String,Integer> attribute: player.getAttributes().entrySet()) {
            BufferedImage icon = ObjectDataHandler.resources.getImage(attribute.getKey() + ".png", new Double[] {ICONSIZE, ICONSIZE});
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

        int yPos = 0;
        g.setColor(Color.RED);
        g.fillRect(6, 6, (STATSWIDTH-11)*player.getHP() / player.getMaxHP(), 24);
        UIVisuals.drawCenteredString(g, "HP: " + player.getHP() + "/" + player.getMaxHP(), Color.BLACK, true, false, true, new Rectangle(5,5,STATSWIDTH-10,25), Color.BLACK, UIVisuals.STATSFONT);
        yPos += 30;

        g.setColor(Color.YELLOW);
        g.fillRect(6, 6+yPos, (STATSWIDTH-11)*player.getAP() / player.getMaxAP(), 24);
        UIVisuals.drawCenteredString(g, "AP: " + player.getAP() + "/" + player.getMaxAP(), Color.BLACK, true, false, true, new Rectangle(5,5+30,STATSWIDTH-10,25), Color.BLACK, UIVisuals.STATSFONT);
        yPos += 30;

        g.setColor(UIVisuals.PURPLE);
        g.fillRect(6, 6+yPos, (STATSWIDTH-11)*player.getXp() / player.getXpToLevelUp(), 24);
        UIVisuals.drawCenteredString(g, "XP " + player.getXp() + "/" + player.getXpToLevelUp(), Color.BLACK, true, false, true, new Rectangle(5,5+60,STATSWIDTH-10,25), Color.BLACK, UIVisuals.STATSFONT);
        yPos += 40;

        String state;
        if(player.getAttackState()){
            state = "Attacking";
        }
        else {state= "Moving";}
        g.setFont(UIVisuals.STATSFONT);
        g.drawString("State: " + state, 5, 6+yPos);
        yPos += 20;

        g.setFont(UIVisuals.STATSHEADERFONT);
        g.drawString("Stats", 5, 6+yPos);

        FontMetrics metrics = g.getFontMetrics(UIVisuals.STATSFONT);
        g.setFont(UIVisuals.STATSFONT);
        String text = "Available Points: " + player.getAttributePoints();
        g.drawString(text, STATSWIDTH - metrics.stringWidth(text) - 5, 6+yPos);

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
	    return new Dimension(STATSWIDTH, 300);
    }
}
