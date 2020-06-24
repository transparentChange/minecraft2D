package version2;

import java.util.ArrayList;

/*
 * [Inventory.java]
 * This class represents an inventory for the player, allowing the player's items
 * to be changed and keeping track of which item is currently being used.
 * @author Matthew Sekirin
 */
public class Inventory {
    private ArrayList<Item> items;
    private int indexHand;
    
    Inventory() {
        items = new ArrayList<Item>();
    }
    
    /*
     * update
     * This method sets the indexHand field to the index argument and deals with items that are used up
     * @param index, the new indexHand value
     */
    public void update(int index) {
        indexHand = index;
        if ((items.size() != 0) && (indexHand < items.size()) && (items.get(indexHand).getCopies() == 0)) {
            items.remove(indexHand);
            
            if (indexHand != 0) {
                indexHand--;
            } else {
                indexHand = 0;
            }
        }
    }
    
    /*
     * addItem
     * This method adds the itemToAdd parameter to the items ArrayList
     * @param itemToAdd, the Item to be added
     */
    public void addItem(Item itemToAdd) {
        items.add(itemToAdd);
    }
    
    /*
     * addBlock
     * This method adds a single Block to the inventory, creating a new BlockItem if necessary, and updates
     * indexHand.
     */
    public void addBlock(Block blockToAdd) { 
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof BlockItem) {
                items.get(i).stack(1);
                indexHand = i;
                return;
            }
        }
        items.add(new BlockItem());
        indexHand = items.size() - 1;
    }
    
    /* 
     * getHand
     * This method returns the Item at at the index indexHand
     * @return the item, or null of there are no items or the indexHand is less than the number of items
     */
    public Item getHand() {
        if ((items.size() != 0) && (indexHand < items.size())) {
            return items.get(indexHand);
        } else {
            return null;
        }
    }
    
    /*
     * getIndexHand
     * This method returns the indexHand field
     * @return indexHand
     */
    public int getIndexHand() {
        return indexHand;
    }
    
    /*
     * getItem
     * This method returns the item at index i, the parameter
     * @param int i, the index of the item to be returned
     * @return the ith item if the index is less than the items' size, null otherwise
     */
    public Item getItem(int i) {
        if (i < items.size()) {
            return items.get(i);
        } else {
            return null;
        }
    }
}