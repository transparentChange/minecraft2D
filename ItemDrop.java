/*
 * [ItemDrop.java]
 * This file contains the ItemDrop class.
 * Author: Andy Wang
 * Date: 14 June 2020
 */

package version2;

/**
 * The ItemDrop class represents an item on the ground, ready to be picked up by the player.
 * @author Andy Wang
 * @since 14 June 2020
 */
class ItemDrop extends Entity {
    private Item item;
    
    /**
     * This constructor initializes an item drop, given a certain item.
     * @param item The item to give.
     * @param x x position
     * @param y y position
     */
    public ItemDrop(Item item, float x, float y) {
        super(x, y);
        this.sprite = item.getSprite();
    }
    
    //Getter for the item
    public Item getItem() {
        return item;
    }
    
    @Override public void destroy() {
        //REMOVE THIS ITEM FROM THE WORLD AND PUT IT IN THE PLAYER'S INVENTORY
    }
}