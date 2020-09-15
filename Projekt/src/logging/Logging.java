package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This is a class that handles the logs of the game
 */

public class Logging
{
    private static final Logger LOGGER = Logger.getLogger(Logging.class.getName());

    public Logging(String name) {
	try {
	    final FileHandler fh = new FileHandler(name + ".txt", true);
	    fh.setFormatter(new SimpleFormatter());
	    LOGGER.addHandler(fh);
	    LOGGER.setLevel(Level.WARNING);
	} catch (SecurityException | IOException e) {
	    //We can't log this error, since the log didn't initalize properly
	    e.printStackTrace();
	}
    }

    public void log(final Level warning, final String logMessage) {
	LOGGER.log(warning, logMessage);
    }
}
