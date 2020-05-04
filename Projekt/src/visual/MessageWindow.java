package visual;

import javax.swing.*;

/**
 * A class that contains a static function to display a message window
 * Mostly used for error handling messages
 */

public class MessageWindow
{
    public static void showMessage(String infoMessage, String titleBar)
    {
	JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}
