package resources;


import visual.MessageWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Text resource class, takes care of loading in and returning the first line as a
 * string in the .txt file requested
 */
public class TextResource extends AbstractResource
{

    public TextResource() {
        super();
    }

    @Override protected void loadResource(final String fileName) {

    }

    public String getJSONData(String type, String fileName) {
        return loadData(type, fileName);
    }

    public void saveJSONData(String type, String fileName, String data){
        try {
            String tempFilePath = System.getProperty("user.dir") + "/Projekt/resources/data/" + type + "/" + fileName + "_temp.txt";

            File tempData = new File(tempFilePath);
            if (tempData.createNewFile()) {
                FileWriter writer = new FileWriter(tempData);
                try {
                    writer.write(data);
                    writer.close();
                } catch (IOException e){
                    LOGGER.log(Level.WARNING, "Failed to write to file: " + e.getMessage());
                    MessageWindow.showMessage("Failed to write to file: " + e.getMessage(), "Writing error, Type: " + type + ", file: " + fileName);
                }

                String filePath = System.getProperty("user.dir") + "/Projekt/resources/data/" + type + "/" + fileName + ".txt";
                File newName = new File(filePath);
                if (!tempData.renameTo(newName)) {
                    LOGGER.log(Level.WARNING, "Failed to rename temp file during saving process for: " + newName);
                    MessageWindow.showMessage("Failed to rename temp file during saving process", "Saving error, Type: " + type + ", file: " + fileName);
                }
            } else {
                LOGGER.log(Level.WARNING, "Failed to create temp file: " + tempFilePath);
                MessageWindow.showMessage("Failed to create temp file", "Saving error, Type: " + type + ", file: " + fileName);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to find file path for file: " + fileName + ", error: " + e.getMessage());
            MessageWindow.showMessage("Failed to find file path: " + e.getMessage(), "Saving error, Type: " + type + ", file: " + fileName);
        }
    }

    private String loadData(final String type, final String fileName) {
        try {
            URL fileURL = getURL("data", type + "/" + fileName + ".txt");
            if (fileURL == null) {
                MessageWindow.showMessage("Failed to find file path", "Loading error, Type: " + type + ", file: " + fileName);
                return null;
            }

            URI filePath = fileURL.toURI();
            File txt = new File(filePath);
            if (txt.exists()){
                File myObj = new File(filePath);
                Scanner myReader = new Scanner(myObj);
                if (myReader.hasNextLine()) {
                    return myReader.nextLine();
                }
                myReader.close();
            }
        } catch (URISyntaxException | FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Failed to load file: " + e.getMessage());
            MessageWindow.showMessage("Failed to load file: " + e.getMessage(), "Loading error, Type: " + type + ", file: " + fileName);
        }
        return null;
    }
}
