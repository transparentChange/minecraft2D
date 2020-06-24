package version2;

import java.util.Iterator;

/* 
 * [BlockIterator.java]
 * This class is an iterator for a 2D Block array passed into the constructor
 * @author Matthew Sekirin, Andy Wang
 */
public class BlockIterator implements Iterator<Block> {
    private int indexX = 0;
    private int indexY = 0;
    private Block[][] map;
    
    BlockIterator(Block[][] map) {
        this.map = map;
    }
    
    /*
     * hasNext
     * This method checks if there remains a Block that has not been returned by the next() method
     * @boolean, true if a Block remains, false otherwise
     */
    @Override
	public boolean hasNext() {
		for (int i = indexY; i < map.length; i++) {
			for (int j = indexX; j < map[0].length; j++) {
				if (map[i][j] != null) {
					return true;
				}
			}
		}
		return false;
	}
    
    /*
     * next
     * This method returns the next block in map
     * @return Block, the next Block
     */
    @Override
    public Block next() {
        for (int i = indexY; i < map.length; i++) {
            for (int j = indexX; j < map[0].length; j++) {
                if (map[i][j] != null) {
                    indexY = i;
                    indexX = j;
                    
                    //Only increment indices if there is next value
                    if (this.hasNext()) {
                        indexX++;
                        if (indexX == map[0].length) {
                            indexX = 0;
                            indexY++;
                        }
                    }
                    return map[i][j];
                }
            } indexX = 0; //Without this line, it would skip indices 0 - indexX when looping the next row
        } this.reset();
        return null;
    }
    
    /*
     * reset
     * This method resets the iterator
     */
    public void reset() {
        indexX = 0;
        indexY = 0;
    }
}
