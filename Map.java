package version2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;

/* 
 * [Map.java]
 * This file contains the Map class, which contains an Entity 2D array.
 * Author: Matthew Sekirin
 * Date: 10 June 2020
 */
public class Map {
    private Block[][] blockMap;
    private ArrayList<Animal> animals;
    private Integer playerLoadX = null;
    private Integer playerLoadY = null;
    public static final int BLOCK_DIM = 45;
    public static final float GRAVITY = 1.2f;
    
    Map(String filenameRead) {
        animals = new ArrayList<Animal>();
        // order important
        readWorldMap(filenameRead);
    }
    
    /*
     * newBlock
     * This method creates a new block at the position specified by pixelWorldPoint
     * @param pixelWorldPoint, the point on the canvas where the mouse is clicked, but converted to be relative to the world.
     * @param newBlockType, a char representation of the Block to be created
     */
    public void newBlock(Point pixelWorldPoint, char newBlockType) { // change to ItemSelected
        int xPos = pixelWorldPoint.x / BLOCK_DIM;
        int yPos = pixelWorldPoint.y / BLOCK_DIM;
        if (newBlockType != ' ') {
            blockMap[yPos][xPos] = new GrassDirtBlock(xPos * BLOCK_DIM, yPos * BLOCK_DIM);
        }
    }
    
    /**
     * The addAnimal method adds an animal to the map.
     * @param a The animal to add.
     */
    public void addAnimal(Animal a) {
        this.animals.add(a);
    }
    
    /**
     * The removeAnimal method removed an animal from the map.
     * @param a The animal to remove.
     */
    public void removeAnimal(Animal a) {
        this.animals.remove(a);
    }
    
    /*
     * removeBlock
     * This method remove the block at the position specified by pixelWorldPoint and returns it
     */
    public Block deleteBlock(Point pixelWorldPoint) {
        int xPos = pixelWorldPoint.x / BLOCK_DIM;
        int yPos = pixelWorldPoint.y / BLOCK_DIM;
        
        if (blockMap[yPos][xPos] == null) {
            System.out.println("oh no");
        }
        
        blockMap[yPos][xPos].destroy();
        Block blockToDelete = blockMap[yPos][xPos];
        blockMap[yPos][xPos] = null;
        //System.out.println("here");
        
        return blockToDelete;
    }
    
    public boolean isFull(Point pixelWorldPoint) {
        if (blockMap[pixelWorldPoint.y / BLOCK_DIM][pixelWorldPoint.x / BLOCK_DIM] != null) {
            return true;
        } else {
            return false;
        }
    }
    
    /*
     * draw
     * This method invokes the draw method on a selection of entities in entityMap
     * @param g, a Graphics object
     * @param startPoint, a Point representing the upper-left corner of the camera
     */
    public void draw(Graphics g, Point startPoint, Dimension dimView) {
        int mapXStart = Math.max(startPoint.x / BLOCK_DIM, 0);
        int mapYStart = Math.max(startPoint.y / BLOCK_DIM, 0);
        if ((mapXStart >= blockMap[0].length) || (mapYStart >= blockMap.length)) { // will be deleted later
            throw new IllegalArgumentException("Drawing off map (too far down or right)" + mapXStart + " " + mapYStart);
        }
        
        int xEnd = (int) Math.min((startPoint.x + dimView.getWidth()) / BLOCK_DIM, blockMap[0].length - 1); // if off the map
        int yEnd = (int) Math.min((startPoint.y + dimView.getHeight()) / BLOCK_DIM, blockMap.length - 1);
        if ((xEnd < 0) || (yEnd < 0)) { // will be deleted later
            throw new IllegalArgumentException("Drawing off map (too far up or left)" + mapXStart + " " + mapYStart);
        }        
        
        for (int i = mapYStart; i <= yEnd; i++) {
            for (int j = mapXStart; j <= xEnd; j++) {
                if (blockMap[i][j] != null) {
                    blockMap[i][j].draw(g, startPoint);
                }
            }
        } for (Animal a : this.animals) {
            if (a.isActive()) {
                a.draw(g, startPoint);
            }
        }
    }
    
    /*
     * readWorldMap
     * A method that takes a map in the form of a space-separated grid
     * and stores it as a 2D Entity array
     * @param filename, the name of the text file in which the map is located
     */
    public void readWorldMap(String filename) {
        File myFile = new File(filename);
        try {
            Scanner input = new Scanner(myFile);
            int xSize = input.nextInt();
            int ySize = input.nextInt();
            input.nextLine();
            
            blockMap = new Block[ySize][xSize];
            String line;
            for (int i = 0; i < ySize; i++) {
                line = input.nextLine();
                for (int j = 0; j < xSize; j++) {
                    if (line.charAt(j * 2) == '1') {
                        blockMap[i][j] = new GrassDirtBlock(j * BLOCK_DIM, i * BLOCK_DIM);
                    } else if (line.charAt(j * 2) == '@') {
                        playerLoadX = j * BLOCK_DIM;
                        playerLoadY = (i - 1) * BLOCK_DIM; // player sprite takes up more than one block vertically
                    } else if (line.charAt(j * 2) == 'p') {
                        this.addAnimal(new Pig(j * BLOCK_DIM, i * BLOCK_DIM,  this));
                    } else if (line.charAt(j * 2) == 'c') {
                        this.addAnimal(new Cow(j * BLOCK_DIM, i * BLOCK_DIM,  this));
                    } else if (line.charAt(j * 2) == 'z') {
                        this.addAnimal(new Zombie(j * BLOCK_DIM, i * BLOCK_DIM, this));
                    }
                }
            }
            
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * writeMapToFile
     * 
     */
    public void writeMapToFile(String filename, Point playerPos) {
        File writeFile = new File(filename);
        try {
            PrintWriter output = new PrintWriter(writeFile);
            output.println(blockMap[0].length);
            output.println(blockMap.length);
            for (int i = 0; i < blockMap.length; i++) {
                for (int j = 0; j < blockMap[0].length; j++) {
                	if (!printAnimals(output, i, j)) {
	                    if (blockMap[i][j] !=  null) {
	                        output.print("1 ");
	                    } else if ((playerPos.x / BLOCK_DIM == j) && (playerPos.y / BLOCK_DIM == i)) {
	                        output.print("@ ");
	                    } else {
	                        output.print("0 ");
	                    }
                	}
                }
                output.println();
            }
            
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean printAnimals(PrintWriter output, int i, int j) {
    	for (int k = 0; k < animals.size(); k++) {
    		if (((int) (animals.get(k).x / BLOCK_DIM) == j) 
    				&& ((int) (animals.get(k).y / BLOCK_DIM) == i)) {
    			if (animals.get(k) instanceof Pig) {
    				output.print("p ");
    			} else if (animals.get(k) instanceof Cow) {
    				output.print("c ");
    			} else if (animals.get(k) instanceof Zombie) {
    				output.print("z ");
    			}
    			animals.remove(k);
    			return true;
    		}
    	}
    	return false;
    }
    
    public BlockIterator getBlockIterator() {
        return new BlockIterator(blockMap);
        //return a different iterator, rather than a pointer to a single one
    }
    
    public Dimension getMapDimension() {
        return new Dimension(blockMap[0].length * BLOCK_DIM, blockMap.length * BLOCK_DIM);
    }
    
    public Integer getPlayerLoadX() {
        return playerLoadX;
    }
    
    public Integer getPlayerLoadY() {
        return playerLoadY;
    }
}
