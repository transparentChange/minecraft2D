/*
 * [Zombie.java]
 * This file contains the zombie class.
 * Author: Andy Wang
 * Date: 19 June 2020
 */

package version2;

/**
 * The Zombie class is a hostile animal in the game. It will try to attack the player when it comes close enough.
 * @author Andy Wang
 * @since 19 June 2020
 */
class Zombie extends Animal implements Hostile {
    private final int WALK_SPEED = 2;
    private final int JUMP_SPEED = 10;
    private final int DAMAGE = 20;
    private final int MAX_HEALTH = 100;
    private final int ATTACK_RANGE = 6 * Map.BLOCK_DIM; //attacks within 6 blocks
    private String idleSpriteName = "Sprites/zombieIdle.png";
    private String walkingSpriteName = "Sprites/zombieWalk";
    
    private long startingTime = (long) (System.nanoTime()/1000000000.0);
    private int walkingChangeTime = (int) (Math.random() * 5) + 1;
    
    private long attackStart = (long) (System.nanoTime()/1000000000.0);
    private int attackDelay = 2;
    
    
    /**
     * This constructor initializes a zombie.
     * @param x The x position of the zombie.
     * @param y The y position of the zombie.
     * @param map The map that the zombie is on.
     */
    public Zombie(float x, float y, Map map) {
        super(x, y, map);
        this.hp = MAX_HEALTH;
        this.sprite = new Sprite(idleSpriteName); //idle is the first frame of the walk animation
             
        Zombie currentZombie = this;
        Thread updater = new Thread(new Runnable() {
            @Override public void run() {
                //Wait for the player to load first
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                while (currentZombie.getHP() > 0) {
                    //Calculate x and y distances from the player
                    int dx = (int) Math.abs(currentZombie.getX() - GameWindow.player.x);
                    int dy = (int) Math.abs(currentZombie.getY() - GameWindow.player.y);
                    
                    //If the zombie is a certain distance away from the player, then it is not active anymore.
                    if ((dx >= 15 * Map.BLOCK_DIM) || (dy >= 8 * Map.BLOCK_DIM)) {
                        currentZombie.setActive(false);
                        currentZombie.getSprite().setActive(false);
                    } else {
                        currentZombie.setActive(true);
                        currentZombie.getSprite().setActive(true);
                    }
                    
                    if (currentZombie.isActive()) {
                        currentZombie.step();
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
     * The step method calculates the zombie's movement almost each frame of the game.
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
        
        if ((this.isColliding((int)x, (int)y, GameWindow.player)) && (GameWindow.player.getHP() > 0)) {
        	attackPlayer();
        }
        
        updateXPosition(mapDimension.width);
        updateYPosition(mapDimension.height);
        
        if ((this.dx != 0) && (!this.sprite.getName().equals(walkingSpriteName))) {
            this.changeSprite(new Sprite(walkingSpriteName, 400));
        } else if ((this.dx == 0) && (!this.sprite.getName().equals(idleSpriteName))) {
            this.changeSprite(new Sprite(idleSpriteName));
        }
        
        // Flip the sprite if the player is moving left or flip the sprite back to face right if the player moves from left to right
        if (((move == -1) && !sprite.isFlipped()) || ((move == 1) && this.sprite.isFlipped())) {   
            this.sprite.flip();
        }
    }
    
    private void setDirection() {
    	//Calculate x distance from the player
        int fromPlayerX = (int) Math.abs(this.x - GameWindow.player.x);
        
        if (fromPlayerX <= ATTACK_RANGE) {
            if (this.x < GameWindow.player.x) {
                //If the zombie is to the left, then move right towards the player
                left = 0;
                right = 1;
            } else {
                //If the zombie is to the right, then move left towards the player
                left = 1;
                right = 0;
            }
        } else {//Choose whether to walk right or left based on time passed
            long timeNow = (long) (System.nanoTime()/1000000000.0);
            Sound.ZOMBIE_GROAN.play();
            if (timeNow - startingTime >= walkingChangeTime) {
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
    }
    
	private void attackPlayer() {
		long timeNow = (long) (System.nanoTime() / 1000000000.0);
		if (timeNow - attackStart >= attackDelay) {
			attackStart = timeNow;
			this.attack();
		}
	}
    
    /**
     * The destroy method is called when the zombie is removed from the game (it dies)
     */
    @Override public void destroy() {
        //Maybe play a zombie squeal sound effect
        this.map.removeAnimal(this);
    }
    
    /**
     * The attack method does damage to the player.
     */
    @Override public void attack() {
        GameWindow.player.damage(this.DAMAGE);
        Sound.HIT_SOUND.play();
    }
}