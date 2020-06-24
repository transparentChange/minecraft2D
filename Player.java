package version2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/*
 * [Player.java]
 * This file contains the player class.
 * Author: Andy Wang
 * Date: 13 June 2020
 */

import java.awt.event.KeyEvent;

/**
 * The Player class represents the player, or the entity the player is controlling.
 * @author Andy Wang
 * @since 13 June 2020
 */
class Player extends Animal {
    private final int MAX_HP = 100;
    private final int JUMP_SPEED = 15;
    private final int WALK_SPEED = 5;
    private boolean jumping;
    private Point centerPoint;
    
    /**
     * This constructor makes the Player's character at specified x and y positions.
     * @param x The x position.
     * @param y The y position.
     */
    public Player(float x, float y, Map map) {
        super(x, y, map);
        
        this.sprite = new Sprite("Sprites/tempPlayer2.png");
        this.centerPoint = new Point((int) (x + sprite.getWidth() / 2), (int) (y + sprite.getHeight() / 2));
       
        this.hp = MAX_HP;
        this.active = true; //The player is always active
    }
    
    /**
     * The checkKeysPressed method dictates what happens when the player presses a certain key. This method is called in the JFrame's
     * KeyListener.
     * @param e The KeyEvent to process.
     */
    public void checkKeysPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            this.left = 1;
        } if (e.getKeyCode () == KeyEvent.VK_D) {
            this.right = 1;
        } if (e.getKeyCode () == KeyEvent.VK_SPACE) {
          if (this.hp > 0) { //prevent player from jumping while death animation is playing
            this.jumping = true;
          }
        }
    }
    
    /**
     * The checkKeysReleased method dictates what happens when the player releases a certain key. This method is called in the JFrame's
     * KeyListener.
     * @param e The KeyEvent to process.
     */
    public void checkKeysReleased (KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            this.left = 0;
        } if (e.getKeyCode () == KeyEvent.VK_D) {
            this.right = 0;
        } if (e.getKeyCode () == KeyEvent.VK_SPACE) {
            this.jumping = false;
        }
    }
    
    /**
     * The step method moves the player and is called (almost) every frame of the game.
     */
    @Override public void step() {
        this.move = right - left;
        this.dx = move * WALK_SPEED;
        this.dy += Map.GRAVITY;
        
        // HANDLE JUMPING
        if ((isOnGround() || (this.y + dy + sprite.getHeight() > mapDimension.height)) && jumping) {
            this.dy = -JUMP_SPEED; 
        }
        
        handleXCollisions();
		updateXPosition(mapDimension.width);
		handleYCollisions();
		updateYPosition(mapDimension.height);
		updateCenterPoint();
        
		// Flip the sprite if the player is moving left or flip the sprite back to face right if the player moves from left to right
        if (((move == -1) && !sprite.isFlipped()) || ((move == 1) && this.sprite.isFlipped())) {   
            this.sprite.flip();
        }
        
        //IF THE PLAYER DIES
        if (this.hp <= 0) {
            this.destroy();
        }
        
        if ((move != 0) && (onGround)) {
            Sound.FOOTSTEPS.play();
        } else if (Sound.FOOTSTEPS.isPlaying()) {
            Sound.FOOTSTEPS.stop(); //Stop when the player jumps or stops moving
        }
        
        /*
        //PICK UP ITEM DROP
        for (ItemDrop d : ACTIVE_ITEM_DROPS) {
            if (isColliding(x, y, d) {
                pickUpItemDrop(d);
            }
        } */
    }
    
    /**
     * The pickUpItem method picks up an item drop if the player is near it.
     * @param drop The item drop to pick up.
     */
    /* public void pickUpItem(ItemDrop drop) {
        //Loop through the player's inventory to find the first empty slot
        for (int i = 0; i < INVENTORY_SPACE; i++) {
            if (inventory[i] == null) {
                inventory[i] = drop.getItem();
            }
        }
    } */
    
    /**
     * The destroy method dictates what happens when this player dies (hp < 0). In the player's case, it will respawn
     * after a certain amount of time.
     */
    @Override public void destroy() {
        try {
            System.out.println("You died! Respawning...");
            Sound.DEATH.play();
            Thread.sleep(3000);
            this.x = map.getPlayerLoadX();
            this.y = map.getPlayerLoadY();
        } catch (Exception e) {
            e.printStackTrace();
        } this.hp = MAX_HP;
        //respawning where you die is probably a bad idea but it's too early in development to change it
    }
   
    private void updateCenterPoint() {
    	centerPoint.x = (int) (x + sprite.getWidth() / 2);
    	centerPoint.y = (int) (y + sprite.getHeight() / 2);
    }
    
    public Point getCenterPoint() {
    	return centerPoint;
    }
}