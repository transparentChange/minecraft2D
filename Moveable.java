package version2;

/*
 * [Moveable.java]
 * This file contains the Moveable interface.
 * Author: Andy Wang
 * Date: 13 June 2020
 */

/**
 * The Moveable class is reserved for non-animal entities that can move around in the world.
 * @author Andy Wang
 * @since 13 June 2020
 */
interface Moveable {
    /**
     * The step method changes the positition of the entity. This method is called repeatedly.
     */
    public void step();
}