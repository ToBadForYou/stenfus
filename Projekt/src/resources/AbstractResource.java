package resources;

import logging.Logging;
import visual.MessageWindow;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.logging.Level;

/**
 * Abstract class for resources, used for loading resources
 */
public abstract class AbstractResource
{
    protected static final Logging LOGGER = new Logging("resources");

    protected AbstractResource() {

    }

    protected void loadAllResources(String directory){
        if (directory != null) {
            try {
                String fs = File.separator;
                URI folderURI = ClassLoader.getSystemResource("." + fs + directory + fs).toURI();
                try (FileSystem fileSystem = (folderURI.getScheme().equals("jar") ? FileSystems.newFileSystem(folderURI, Collections.emptyMap()) : null)) {
                    Files.walkFileTree(Paths.get(folderURI), new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttributes) {
                            loadResource(filePath.getFileName().toString());
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            } catch (URISyntaxException | IOException e) {
                String logMessage = "Failed to load resource: " + e.getMessage();
                LOGGER.log(Level.WARNING, logMessage);
                MessageWindow.showMessage(logMessage, "Loading error, directory: " + directory);
            }
        }
    }

    protected URL getURL(String directory, String fileName){
        String fs = File.separator;
        return ClassLoader.getSystemResource("." + fs + directory + fs + fileName);
    }

    protected abstract void loadResource(String fileName);
}
