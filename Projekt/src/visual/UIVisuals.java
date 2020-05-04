package visual;

import java.awt.*;

/**
 * This class includes all fonts used in other components and also a static function
 * that can create centered strings (useful for health bars and text that is not constant)
 */

public class UIVisuals
{
    public static final Font HEALTHBARFONT = new Font("Arial", Font.PLAIN, 10);
    public static final Font NAMEFONT = new Font("Arial", Font.PLAIN, 10);
    public static final Font MAPNAME = new Font("Arial", Font.PLAIN, 20);
    public static final Font LEVELFONT = new Font("Arial", Font.PLAIN, 8);
    public static final Font ATTRIBUTEUPGRADE = new Font("Arial", Font.PLAIN, 10);
    public static final Font WORLDEDITORFONT = new Font("Arial", Font.PLAIN, 15);
    public static final Font WORLDEDITORTITLEFONT = new Font("Arial", Font.BOLD, 18);
    public static final Font STATSHEADERFONT = new Font("Arial", Font.PLAIN, 20);
    public static final Font STATSFONT = new Font("Arial", Font.BOLD, 14);
    public static final Font QUESTHEAD = new Font("Arial", Font.BOLD, 20);
    public static final Font QUESTSYMBOL = new Font("Arial", Font.BOLD, 23);
    public static final Font STACKSIZE = new Font("Arial", Font.BOLD, 12);
    public static final Font QUEST = new Font("Arial", Font.PLAIN, 15);
    public static final Color BEIGE = new Color(211,188,141);
    public static final Color PURPLE = new Color(138,43,226);

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
    public static void drawCenteredString(Graphics g, String text, Color textColor, Boolean centerX, Boolean filled, Boolean drawRectangle, Rectangle rect, Color rectColor, Font font) {
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
}
