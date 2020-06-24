package version2;

import java.awt.Color;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

/*
 * GameWindow
 * This class is the JFrame that holds the JPanels GamePanel and MyGlassPane. 
 */
public class GameWindow extends JFrame {
    private static final Dimension DIM_JFRAME = new Dimension(1350, 750);
    private MyGlassPane glassPane;
    
    private Map map = new Map("map1.txt");
    private Camera camera = new Camera();
    public static Player player;
    private Inventory inventory = new Inventory();
    
    GameWindow() {
        if ((map.getPlayerLoadX() != null) && (map.getPlayerLoadX() != null)) {
            player = new Player(map.getPlayerLoadX(), map.getPlayerLoadY(), map);
        }
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                map.writeMapToFile("map1.txt", new Point((int) player.getX(), (int) player.getY()));
            }
        });
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DIM_JFRAME);
        
        add(new GamePanel());
        
        glassPane = new MyGlassPane();
        setGlassPane(glassPane);
        glassPane.setVisible(true);
        glassPane.setOpaque(false);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        GameWindow test = new GameWindow();
    }
    
    /*
     * GamePanel
     * This class is the JPanel on which the game is played
     */
    public class GamePanel extends JPanel {
        private Point mousePoint;
        private SelectionTool selectionBox;
        
        GamePanel() {
            selectionBox = new SelectionTool();
            setBackground(Color.WHITE);
            setPreferredSize(DIM_JFRAME);
            setFocusable(true);
            requestFocusInWindow();
            addKeyListener(new InGameKeyListener());
            addMouseListener(new BlockMouseListener());
            addMouseMotionListener(new BlockMouseListener());
            setVisible(true);
            
            Thread playerUpdater = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        player.step();
                        camera.update(player.getCenterPoint(), map.getMapDimension());
                        
                        if (player.getHP() <= 0) {
                            try {
                                Thread.sleep(3250);
                                camera.update(player.getCenterPoint(), map.getMapDimension());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } try {
                            Thread.sleep(30);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            
            playerUpdater.start();
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            //Draw the background
            g.setColor(new Color(117, 199, 250));
            g.fillRect(0, 0, 1500, 800);
            
            map.draw(g, camera.getPosPoint(), camera.DIM);
            
            // if close to player, change
            if (mousePoint != null) {
                selectionBox.draw(g, camera.toWorldCoordinates(mousePoint), camera.getPosPoint());
            }
            
            player.draw(g, camera.getPosPoint());
            
            //Display player's health
            if (player.getHP() > 0) {
                g.setColor(Color.BLACK);
            } else {
                g.setColor(Color.RED);
            } g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Health: " + player.getHP(), 20, 30);
            
            repaint();
        }
        
        /*
         * InGameKeyListener
         * This class is a KeyAdapter that handles all keyboard events
         * @author Matthew Sekirin
         */
        private class InGameKeyListener extends KeyAdapter {
            private final int KEY1_VALUE = KeyEvent.VK_1;
            
            /*
             * keyPressed
             * This method updates the player and the glassPane if necessary
             * @param e, a KeyEvent object
             */
            @Override
            public void keyPressed(KeyEvent e) {
                player.checkKeysPressed(e);
                
                if ((e.getKeyCode() >= KEY1_VALUE) && (e.getKeyCode() <= KEY1_VALUE + glassPane.getNumIcons())) {
                    glassPane.setSelection(e.getKeyCode() - KEY1_VALUE);
                }
            }
            
            /*
             * keyReleased
             * This method updates the player if necessary
             * @param e, a KeyEvent object
             */
            @Override
            public void keyReleased(KeyEvent e) {
                player.checkKeysReleased(e);
            }
        }
        
        /*
         * BlockMouseListener
         * This class is a MouseAdapter that handles all mouse events for the game.
         * Particularly, it deals with the mining and placing of blocks
         */
        private class BlockMouseListener extends MouseAdapter {
        	
        	/*
        	 * mousePressed
        	 * This method allows blocks to be removed or place according to the game logic
        	 * @param e, a MouseEvnet object
        	 */
            @Override
            public void mousePressed(MouseEvent e) {
                Point pointWorld = camera.toWorldCoordinates(e.getPoint());
                if (map.isFull(pointWorld)) {
                    inventory.addBlock(map.deleteBlock(pointWorld));
                } else if (!map.isFull(pointWorld) && (inventory.getHand() instanceof BlockItem)) {
                    map.newBlock(pointWorld, 'g');
                    inventory.getHand().use();
                }
                
                glassPane.update();
            }
            
            /*
             * mouseMoved
             * This method sets the mousePoint Point to the Point returned by the MouseEvent parameter
             * @param e, a MouseEvent object
             */
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePoint = e.getPoint();
            }
        }
    }
    
    /*
     * MyGlassPane
     * This class allows the user to see and interact with the Inventory
     */
    private class MyGlassPane extends JPanel {
        private SlotIcon[] slotIcons;
        private static final int NUM_ICONS = 5;
        
        MyGlassPane() {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            JPanel containerPanel = new JPanel();
            containerPanel.setOpaque(false);
            containerPanel.setLayout(new GridBagLayout());
            
            c.gridx = 0;
            slotIcons = new SlotIcon[NUM_ICONS];
            for (int i = 0; i < slotIcons.length; i++) {
                c.gridx++; 
                slotIcons[i] = new SlotIcon(i);
                
                containerPanel.add(slotIcons[i], c);
            }
            setSelection(0);
            
            c = new GridBagConstraints();
            c.gridx = 0;
            c.anchor = GridBagConstraints.PAGE_END;
            c.weighty = 1.0;
            c.insets = new Insets(0, 0, 10, 0);
            add(containerPanel, c);
        }
        
        /*
         * update
         * This method calls the setSelection() method and updates the slotIcons as necessary.
         */
        public void update() {
            setSelection(inventory.getIndexHand()); 
            
            int inventoryIndex = 0;
            for (int i = 0; i < slotIcons.length; i++) {
                if ((inventory.getItem(i) == null) && (slotIcons[i].getMyIcon() != null)) {
                    slotIcons[i].setMyIcon(null);
                    inventoryIndex++; // allows the slots to the right to have their items shifted
                }
                if (inventory.getItem(inventoryIndex) == null) {
                    return;
                }
                slotIcons[i].setMyIcon(inventory.getItem(inventoryIndex++).getIconImage());
            }
        }
        
        /*
         * setSelection
         * This method updates the inventory and sets the selection to the value of the index parameter 
         * by changing the appropriate borders
         * @param index, the index of the slotIcon to be selected
         */
        public void setSelection(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("Index out of bounds");
            }
            inventory.update(index);
            
            try {
                slotIcons[index].setBorder(ImageIO.read(new File("Sprites/slotBorderSelected.png")), new Point(5, 5));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            for (int i = 0; i < slotIcons.length; i++) {
                if (i != index) {
                    slotIcons[i].setDefaultBorder();
                }
            }
        }
        
        /*
         * getNumIcons
         * This method returns the NUM_ICONS field
         * @return NUM_ICONS, the number of icons in the inventory
         */
        public int getNumIcons() {
            return NUM_ICONS;
        }
        
        /*
         * SlotIcon
         * This class represents one slot capable of storing one type of item
         */
        private class SlotIcon extends JLabel {
            private BufferedImage background, border;
            private BufferedImage icon;
            private Point backgroundTopLeft;
            
            SlotIcon(int index) {
                try {
                    this.background = ImageIO.read(new File("Sprites/slotBackground.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setDefaultBorder();
                
                Insets borderInsets;
                if ((index != 0) && (index != slotIcons.length - 1)) { // border is different if SlotIcon is on the ends
                    borderInsets = new Insets(1, 0, 1, 0);
                } else if (index == 0) {
                    borderInsets = new Insets(1, 1, 1, 0);
                } else {
                    borderInsets = new Insets(1, 0, 1, 1);
                }
                
                setBorder(new MatteBorder(borderInsets, Color.BLACK));
            }
            
            /*
             * setDefaultBorder 
             * This method loads the default border from a file and calls
             * updateImage()
             */
            public void setDefaultBorder() {
                try {
                    this.border = ImageIO.read(new File("Sprites/slotBorderNormal.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                backgroundTopLeft = new Point(3, 3);
                updateImage();
            }
            
            /*
             * setMyIcon
             * This method sets the icon and calls updateImage()
             * @param icon, the BufferedImage representing the inner icon
             */
            public void setMyIcon(BufferedImage icon) {
                this.icon = icon;
                updateImage();
            }
            
            /*
             * getMyIcon
             * This method returns the icon
             */
            public BufferedImage getMyIcon() {
                return icon;
            }
            
            /*
             * setBorder
             * This method sets the border and calls updateImage()
             * @param border, a BufferedImage representing the border
             * @param backgroundTopLeft, the Point where the background should be placed relative to the
             * border
             */
            public void setBorder(BufferedImage border, Point backgroundTopLeft) {
                this.border = border;
                this.backgroundTopLeft = backgroundTopLeft;
                updateImage();
            }
            
            /*
             * updateImage
             * This method combines the border, background and images and sets the result as
             * an icon of the JLabel
             */
            private void updateImage() {
                BufferedImage resultingImage = new BufferedImage(border.getWidth(), border.getHeight(),
                                                                 BufferedImage.TYPE_INT_ARGB);
                
                Graphics2D g = resultingImage.createGraphics();
                g.drawImage(border, 0, 0, null);
                g.drawImage(background, backgroundTopLeft.x, backgroundTopLeft.y, null);
                if (icon != null) {
                    Point iconTopLeft = new Point(backgroundTopLeft.x + (background.getWidth() - icon.getWidth()) / 2,
                                                  backgroundTopLeft.y + (background.getHeight() - icon.getHeight()) / 2);
                    g.drawImage(icon, iconTopLeft.x, iconTopLeft.y, null);
                }
                g.dispose();
                
                setIcon(new ImageIcon(resultingImage));
            }
        }
    }
}
