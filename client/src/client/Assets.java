package client;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A static utility class to access different resources throughout the project.
 *
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class Assets {

    public static int MENU_WHAM = 0;
    public static int MENU_ROCKET = 1;
    public static int MENU_FLAME0 = 2;
    public static int MENU_FLAME1 = 3;
    public static int MENU_BLOCK = 4;
    public static int MENU_LEFT_ARROW = 5;
    public static int MENU_RIGHT_ARROW = 6;

    private static Font font;
    private static BufferedImage errorImage = null;

    private static Image[] playerIcons;
    private static Image[] mainmenyIcons;

    /**
     *
     * @return The path to the location of the resources.
     */
    public static String getResourcesPath() {
        return Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }


    /**
     *
     * @return The default font that should be used in the project.
     */
    public static Font getFont() {
        if (font == null) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("/GloriaHallelujah.ttf")).deriveFont(30.0f);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                font = new Font(Font.SERIF, Font.BOLD, 30);
            }
        }
        return font;
    }

    /**
     *
     * @return Returns a transparent 128x128 image.
     */
    public static BufferedImage getErrorImage() {
        if (errorImage == null) {
            errorImage = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = errorImage.getGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, errorImage.getWidth(), errorImage.getHeight());
        }
        return errorImage;
    }

    /**
     *
     * @param name The filename of the image to load.
     * @return On success the loaded image else getErrorImage.
     */
    public static BufferedImage loadImage(String name) {
        try {
            return ImageIO.read(Main.class.getResource("/"+name));
        } catch (IOException e) {
            System.out.println("Could not load asset: " + name);
        }
        return getErrorImage();
    }

    /**
     * Returns a subimage of the BufferedImage.
     * @param map The source image.
     * @param tileX The position in tiles of the top left corner of the tile.
     * @param tileY The position in tiles of the top left corner of the tile.
     * @param tilesX The width of the tile in tiles.
     * @param tilesY The height of the tile in tiles.
     * @param numTiles The total height and width of the image in tiles.
     * @return The subimage.
     */
    public static BufferedImage getTile(BufferedImage map, int tileX, int tileY, int tilesX, int tilesY, int numTiles) {
        int width = map.getWidth();
        int height = map.getHeight();
        return map.getSubimage((width * tileX) / numTiles, (height * tileY) / numTiles, (width * tilesX) / numTiles, (height * tilesY) / numTiles);
    }

    /**
     * Returns a subimage of the BufferedImage.
     * @param map The source image.
     * @param startTileX The start position in tiles of the top left corner of the tile.
     * @param startTileY The start position in tiles of the top left corner of the tile.
     * @param tilesX The width of the tile in tiles.
     * @param tilesY The height of the tile in tiles.
     * @param gridSize The total height and width of the image in tiles.
     * @param advanceTilesX Number of tiles to advance horizontally with, if x >= gridSize it
     *                      advances with advanceTilesY and resets x to startTileX.
     * @param advanceTilesY Number of tiles to advance vertically with.
     * @param count The total times to advance.
     * @return Returns an array of length count with all the sub images.
     */
    public static Image[] getTiles(BufferedImage map, int startTileX, int startTileY, int tilesX, int tilesY, int gridSize, int advanceTilesX, int advanceTilesY, int count) {
        Image[] result = new Image[count];
        int startX = (map.getWidth() * startTileX) / gridSize;
        int startY = (map.getHeight() * startTileY) / gridSize;
        int width = (map.getWidth() * tilesX) / gridSize;
        int height = (map.getHeight() * tilesY) / gridSize;
        int advanceX = (map.getWidth() * advanceTilesX) / gridSize;
        int advanceY = (map.getHeight() * advanceTilesY) / gridSize;
        int x = startX;
        int y = startY;
        for (int i = 0; i < count; i++) {
            result[i] = map.getSubimage(x, y, width, height);
            x += advanceX;
            if (x + width > map.getWidth()) {
                x = startX;
                y += advanceY;
            }
        }
        return result;
    }

    /**
     * Return an array of the player icons
     * @return
     */
    public static Image getAvatarImage(int avatarId) {
        if (playerIcons == null) {
            BufferedImage tileMap = Assets.loadImage("player-icons.png");
            playerIcons = Assets.getTiles(tileMap, 0, 0, 1, 1, 8, 4, 1, 16);
        }
        avatarId = Math.max(Math.min(avatarId, playerIcons.length - 1), 0);
        return playerIcons[avatarId];
    }

    public static Image getMainmenuIcon(int icon) {
        if (mainmenyIcons == null) {
            mainmenyIcons = new Image[7];
            BufferedImage tileMap = Assets.loadImage("mainmenu.png");
            mainmenyIcons[MENU_WHAM] = Assets.getTile(tileMap, 0, 0, 3, 1, 8);
            mainmenyIcons[MENU_LEFT_ARROW] = Assets.getTile(tileMap, 0, 4, 1, 1,8);
            mainmenyIcons[MENU_RIGHT_ARROW] = Assets.getTile(tileMap, 1, 4, 1, 1, 8);
            mainmenyIcons[MENU_ROCKET] = Assets.getTile(tileMap, 0, 1, 6, 2, 8);
            mainmenyIcons[MENU_FLAME0] = Assets.getTile(tileMap, 0, 3, 1, 1, 8);
            mainmenyIcons[MENU_FLAME1] = Assets.getTile(tileMap, 1, 3, 1, 1, 8);
            mainmenyIcons[MENU_BLOCK] = Assets.getTile(tileMap, 4, 3, 4, 5, 8);
        }
        return mainmenyIcons[icon];
    }



}
