package logic;

import resources.ObjectData;
import resources.SaveData;

/**
 * This class is suposed to be edited and run to generate a new Item to our txt file.
 */

public class GenerateItem
{
    public static void main(String[] args) {
        final int imageScale = 64;
        final int hpRestore = 50;
        Object[] imageData = new Object[]{"hp_pot1.png", 0, 0, imageScale, imageScale};
        Object[] data = new Object[]{"hp_potion","Health Potion 1", hpRestore};

        ObjectData objects = new ObjectData("items");

        SaveData item = new SaveData(objects.size(), imageData, data);
        objects.addData(item);
        objects.saveData();
    }
}