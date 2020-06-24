package version2;

/*
 * [Breakable.java]
 * This file contains the Breakable interface.
 * Author: Andy Wang
 * Date: 12 June 2020
 */

/**
 * The Breakable interface is used for objects that can be damages/be destroyed.
 * @author Andy Wang
 * @since 12 June 2020
 */
interface Breakable {
    /**
     * The damage method damages the object by n health points.
     * @param n The amount of damage done.
     */
    public void damage(int n);
}