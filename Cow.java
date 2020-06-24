/*
 * [Cow.java]
 * This file contains the cow class.
 * Author: Andy Wang
 * Date: 18 June 2020
 */

package version2;

import java.awt.Dimension;

/**
 * The Cow class is a passive animal in the game. It just roams around freely.
 * @author Andy Wang
 * @since 18 June 2020
 */
class Cow extends Animal {
    private final int WALK_SPEED = 2;
    private final int JUMP_SPEED = 13;
    private final int MAX_HEALTH = 50;
    private String idleSpriteName = "Sprites/cowWalk/1.png";
    private String walkingSpriteName = "Sprites/cowWalk";
    
    private long startingTime = (long) (System.nanoTime()/1000000000.0);
    private int walkingChangeTime = (int) (Math.random() * 7) + 1;
    
    
    /**
     * This constructor initializes a cow.
     * @param x The x position of the cow.
     * @param y The y position of the cow.
     * @param map The map that the cow is on.
     */
    public Cow(float x, float y, Map map) {
        super(x, y, map);
        this.hp = MAX_HEALTH;
        this.sprite = new Sprite(idleSpriteName); //idle is the first frame of the walk animation
             
        Cow currentCow = this;
        Thread updater = new Thread(new Runnable() {
            @Override public void run() {
                //Wait for the player to load first
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                while (currentCow.getHP() > 0) {
                    //Calculate x and y distance from the player
                    int dx = (int) Math.abs(currentCow.getX() - GameWindow.player.x);
                    int dy = (int) Math.abs(currentCow.getY() - GameWindow.player.y);
                    
                    //If the cow is a certain distance away from the player, then it is not active anymore.
                    if ((dx >= 15 * Map.BLOCK_DIM) || (dy >= 8 * Map.BLOCK_DIM)) {
                        currentCow.setActive(false);
                        currentCow.getSprite().setActive(false);
                    } else {
                        currentCow.setActive(true);
                        currentCow.getSprite().setActive(true);
                    }
                    
                    if (currentCow.isActive()) {
                        currentCow.step();
                    }
                    
                    try {
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updater.start();
    }
    
    /**
     * The step method calculates the cow's movement almost each frame of the game.
     * @param bi The BlockIterator to use.
     */
    @Override public void step() {
    	setDirection();
        
        this.move = right - left;
        this.dx = move * WALK_SPEED;
        this.dy += Map.GRAVITY;
        
        jumping = handleXCollisions();
        handleYCollisions();
        
        // HANDLE JUMPING
        if ((isOnGround() || (this.y + dy + sprite.getHeight() > mapDimension.height)) && jumping) {
            this.dy = -JUMP_SPEED; 
        }
        
        updateXPosition(mapDimension.width);
        updateYPosition(mapDimension.height);
        
        if ((this.dx != 0) && (!this.sprite.getName().equals(walkingSpriteName))) {
            this.changeSprite(new Sprite(walkingSpriteName, 250));
        } else if ((this.dx == 0) && (!this.sprite.getName().equals(idleSpriteName))) {
            this.changeSprite(new Sprite(idleSpriteName));
        }
        
         // Flip the sprite if the player is moving left or flip the sprite back to face right if the player moves from left to right
        if (((move == -1) && !sprite.isFlipped()) || ((move == 1) && this.sprite.isFlipped())) {   
            this.sprite.flip();
        }
    }
    
    private void setDirection() {
    	//Choose whether to walk right or left based on time passed
        long timeNow = (long) (System.nanoTime()/1000000000.0);
        if (timeNow - startingTime >= walkingChangeTime) {
            Sound.COW_MOO.play();
            startingTime = (long) (System.nanoTime()/1000000000.0);
            walkingChangeTime = (int) (Math.random() * 5) + 1;
            
            int num = (int) (Math.random() * 3) + 1;
            if (num == 1) { //Go left
                left = 1;
                right = 0;
            } else if (num == 2) { //Go right
                right = 1;
                left = 0;
            } else {
                right = 0;
                left = 0;
            }
        }
    }
    
    /**
     * The destroy method is called when the cow is removed from the game (it dies)
     */
    @Override public void destroy() {
        //Maybe play a cow squeal sound effect
        this.map.removeAnimal(this);
    }
}