package version2;

/* 
 * [Sprite.java]
 * This file contains the Sprite class, which represents how an entity in a game is drawn.
 * Author: Andy Wang
 * Date: 10 June 2020
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The Sprite class represents an entity's sprite, which is how the entity is drawn.
 * @author Andy Wang
 * @since 10 June 2020
 */
class Sprite {
    private BufferedImage[] frames;
    private boolean isFlipped = false; //if the sprite is currently flipped (facing left)
    private boolean active = true; //If this sprite is currently being used by an entity
    private int currentFrame = 0; //index of the current frame
    private String fileName;
    
    /**
     * This constructor creates a sprite given the name of an image file. This is used for non-animated sprites.
     * @param fileName The name of the frame file.
     */
    public Sprite(String fileName) {
        try { 
            File frameFile = new File(fileName);
            frames = new BufferedImage[1];
            frames[0] = ImageIO.read(frameFile);
            this.fileName = fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }                          
                                 
    /**
     * This constructor creates a sprite given the name of the folder containing the frames, if the sprite is animated.
     * @param folderName The name of the folder containing the frames.
     * @param delay The delay in miliseconds in between frames.
     */
    public Sprite(String folderName, int frameDelay) {
        File folder = new File(folderName);
        File[] frameFiles = folder.listFiles();
        
        this.fileName = folderName;
        
        //Sort the frame files by name in ascending order (FRAME NAMES SHOULD BE NUMBERS!!!!)
        Arrays.sort(frameFiles, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                String n1 = f1.getName();
                String n2 = f2.getName();
                
                //Remove the ".png" at the end
                n1 = n1.substring(0, n1.length() - 4);
                n2 = n2.substring(0, n2.length() - 4);
                
                if (Integer.parseInt(n1) < Integer.parseInt(n2)) {
                    return -1;
                } else if (Integer.parseInt(n1) > Integer.parseInt(n2)) {
                    return 1;
                } else {
                    return 0;
                }
            } 
        });
        
        this.frames = new BufferedImage[frameFiles.length];
        
        try {
            //Populate frames with BufferedImages
            for (int i = 0; i < frames.length; i++) {
                frames[i] = ImageIO.read(frameFiles[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        Thread spriteAnimator = new Thread(new Runnable() {
            @Override public void run() {
                while (active) {
                    try {
                        Thread.sleep(frameDelay);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } currentFrame++;
                    currentFrame %= frames.length; //if currentFrame goes out of bounds it becomes zero again
                }
            }
        });
        spriteAnimator.start();
    } 
    
    /**
     * The flip method flips every frame horizontally.
     */
    public void flip() {
        for (int i = 0; i < frames.length; i++) {
            BufferedImage orig = this.frames[i];
            BufferedImage flipped = new BufferedImage(orig.getWidth(), orig.getHeight(), BufferedImage.TYPE_INT_ARGB);
            
            //Flip each image by writing pixels to a new image
            for (int x = 0; x < orig.getWidth(); x++){
                for (int y = 0; y < orig.getHeight(); y++){
                    flipped.setRGB(orig.getWidth() - 1 - x, y, orig.getRGB(x, y));
                }
            } this.frames[i] = flipped;
        } this.isFlipped = !this.isFlipped;
    }
    
    /**
     * The setActive method sets whether or not this sprite is the active one.
     * @param bool If it's active or not.
     */
    public void setActive(boolean bool) {
        this.active = bool;
    }
    
    /**
     * The isFlipped method returns whether or not the sprite is facing left.
     * @return Whether or not the sprite is facing left.
     */
    public boolean isFlipped() {
        return isFlipped;
    }
    
    /**
     * The currentFrame method returns the current frame as a BufferedImage.
     * @return The current frame as a BufferedImage
     */
    public BufferedImage currentFrame() {
        return this.frames[this.currentFrame];
    }
    
    /**
     * The getWidth method returns the width of the current frame in pixels.
     * @return The width of the current frame in pixels.
     */
    public int getWidth() {
        return this.frames[0].getWidth(); //all sprites should have the same dimensions
    }
    
    /**
     * The getHeight method returns the height of the current frame in pixels.
     * @return The height of the current frame in pixels.
     */
    public int getHeight() {
        return this.frames[0].getHeight();
    }
    /**
     * The getName method returns the name of the directory of the sprite's frames.
     * @return The sprite's file name
     */
    public String getName() {
        return this.fileName;
    }
}