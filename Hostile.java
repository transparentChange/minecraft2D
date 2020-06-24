/*
 * [Hostile.java]
 * This file contains the hosstile interface.
 * Author: Andy Wang
 * Date: 19 June 2020
 */

package version2;

/**
 * The Hostile interface is used for animals that attack the player.
 * @author Andy Wang
 * @since 19 June 2020
 */
interface Hostile {
    /**
     * The attack method declares what happens when a hostile animal attacks the player.
     */
    public void attack();
}