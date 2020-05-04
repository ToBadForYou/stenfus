package logic;

import java.awt.*;

/**
 * This interface represents all the different kinds of NPC's in the game and contains the following methods
 */

public interface NPC
{
    void interaction(Player player);

    int getId();

    Object getPos();

    int getmapID();

    Image getImage();

    String getName();
}
