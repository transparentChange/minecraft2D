package version2;

import java.awt.Dimension;
import java.awt.Point;

/* 
 * [Camera.java]
 * This file contains the Camera class, which is used to change the game screen to show different portions of the world.
 * @author Matthew Sekirin
 * @since 10 June 2020
 */
public class Camera {
    public final Dimension DIM = new Dimension(1334, 711); // JFrame without borders
    private Point posPoint = new Point(0, 0); // upper-left point of Camera representing position (pixels) on world map
    
    /*
     * update
     * This method centers the camera around the parameters by changing posPoint
     * @param centerX, centerY, the coordinates of the point around which the 
     * camera is being centered
     */
    public void update(Point centerPoint, Dimension mapDimension) {
    	int newXPosition = (int) (centerPoint.x - DIM.getWidth() / 2);
    	if ((newXPosition >= 0) && (newXPosition <= mapDimension.width - DIM.width)) {
    		posPoint.x = newXPosition;
    	} else if (newXPosition > mapDimension.width - DIM.width) {
    		posPoint.x = mapDimension.width - DIM.width;
    	} else {
    		posPoint.x = 0;
    	}
    	
    	int newYPosition = (int) (centerPoint.y - DIM.getHeight() / 2);
    	if ((newYPosition >= 0) && (newYPosition <= mapDimension.height - DIM.height)) {
    		posPoint.y = newYPosition;
    	} else if (newYPosition > mapDimension.height - DIM.height) {
    		posPoint.y = mapDimension.height - DIM.height;
    	} else {
    		posPoint.y = 0;
    	}
    }
    
    /*
     * toCameraCoordinates
     * This method converts the Point passed in as a parameter to a Point
     * whose coordinates are relative to the Camera (all in pixels)
     * @param worldPoint, the Point relative to the world being converted
     * @return Point, the result of the conversion
     */
    public Point toCameraCoordinates(Point worldPoint) {
        return new Point(worldPoint.x - posPoint.x, worldPoint.y - posPoint.y);
    }
    
    /*
     * toWorldCoordinates
     * This method converts the Point passed in as a parameter to a Point
     * whose coordinates are relative to the world (all in pixels)
     * @param cameraPoint, the Point relative to the camera being converted
     * @return Point, the result of the conversion
     */
    public Point toWorldCoordinates(Point cameraPoint) {
        return new Point(cameraPoint.x + posPoint.x, cameraPoint.y + posPoint.y);
    }
    
    /*
     * getPosPoint
     * This method returns the posPoint field
     * @return Point, the posPoint field which represents the upper-left corner of the camera
     */
    public Point getPosPoint() {
        return posPoint;
    }
}
