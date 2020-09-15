package visual;

import logic.AbstractItem;
import logic.Item;
import logic.MapListener;

import javax.swing.*;
import java.awt.*;

/**
 * This class handles the visual representation of the players Items in the inventory
 */

public class ItemComponent extends JComponent implements MapListener
{

    private static final Font STACK_SIZE = new Font("Arial", Font.BOLD, 12);
    private Item[] inventory;

    public ItemComponent(final Item[] inventory) {
        this.inventory = inventory;
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.setFont(UIVisuals.QUEST_HEAD);
	final int y = 20;
	g.drawString("Inventory", 5, y);
	for (int x = 0; x < inventory.length ; x++) {
	    int xPadding = x * (AbstractItem.SIZE - 1);
	    g.setColor(Color.BLACK);
	    g.fillRect( 5 + xPadding, y + 2, AbstractItem.SIZE, AbstractItem.SIZE);
	    g.setColor(Color.WHITE);
	    g.fillRect( 7 + xPadding, y + 4, AbstractItem.SIZE - 4, AbstractItem.SIZE - 4);
	    if(inventory[x]!= null) {
		g.drawImage(inventory[x].getImage(), 5 + xPadding, y + 4, this);

		//Drawing Stacksize;
		g.setColor(Color.BLACK);
		if(inventory[x].getStackLimit() > 1){
		    g.setFont(STACK_SIZE);
		    g.drawString(Integer.toString(inventory[x].getStackSize()),AbstractItem.SIZE - 4 + xPadding, AbstractItem.SIZE + y - 3);
		}
	    }
	    //Drawing Keybind index
	    g.setFont(UIVisuals.NAME_FONT);
	    g.drawString(Integer.toString(x + 1),8 + xPadding, y + 12);
	}
    }

    @Override public void mapChanged(final int i) {
	repaint();
    }
}
