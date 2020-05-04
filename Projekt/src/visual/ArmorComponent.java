package visual;

import logic.AbstractItem;
import logic.Armor;
import logic.MapListener;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the visual representation of the currently equipped armor.
 */
public class ArmorComponent extends JComponent implements MapListener
{
    private HashMap<Armor.Type, Armor> equipped;
    private EnumMap<Armor.Type, Point> coordMapper;

    public ArmorComponent(final HashMap<Armor.Type, Armor> equipped) {
        this.equipped = equipped;
        coordMapper = new EnumMap<>(Armor.Type.class);
        int x = (PlayerInformation.STATSWIDTH - AbstractItem.SIZE) / 2;
        coordMapper.put(Armor.Type.HEAD, new Point(x, 0));
        coordMapper.put(Armor.Type.CHEST, new Point(x , (int)(AbstractItem.SIZE * 1.5)));
        coordMapper.put(Armor.Type.LEGS, new Point(x, AbstractItem.SIZE * 3));
        coordMapper.put(Armor.Type.WEAPON, new Point(x + (int)(AbstractItem.SIZE * 1.5), (int)(AbstractItem.SIZE * 1.5)));
    }

    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        for (final Map.Entry<Armor.Type, Point> entry : coordMapper.entrySet()) {
            Armor.Type type = entry.getKey();
            int x = (int) entry.getValue().getX();
            int y = (int) entry.getValue().getY();
            g.setColor(Color.BLACK);
            g.fillRect(x, y, AbstractItem.SIZE, AbstractItem.SIZE);
            g.setColor(Color.WHITE);
            g.fillRect( x + 2, y + 2, AbstractItem.SIZE - 4, AbstractItem.SIZE - 4);
            if(equipped.get(type) != null){
                g.drawImage(equipped.get(type).getImage(), x, y, this);
            }
        }


    }

    @Override public void mapChanged(final int i) {
	repaint();
    }

    @Override public Dimension getPreferredSize(){
        return new Dimension(PlayerInformation.STATSWIDTH, AbstractItem.SIZE * 2);
    }
}
