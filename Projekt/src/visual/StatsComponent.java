package visual;

import logic.MapListener;
import logic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * This class handles the visual representation of the players stats
 */

public class StatsComponent extends JPanel implements MapListener
{

    private String attribute;
    private BufferedImage icon;
    private int value;
    private JButton upgradeButton;
    private Player player;
    private static final Color LIGHT_GREEN = new Color(57, 200, 20);
    private static final Font ATTRIBUTE_UPGRADE = new Font("Arial", Font.PLAIN, 10);
    private static final Dimension BUTTON_DIMENSION = new Dimension((int)PlayerInformation.ICON_SIZE + 25, (int)PlayerInformation.ICON_SIZE);

    public StatsComponent(Player player, String attribute, int statValue, BufferedImage icon) {
        this.attribute = attribute;
        this.icon = icon;
        this.player = player;
        value = statValue;

        setLayout(new BorderLayout());

        upgradeButton = new JButton("+");
        upgradeButton.setFocusable(false);
        upgradeButton.setPreferredSize(BUTTON_DIMENSION);
        upgradeButton.setFont(ATTRIBUTE_UPGRADE);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                player.upgradeAttribute(attribute);
                value = player.getAttribute(attribute);
                repaint();
            }
        };
        upgradeButton.addActionListener(actionListener);

        add(upgradeButton, BorderLayout.EAST);
    }

    public void toggleButtonVisibility(boolean toggle){
        upgradeButton.setVisible(toggle);
    }

    @Override public Dimension getMaximumSize() {
        return new Dimension(PlayerInformation.PLAYER_INFO_WIDTH, (int)PlayerInformation.ICON_SIZE);
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(PlayerInformation.PLAYER_INFO_WIDTH, (int)PlayerInformation.ICON_SIZE);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        final int statSize = (int) PlayerInformation.ICON_SIZE;
        g.drawImage(icon, 5, 0, this);
        UIVisuals.drawCenteredString(g, attribute + ":", Color.BLACK, false, false, false,
                                     new Rectangle(statSize + 7, 0, statSize, statSize), Color.BLACK, UIVisuals.STATS_FONT);

        final int statValueXPos = 150 + statSize;
        UIVisuals.drawCenteredString(g, String.valueOf(value), Color.BLACK, false, false, false,
                                     new Rectangle(statValueXPos, 0, statSize, statSize), Color.BLACK, UIVisuals.STATS_FONT);

        if(player.getAttributeIncrement(attribute) > 0){
            final int buttonXPos = statValueXPos + 20;
            UIVisuals.drawCenteredString(g, "(+" + player.getAttributeIncrement(attribute) + ")", LIGHT_GREEN, false, false,
                                         false, new Rectangle(buttonXPos, 0, statSize, statSize), Color.GREEN, UIVisuals.STATS_FONT);
        }

    }

    @Override public void mapChanged(final int i) {
        value = player.getAttribute(attribute);
        repaint();
    }
}
