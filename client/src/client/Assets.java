package client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Assets {

    private static Font font;
    private static BufferedImage errorImage = null;

    public static String getResourcesPath() {
        return Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static File getResourceFile(String name) {
        return new File(getResourcesPath() + name);
    }

    public static Font getFont() {
        if (font == null) {
            try {
                font = Font.createFont(Font.TRUETYPE_FONT, new File(getResourcesPath() + "GloriaHallelujah.ttf")).deriveFont(30.0f);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                font = new Font(Font.SERIF, Font.BOLD, 30);
            }
        }
        return font;
    }

    public static BufferedImage getErrorImage() {
        if (errorImage == null) {
            errorImage = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics g = errorImage.getGraphics();
            g.setColor(Color.RED);
            g.fillRect(0, 0, errorImage.getWidth(), errorImage.getHeight());
        }
        return errorImage;
    }

    public static BufferedImage loadImage(String name) {
        String path = getResourcesPath() +  name;
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.out.println("Could not load asset: " + path);
        }
        return getErrorImage();
    }

    public static BufferedImage getTile(BufferedImage map, int tileX, int tileY, int tilesX, int tilesY, int numTiles) {
        int width = map.getWidth();
        int height = map.getHeight();
        return map.getSubimage((width * tileX) / numTiles, (height * tileY) / numTiles, (width * tilesX) / numTiles, (height * tilesY) / numTiles);
    }

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

}
