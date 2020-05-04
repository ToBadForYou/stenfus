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

    public StatsComponent(Player player, String attribute, int statVal, BufferedImage icon) {
        this.attribute = attribute;
        this.icon = icon;
        this.player = player;
        value = statVal;

        setLayout(new BorderLayout());

        upgradeButton = new JButton("+");
        upgradeButton.setFocusable(false);
        upgradeButton.setPreferredSize(new Dimension((int)PlayerInformation.ICONSIZE + 25,(int)PlayerInformation.ICONSIZE));
        upgradeButton.setFont(UIVisuals.ATTRIBUTEUPGRADE);

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
        return new Dimension(PlayerInformation.STATSWIDTH,(int)PlayerInformation.ICONSIZE);
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(PlayerInformation.STATSWIDTH, (int)PlayerInformation.ICONSIZE);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(icon, 5, 0, this);
        UIVisuals.drawCenteredString(g, attribute + ":", Color.BLACK, false, false, false, new Rectangle((int)PlayerInformation.ICONSIZE + 7, 0, (int)PlayerInformation.ICONSIZE, (int)PlayerInformation.ICONSIZE), Color.BLACK, UIVisuals.STATSFONT);
        UIVisuals.drawCenteredString(g, String.valueOf(value), Color.BLACK, false, false, false, new Rectangle((int)PlayerInformation.ICONSIZE + 150, 0, (int)PlayerInformation.ICONSIZE, (int)PlayerInformation.ICONSIZE), Color.BLACK, UIVisuals.STATSFONT);
        if(player.getAttributeIncrement(attribute) > 0){
            UIVisuals.drawCenteredString(g, "(+" + player.getAttributeIncrement(attribute) + ")", new Color(57, 200, 20), false, false, false, new Rectangle((int)PlayerInformation.ICONSIZE + 170, 0, (int)PlayerInformation.ICONSIZE, (int)PlayerInformation.ICONSIZE), Color.GREEN, UIVisuals.STATSFONT);

        }

    }

    @Override public void mapChanged(final int i) {
        value = player.getAttribute(attribute);
        repaint();
    }
}
