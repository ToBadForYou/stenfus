package logic;

import java.awt.*;

/**
 * This interface represents all the different kinds of NPC's in the game and contains the following methods
 */

public interface InteractableCharacter
{
    void interact(Player player);

    int getID();

    Object getPos();

    int getMapID();

    Image getImage();

    String getName();
}
