package logic;

import logging.Logging;
import resources.TextResource;
import visual.MessageWindow;
import com.google.gson.Gson;

import java.awt.*;
import java.util.logging.Level;
import java.util.zip.DataFormatException;

/**
 * This class is suposed to be edited and run to generate a new Player to our txt file.
 */

public class GeneratePlayer {
    protected static final Logging LOGGER = new Logging("generateplayer");

    public static void main(String[] args) {
        Player player = new Player("Joel", 10, 10, 10, 10, 10, 10, 10, 10,  new Point(1,1), 0, new MapArea());
        try {
            player.addItemToInventory(ItemFactory.createItem(1));
            player.addItemToInventory(ItemFactory.createItem(2));
        } catch (DataFormatException e){
            String logMessage = "Failed to generate player: " + e.getMessage();
            LOGGER.log(Level.WARNING, logMessage);
            MessageWindow.showMessage(logMessage, "Item creation error");
        }
        player.saveInventory();
        TextResource dataSaver = new TextResource();
        Gson gson = new Gson();
        String dataJSON = gson.toJson(player);
        dataSaver.saveJSONData("playerdata", "1", dataJSON);
    }
}
