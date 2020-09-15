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
        String detailedLog = "Type: " + type + ", file: " + fileName;
        String savingError = "Saving error: ";
        try {
            String fs = File.separator;
            String tempFilePath = System.getProperty("user.dir") + fs +"Projekt" + fs + "resources" + fs + "data" + fs + type + fs + fileName + "_temp.txt";

            File tempData = new File(tempFilePath);
            if (tempData.createNewFile()) {
                try(FileWriter writer = new FileWriter(tempData)) {
                    writer.write(data);
                } catch (IOException e){
                    String logMessage = "Failed to write to file: " + e.getMessage();
                    LOGGER.log(Level.WARNING, logMessage);
                    MessageWindow.showMessage(logMessage, "Writing error, " + detailedLog);
                }

                String filePath = System.getProperty("user.dir") + fs + "Projekt" + fs + "resources" + fs + "data" + fs + type + fs + fileName + ".txt";
                File newName = new File(filePath);
                if (!tempData.renameTo(newName)) {
                    String logMessage = "Failed to rename temp file during saving process for: " + newName;
                    LOGGER.log(Level.WARNING, logMessage);
                    MessageWindow.showMessage(logMessage, savingError + detailedLog);
                }
            } else {
                String logMessage = "Failed to create temp file: " + tempFilePath;
                LOGGER.log(Level.WARNING, logMessage);
                MessageWindow.showMessage(logMessage, savingError + detailedLog);
            }
        } catch (IOException e) {
            String logMessage = "Failed to find file path for file: " + fileName + ", error: " + e.getMessage();
            LOGGER.log(Level.WARNING, logMessage);
            MessageWindow.showMessage(logMessage, savingError + detailedLog);
        }
    }

    private String loadData(final String type, final String fileName) {
        String detailedLog = "Type: " + type + ", file: " + fileName;
        try {
            String fs = File.separator;
            URL fileURL = getURL("data", type + fs + fileName + ".txt");
            if (fileURL == null) {
                MessageWindow.showMessage("Failed to find file path", "Loading error, " + detailedLog);
                return null;
            }

            URI filePath = fileURL.toURI();
            File txt = new File(filePath);
            if (txt.exists()){
                File myObj = new File(filePath);
                try(Scanner myReader = new Scanner(myObj)) {
                    if (myReader.hasNextLine()) {
                        return myReader.nextLine();
                    }
                }
            }
        } catch (URISyntaxException | FileNotFoundException e) {
            String logMessage = "Failed to load file: " + e.getMessage();
            LOGGER.log(Level.WARNING, logMessage);
            MessageWindow.showMessage(logMessage, "Loading error, " + detailedLog);
        }
        return null;
    }
}
