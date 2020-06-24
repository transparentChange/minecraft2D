package version2;

/*
 * [GrassDirtBlock.java]
 * This class represents a Block with grass and dirt
 */
public class GrassDirtBlock extends Block {
    GrassDirtBlock(int x, int y) {
        super(x, y);
        this.sprite = new Sprite("Sprites/grassBlock.png");
    }
}
