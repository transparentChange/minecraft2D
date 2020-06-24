package version2;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * SelectionTool
 * This class allows a user to see what block portion of the map their cursor is on
 * @author Matthew Sekirin
 */
public class SelectionTool {
	private BufferedImage selectionImage;

	SelectionTool() {
		try {
			selectionImage = ImageIO.read(new File("Sprites/selection1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * draw
	 * This method draws the image corresponding to the Selection Tool in the
	 * appropriate location
	 * @param g, the Graphics object
	 * @param worldPoint, the Point object that shows where the Selection should be
	 * drawn, relative to the world
	 * @param cameraOffset, the offset of the Camera, used to convert back to
	 * Camera coordinates once the world coordinates of the Selection are calculated
	 */
	public void draw(Graphics g, Point worldPoint, Point cameraOffset) {
		int xPos, yPos;

		xPos = worldPoint.x - worldPoint.x % Map.BLOCK_DIM - cameraOffset.x;
		yPos = worldPoint.y - worldPoint.y % Map.BLOCK_DIM - cameraOffset.y;
		if (worldPoint.x < 0) {
			xPos -= Map.BLOCK_DIM;
		}
		if (worldPoint.y < 0) {
			yPos -= Map.BLOCK_DIM;
		}
		g.drawImage(selectionImage, xPos, yPos, null);
	}
}
