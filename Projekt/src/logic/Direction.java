package logic;

import java.awt.*;
import java.util.EnumMap;

/**
 * This class is used for its enumerates for 4 differents.
 * It also assists AbstractBattleCharacter with translating a Point with a given direction.
 */

public class Direction
{
    public enum Dir
    {
        NORTH, SOUTH, EAST, WEST
    }
    private EnumMap<Dir, Point> directionMapper;

    public Direction(){
        directionMapper = new EnumMap<>(Dir.class);
        directionMapper.put(Dir.NORTH, new Point(0, -1));
        directionMapper.put(Dir.SOUTH, new Point(0, 1));
        directionMapper.put(Dir.EAST, new Point(1, 0));
        directionMapper.put(Dir.WEST, new Point(-1, 0));
    }

    public Point getdeltaCoord(Dir direction){
        return directionMapper.get(direction);
    }

    public Point translatePoint (Dir direction, Point p){
        int dx = (int)getdeltaCoord(direction).getX();
        int dy = (int)getdeltaCoord(direction).getY();
        p.translate(dx, dy);
        return p;
    }

}
