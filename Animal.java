package version2;

import java.awt.Dimension;

/*
 * [Animal.java]
 * This file contains the animal class.
 * Author: Andy Wang
 * Date: 12 June 2020
 */

/**
 * The Animal class represents an animal, or living thing in the world.
 * @author Andy Wang
 * @since 12 June 2020
 */
abstract class Animal extends Entity implements Breakable {
    protected int hp; //The amount of health the animal has
    protected float dx, dy; //Velocities of the animal
    protected int move, left, right;
    protected boolean onGround, jumping;
    protected boolean active = true; //Whether or not to process this animal
    protected Map map;
    protected Dimension mapDimension;
    protected BlockIterator blockIterator;
    
    /**
     * This constructor initializes an Animal at the specified coordinates in the world's space.
     * @param x The x position.
     * @param y The y position.
     * @param map The map that the animal is in.
     */
    public Animal(float x, float y, Map map) {
        super(x, y);
        this.map = map;
        this.mapDimension = map.getMapDimension();
        this.blockIterator = map.getBlockIterator();
            
        //SUBCLASSES WILL INCLUDE SPRITES HERE 
        //SUBCLASSES WILL BE MULTITHREADED TO RUN THE STEP() METHOD IF ACTIVE
    }
    
    /**
     * The getHP method returns how much health the animal has.
     * @return How many health points an animal has.
     */
    public int getHP() {
        return hp;
    }
    
    /**
     * The damage method does n damage to the animal.
     * @param n The amount of damage to deal.
     */
    @Override
    public void damage(int n) {
        hp -= n;
    }
    
    /**
     * The isActive method returns whether or not this animal is active, that is to say, whether or not this animal
     * is being processed.
     * @return If the animal is active or not.
     */
    public boolean isActive() {
        return this.active;
    }
    
    /**
     * The setActive method sets the active state of an animal.
     * @param b What to set active as.
     */
    public void setActive(boolean b) {
        this.active = b;
    }
    /**
     * The step method modifies this Animal.
     */
    abstract public void step();
    
    /*
     * handleXCollisions
     * This method handles x axis collisions by looping through all the blocks to see if the animal will collide with one
     * horizontally
     */
    protected boolean handleXCollisions() {
    	boolean collidingX = false;
		while (!collidingX && blockIterator.hasNext()) {
			Block b = blockIterator.next();
			// Check if the animal will collide with the block if it moves any further
			if (this.isColliding((int) (x + dx), (int) y, b)) {
				collidingX = true;

				// Move the animal to be right next to the block (pixel perfect, no intersection)
				while (!this.isColliding((int) (x + Math.signum(dx)), (int) y, b)) {
					x += Math.signum(dx);
				}
				dx = 0;
			}
		}
		blockIterator.reset();
		
		return collidingX;
    }
    
    protected boolean handleYCollisions() {
    	boolean collidingY = false;
    	while (!collidingY && blockIterator.hasNext()) {
			Block b = blockIterator.next();

			if (this.isColliding((int) x, (int) (y + dy), b)) {
				collidingY = true;

				while (!this.isColliding((int) x, (int) (y + Math.signum(dy)), b)) {
					y += Math.signum(dy);
				}
				dy = 0;
			}
		}
		blockIterator.reset();
		
		return collidingY;
    }
    
    protected void updateXPosition(int xMapDimension) {
    	if ((dx < 0) && (x < Math.abs(dx))) {
			x = 0;
		} else if ((dx > 0) && (x > xMapDimension - dx - sprite.getWidth())) {
			x = xMapDimension - sprite.getWidth();
		} else {
			x += dx;
		}
    }
    
    protected void updateYPosition(int yMapDimension) {
    	if ((dy < 0) && (y < Math.abs(dy))) {
			y = 0;
		} else if ((dy > 0) && (y > yMapDimension - dy - sprite.getHeight())) {
			y = yMapDimension - sprite.getHeight();
		} else {
			y += dy;
		}
    }
    
    protected boolean isOnGround() {
        while (blockIterator.hasNext()) {
        	if (this.isColliding((int) x, (int) y + 1, blockIterator.next())) {
        		blockIterator.reset();
        		return true;
        	}
        }
        blockIterator.reset();
        return false;
    }
}