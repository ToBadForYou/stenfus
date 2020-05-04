package logic;

import java.awt.*;

/**
 * This interface represents all the different kinds of Items in the game and contains the following methods
 */

public interface Item
{
    void onUse(Player player);

    Image getImage();

    int getId();

    int getStackLimit();

    int getStackSize();

    boolean equals(Object obj);

    void addToStack();

    void setStackSize(int i);

    void decrementStackSize();
}
