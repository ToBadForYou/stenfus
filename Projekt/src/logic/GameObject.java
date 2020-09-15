package logic;

import resources.ObjectDataHandler;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * This is an abstract class to which is used to view all the objects in the game visually.
 */

public abstract class GameObject implements Serializable
{
    protected int id;
    protected String type;
    protected transient BufferedImage image = null;
    /**
     * Global cache for objects data
     */
    public transient static final ObjectDataHandler OBJECT_DATA_HANDLER = new ObjectDataHandler();

    protected GameObject(final int id, String type) {
	this.id = id;
	this.type = type;
	setImage(id, type);
    }

    public int getID(){
        return id;
    }

    public boolean isType(String type){ return type.equals(this.type); }

    public String getType() { return type; }

    public void setImage(final int id, final String type){
	image = OBJECT_DATA_HANDLER.getImage(id, type);
    }

    public BufferedImage getImage(){ return image; }
}
