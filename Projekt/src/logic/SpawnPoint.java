package logic;

import java.awt.*;

/**
 * This class is used to represent a Point with a specific ID.
 * This is used in MapArea to spawn Enemies correctly.
 */

public class SpawnPoint extends Point
{
    private int teamID;

    public SpawnPoint(final int x, final int y, final int teamID) {
	super(x, y);
	this.teamID = teamID;
    }

    public int getTeamID(){ return teamID; }

    public String toString(){ return "x: "+ x + " y: "+ y + " id: " + teamID + "|||"; }

}
