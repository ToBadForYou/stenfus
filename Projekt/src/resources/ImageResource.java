package resources;

import visual.MessageWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Class for loading in resources, loads in all images in the image folder
 * Can be called upon to return the image requested scaled to given size
 */
public class ImageResource extends AbstractResource
{
    private HashMap<String, BufferedImage> loadedResources;

    public ImageResource() {
        super();
        loadedResources = new HashMap<>();
        loadAllResources("images");
    }

    public BufferedImage getImage(String fileName, Double[] size) {
        return scale(loadedResources.get(fileName), (int)(size[0]*1), (int)(size[1]*1));
    }

    public static BufferedImage scale(BufferedImage imageToScale, int dWidth, int dHeight) {
        BufferedImage scaledImage = null;
        if (imageToScale != null) {
            scaledImage = new BufferedImage(dWidth, dHeight, imageToScale.getType());
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(imageToScale, 0, 0, dWidth, dHeight, null);
            graphics2D.dispose();
        }

        return scaledImage;
    }

    @Override public void loadResource(final String fileName) {
        try {
            BufferedImage image = ImageIO.read((getURL("images", fileName)));
            loadedResources.put(fileName, image);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load image: " + e.getMessage());
            MessageWindow.showMessage("Failed to load image:" + e.getMessage(), "Loading error, file: " + fileName);
        }
    }
}
