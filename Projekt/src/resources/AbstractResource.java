package resources;

import logging.Logging;
import visual.MessageWindow;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;

/**
 * Abstract class for resources
 */
public abstract class AbstractResource
{
    protected static final Logging LOGGER = new Logging("resources");

    protected AbstractResource() {

    }

    protected void loadAllResources(String directory){
        if (directory != null) {
            try {
                File folder = new File(ClassLoader.getSystemResource("./" + directory + "/").toURI());
                File[] listOfFiles = folder.listFiles();

                for (final File listOfFile : listOfFiles) {
                    if (listOfFile.isFile()) {
                        loadResource(listOfFile.getName());
                    }
                }
            } catch (URISyntaxException e) {
                LOGGER.log(Level.WARNING, "Failed to load resource: " + e.getMessage());
                MessageWindow.showMessage("Failed to load resource: " + e.getMessage(), "Loading error, directory: " + directory);
            }
        }
    }


    protected URL getURL(String directory, String fileName){
        return ClassLoader.getSystemResource("./" + directory + "/" + fileName);
    }

    protected abstract void loadResource(String fileName);
}
