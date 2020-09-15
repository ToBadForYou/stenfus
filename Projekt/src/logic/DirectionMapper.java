package logic;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * This class is used for its enumerates for 4 differents.
 * It also assists AbstractBattleCharacter with translating a Point with a given direction.
 */

public class DirectionMapper
{
    public enum Direction
    {
        NORTH, SOUTH, EAST, WEST
    }
    private static final Map<Direction, Point> DIRECTION;

    static {
        DIRECTION = new EnumMap<>(Direction.class);
        DIRECTION.put(Direction.NORTH, new Point(0, -1));
        DIRECTION.put(Direction.SOUTH, new Point(0, 1));
        DIRECTION.put(Direction.EAST, new Point(1, 0));
        DIRECTION.put(Direction.WEST, new Point(-1, 0));
    }

    public static Point getDeltaCoord(Direction direction){
        return DIRECTION.get(direction);
    }

    public static Point translatePoint (Direction direction, Point p){
        int dx = (int) getDeltaCoord(direction).getX();
        int dy = (int) getDeltaCoord(direction).getY();
        p.translate(dx, dy);
        return p;
    }

}
