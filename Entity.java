package version2;

/*
 * [Entity.java]
 * This file contains the entity class.
 * Author: Andy Wang
 * Date: 12 June 2020
 */

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * The Entity class represents some object in the world.
 * @author Andy Wang
 * @since 12 June 2020
 */
abstract class Entity {
    protected float x, y; //Position in the world, NOT on the canvas!
    protected Sprite sprite; //The entity's current sprite.
    protected boolean active; //Whether this Entity is on the screen or not.
    
    /**
     * This constructor initializes some Entity at the coordinates relative to the WORLD, not the canvas.
     * @param x The x position in the world.
     * @param y The y position in the world.
     */
    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        
        //THE SUBCLASSES WILL DECLARE THE DEFAULT SPRITE AND BOUNDING BOX
    }
    
    /**
     * The draw method is what's called inside the paintComponent of the canvas in order to draw it.
     * @param x The x position on the canvas to draw it. (top left corner)
     * @param y The y position on the canvas to draw it. (Top left corner)
     * @param offsetPoint How much to offset the entity by in order to draw it on the canvas.
     */
    public void draw(Graphics g, Point offsetPoint) {
        g.drawImage(sprite.currentFrame(), Math.round(x) - offsetPoint.x, Math.round(y) - offsetPoint.y, null);
    }
    
    /**
     * The changeSprite method changes the entity's current sprite to a specified one.
     * @param sprite The new sprite.
     */
    public void changeSprite(Sprite sprite) {
        boolean alreadyFlipped = this.sprite.isFlipped();
        this.sprite.setActive(false);
       
        this.sprite = sprite;
        this.sprite.setActive(true);
        
        //If the previous sprite was flipped, the new one will be too
        if (alreadyFlipped) {
            this.sprite.flip();
        }
    }
    
    /**
     * The isColliding method checks to see if this entity intersects another entity at the specified world coordinates.
     * @param x The world x position.
     * @param y The world y position.
     * @param e The other entity to check for.
     * @return Whether this entity is intersecting the other.
     */
    public boolean isColliding(int x, int y, Entity e) {
        return this.getBoundingBox(x, y).intersects(e.getBoundingBox());
    }
    
    /**
     * This getBoundingBox method returns this entity's bounding box, but at the specified x and y coordinates.
     * @param x The x position
     * @param y The y position
     * @return This entity's bounding box, AKA the smallest rectangle this entity can fit inside.
     */
    public Rectangle getBoundingBox(int x, int y) {
        return new Rectangle(x, y, sprite.getWidth(), sprite.getHeight());
    }
    
    /**
     * The getBoundingBox method returns this entity's bounding box, used for collision. The top left corner of this
     * box is the top left corner of the entity.
     * @return This entity's bounding box, AKA the smallest rectangle this entity can fit inside.
     */
    public Rectangle getBoundingBox() {
        return new Rectangle((int) Math.round(x), (int) Math.round(y), sprite.getWidth(), sprite.getHeight());
    }
    
    /* The following are just getters and setters */
    public float getX() {
        return this.x;
    } public void setX(float x) {
        this.x = x;
    } public float getY() {
        return this.y;
    } public void setY(float y) {
        this.y = y;
    } public Sprite getSprite() {
        return this.sprite;
    }
    
    /**
     * The destroy method dictates what happens when this entity is removed from the game.
     */
    abstract public void destroy();
}