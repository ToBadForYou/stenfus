package logic;

import resources.ObjectData;
import resources.SaveData;

/**
 * This class is suposed to be edited and run to generate a new Enemy to our txt file.
 */

public class GenerateEnemy
{
    public static void main(String[] args) {
        final int imageScale = 64;
        Object[] imageData = {"yellow_eyes_beige_dragon.png", 0, 0, imageScale, imageScale};
        Object[] data = {"Yellow Eyes Beige Dragon",5,5,5,5,5,5,5,5,5,5,5,5,5,5};

        ObjectData objects = new ObjectData("enemies");

        SaveData enemy = new SaveData(objects.size(), imageData, data);
        objects.addData(enemy);
        objects.saveData();
    }
}