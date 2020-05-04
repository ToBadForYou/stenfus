package logic;

import io.gsonfire.annotations.PostDeserialize;

/**
 * This is an abstract class for the different Items in the game
 */

public abstract class AbstractItem extends GameObject implements Item
{
    protected int stackLimit, stackSize;
    public static final int SIZE = 42;

    protected AbstractItem(final int id, final int stackLimit) {
        super(id, "items");
        this.stackLimit = stackLimit;
        stackSize = 1;
    }

    @PostDeserialize
    public void postDeserializeLogic() {
        setImage(id, "items");
    }

    public int getStackLimit() {
        return stackLimit;
    }

    public int getStackSize() {
        return stackSize;
    }

    public void addToStack(){
        stackSize += 1;
    }

    public void setStackSize(int i){ stackSize = i; }

    public void decrementStackSize(){ stackSize -= 1; }
}
