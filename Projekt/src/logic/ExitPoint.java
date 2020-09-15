package logic;

import java.awt.*;

/**
 * This class is used to connect a Point in a MapArea to another MapArea, which enables to travel between them.
 */

public class ExitPoint extends Point implements GamePoint
{
    private int mapAreaID;

    public ExitPoint() {}

    public void setMapAreaID(final int mapAreaID) {
	this.mapAreaID = mapAreaID;
    }

    public int getID() {
        int id = mapAreaID;
	return id;
    }
}
