package logging;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
	    e.printStackTrace();
	}
    }

    public void log(final Level warning, final String logMessage) {
	LOGGER.log(warning, logMessage);
    }
}
