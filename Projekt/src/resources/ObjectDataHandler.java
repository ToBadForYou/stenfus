package resources;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles the management of each different data handler, it makes sure the correct
 * object data is called upon and requests the data from this object data and returns it
 */

public class ObjectDataHandler
{
    /**
     * Cache for all loaded image resources
     */
    public static final ImageResource RESOURCES = new ImageResource();
    private Map<String, ObjectData> objectType;

    public ObjectDataHandler() {
	ObjectData tilesData = new ObjectData("tiles");
	ObjectData staticObjectData = new ObjectData("static");
	ObjectData enemiesData = new ObjectData("enemies");
	ObjectData playerData = new ObjectData("player");
	ObjectData npcData = new ObjectData("npc");
	ObjectData itemsData = new ObjectData("items");

	objectType = new HashMap<>();
	objectType.put("tiles", tilesData);
	objectType.put("static", staticObjectData);
	objectType.put("enemies", enemiesData);
	objectType.put("player", playerData);
	objectType.put("npc", npcData);
	objectType.put("items", itemsData);
    }

    public BufferedImage getImage(int id, String type){
        String fileName = objectType.get(type).getImagePath(id);
        double[] size = objectType.get(type).getImageSize(id);
        return RESOURCES.getImage(fileName, size);
    }

    public double[] getImageOffset(int id, String type){
	return objectType.get(type).getImageOffset(id);
    }

    public double[] getImageSize(int id, String type){
	return objectType.get(type).getImageSize(id);
    }

    public String getImageName(int id, String type){
        return objectType.get(type).getImagePath(id);
    }

    public Object[] getData(int id, String type){
	return objectType.get(type).getData(id);
    }

    public int getDataLength(String type){
        return objectType.get(type).size();
    }
}
