/*
 * [Pig.java]
 * This file contains the pig class.
 * Author: Andy Wang
 * Date: 18 June 2020
 */

package version2;

import java.awt.Dimension;

/**
 * The Pig class is a passive animal in the game. It just roams around freely.
 * @author Andy Wang
 * @since 18 June 2020
 */
class Pig extends Animal {
    private final int WALK_SPEED = 3;
    private final int JUMP_SPEED = 12;
    private final int MAX_HEALTH = 30;
    private String idleSpriteName = "Sprites/pigWalk/1.png";
    private String walkingSpriteName = "Sprites/pigWalk";
    
    private long startingTime = (long) (System.nanoTime()/1000000000.0);
    private int walkingChangeTime = (int) (Math.random() * 5) + 1;
    
    
    /**
     * This constructor initializes a pig.
     * @param x The x position of the pig.
     * @param y The y position of the pig.
     * @param map The map that the pig is on.
     */
    public Pig(float x, float y, Map map) {
        super(x, y, map);
        this.hp = MAX_HEALTH;
        this.sprite = new Sprite(idleSpriteName); //idle is the first frame of the walk animation
             
        Pig currentPig = this;
        Thread updater = new Thread(new Runnable() {
            @Override public void run() {
                //Wait for the player to load first
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                while (currentPig.getHP() > 0) {
                    //Calculate x and y distance from the player
                    int dx = (int) Math.abs(currentPig.getX() - GameWindow.player.x);
                    int dy = (int) Math.abs(currentPig.getY() - GameWindow.player.y);
                    
                    //If the pig is a certain distance away from the player, then it is not active anymore.
                    if ((dx >= 15 * Map.BLOCK_DIM) || (dy >= 8 * Map.BLOCK_DIM)) {
                        currentPig.setActive(false);
                        currentPig.getSprite().setActive(false);
                    } else {
                        currentPig.setActive(true);
                        currentPig.getSprite().setActive(true);
                    }
                    
                    if (currentPig.isActive()) {
                        currentPig.step();
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
     * The step method calculates the pig's movement almost each frame of the game.
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
            Sound.PIG_OINK.play();
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
     * The destroy method is called when the pig is removed from the game (it dies)
     */
    @Override public void destroy() {
        //Maybe play a pig squeal sound effect
        this.map.removeAnimal(this);
    }
}