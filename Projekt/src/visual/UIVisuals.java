package visual;

import java.awt.*;

/**
 * This class includes all fonts used in other components and also a static function
 * that can create centered strings (useful for health bars and text that is not constant)
 */

public class UIVisuals
{

    /**
    Public static fields for fonts and colors, having them all stored in one class makes it easier to adjust them as they are
    just for design purposes
     */
    public static final Font NAME_FONT = new Font("Arial", Font.PLAIN, 10);
    public static final Font STATS_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font QUEST_HEAD = new Font("Arial", Font.BOLD, 20);

    private static final int BAR_PADDING = 1;
    /**
     * Draw a String centered in the middle of a Rectangle.
     *
     * @param g The Graphics instance.
     * @param text The String to draw.
     * @param textColor The color for the text
     * @param centerX Should it center the x value?
     * @param filled should the Rectangle be filled?
     * @param drawRectangle should the rectangle be drawn?
     * @param rect The Rectangle to center the text in.
     * @param rectColor The color for the rectangle
     *
     * Code modified to satisfy our needs
     * Source: https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
     */
    public static void drawCenteredString(Graphics g, String text, Color textColor, boolean centerX, boolean filled, boolean drawRectangle, Rectangle rect, Color rectColor, Font font) {
	g.setColor(rectColor);
	if(drawRectangle) {
	    if (filled) {
		g.fillRect((int) rect.getX(), (int) rect.getY(), rect.width, rect.height);
	    } else {g.drawRect((int) rect.getX(), (int) rect.getY(), rect.width, rect.height);}
	}

	FontMetrics metrics = g.getFontMetrics(font);
	int x;
	if (centerX) {
	    x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
	} else {x = rect.x;}

	int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	g.setFont(font);
	g.setColor(textColor);
	g.drawString(text, x, y);
    }

    public static void drawBar(Graphics g, Color filledColor, int rectX, int rectY, int rectSizeX, int rectSizeY, double percentage, String barText, Font stringFont){
	g.setColor(filledColor);
	g.fillRect(rectX+BAR_PADDING, rectY+BAR_PADDING, (int)((rectSizeX-BAR_PADDING) * percentage), rectSizeY-BAR_PADDING);
	drawCenteredString(g, barText, Color.BLACK, true,
				     false, true, new Rectangle(rectX, rectY, rectSizeX, rectSizeY), Color.BLACK, stringFont);
    }
}
