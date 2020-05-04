package logic;

import resources.ObjectDataHandler;
import com.google.gson.internal.LinkedTreeMap;

import java.util.zip.DataFormatException;

/**
 * This class is used to create new Items from a given ID
 */

public class ItemFactory
{
    private final static ObjectDataHandler DATA_HANDLER = new ObjectDataHandler();

    public static Item createItem(int id) throws DataFormatException {
        Object[] data = DATA_HANDLER.getData(id, "items");
        switch((String)data[0]) {
	    case "hp_potion":
		return new HealthPotion(id, (Double) data[2], (Double) data[3]);
	    case "armor":
		return new Armor(id, Armor.Type.valueOf((String) data[2]), (LinkedTreeMap<String, Double>) data[3]);
	    default:
		throw new DataFormatException(data.toString());
	}
    }
}
