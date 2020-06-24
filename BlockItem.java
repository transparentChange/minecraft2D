package version2;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*
 * [BlockItem.java]
 * This class represents an Item of type Block that is used in the Inventory
 * @author Matthew Sekirin
 */
public class BlockItem extends Item { 	
	BlockItem() {
		super(1);
		try {
			iconImage = ImageIO.read(new File("Sprites/grassBlockIcon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * use
	 * This method reduces the number of copies of this Item by 1
	 */
	@Override
	public void use() {
		stack(-1);
	}
}
