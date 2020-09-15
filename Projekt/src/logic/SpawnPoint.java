package logic;

import java.awt.*;

/**
 * This class is used to represent a Point with a specific ID.
 * This is used in MapArea to spawn Enemies correctly.
 */

public class SpawnPoint extends Point implements GamePoint
{
    private int teamID;

    public SpawnPoint(final int x, final int y, final int teamID) {
        super(x,y);
	this.teamID = teamID;
    }

    public int getID() {
        int id = teamID;
        return id;
    }

    public String toString(){ return "x: "+ x + " y: "+ y + " id: " + teamID + "|||"; }
}
