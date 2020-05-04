package logic;

import resources.ObjectDataHandler;

import java.awt.*;

/**
 * This interface represents all the different kinds of Battlecharacters in the game and contains the following methods
 */

public interface BattleCharacter
{
    int getAttribute(String name);

    void takeDamage(int damage);

    int getTeamID();

    Point getPos();

    void attack(BattleCharacter defender);

    void move(Point p);

    int getAP();

    void reduceAP(int i);

    Point directionToPoint(Direction.Dir direction);

    void restoreAP();

    int getHP();

    Image getImage();

    Color getStatusColor();

    String getName();

    String getType();

    int getID();

    int getLevel();

    int getMaxHP();

    ObjectDataHandler getObjectData();

    boolean isType(String type);
}
