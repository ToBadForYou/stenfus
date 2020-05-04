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

    private Item[] inventory;

    public ItemComponent(final Item[] inventory) {
        this.inventory = inventory;
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	g.setFont(UIVisuals.QUESTHEAD);
	g.drawString("Inventory", 5, 20);
	for (int i = 0; i < inventory.length ; i++) {
	    g.setColor(Color.BLACK);
	    g.fillRect( 5 + i * (AbstractItem.SIZE - 1), 22, AbstractItem.SIZE, AbstractItem.SIZE);
	    g.setColor(Color.WHITE);
	    g.fillRect( 7 + i * (AbstractItem.SIZE - 1), 24, AbstractItem.SIZE - 4, AbstractItem.SIZE - 4);
	    if(inventory[i]!= null) {
		g.drawImage(inventory[i].getImage(), 5 + i * (AbstractItem.SIZE - 1), 24, this);

		//Drawing Stacksize;
		g.setColor(Color.BLACK);
		if(inventory[i].getStackLimit() > 1){
		    g.setFont(UIVisuals.STACKSIZE);
		    g.drawString(Integer.toString(inventory[i].getStackSize()),AbstractItem.SIZE - 4 + i * (AbstractItem.SIZE - 1), AbstractItem.SIZE + 17);
		}

	    }

	    //Drawing Keybind index
	    g.setFont(UIVisuals.NAMEFONT);
	    g.drawString(Integer.toString(i + 1),8 + i * (AbstractItem.SIZE - 1), 32);
	}
    }

    @Override public void mapChanged(final int i) {
	repaint();
    }
}
