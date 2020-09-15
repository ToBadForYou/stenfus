package logic;

/**
 * This class is used to create a potion which restores HP to the Player when used.
 */

public class HealthPotion extends AbstractItem
{
    private int hpRestore;

    public HealthPotion(final int id, final double hpRestore, final double stackSize) {
	super(id, (int)stackSize);
	this.hpRestore = (int)hpRestore;
    }

    @Override public void onUse(Player player){
	player.restoreHP(hpRestore);
	stackSize -= 1;
    }
}
