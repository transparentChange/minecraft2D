package version2;

import java.awt.image.BufferedImage;

/*
 * [Item.java]
 * This file contains the Item class.
 * Author: Andy Wang
 * Date: 14 June 2020
 */

/**
 * The Item class represents a useable item in the game.
 * @author Andy Wang
 * @since 14 June 2020
 */
abstract class Item {
    protected int copies; //How many of this item is stacked
    protected Sprite sprite;
    protected BufferedImage iconImage;
    
    /**
     * This constructor makes an Item, or a stack of items.
     * @param n The number of this item in the stack.
     */
    public Item(int n) {
        this.copies = n;
    }
    
    /**
     * The stack method stacks more of the same item.
     * @param n How many of this item to stack. n < 0 if removing items.
     */
    public void stack(int n) {
        //Check in case n is negative
        if (copies + n > -1) {
            copies += n;
        }
    }
    
    //Getter for the sprite
    public Sprite getSprite() {
        return this.sprite;
    }
    
    public int getCopies() {
    	return copies;
    }
    
    /**
     * The use method is called whenever the player left clicks while holding an item.
     */
    abstract public void use();
    
    /*
     * The getIconImage is called to get the icon image from the inventory
     */
    public BufferedImage getIconImage() {
    	return iconImage;
    }
}