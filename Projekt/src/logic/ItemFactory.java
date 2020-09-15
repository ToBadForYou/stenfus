package logic;

import com.google.gson.internal.LinkedTreeMap;

import java.util.zip.DataFormatException;

/**
 * This class is used to create new Items from a given ID
 */

public class ItemFactory
{

    public static Item createItem(int id) throws DataFormatException {
        Object[] data = GameObject.OBJECT_DATA_HANDLER.getData(id, "items");
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
