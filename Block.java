package version2;

/*
 * [Block.java]
 * This class represents a generic Block in the world 
 */
abstract public class Block extends Entity {
    
    Block(int x, int y) {
        super(x, y);
    }
    
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
