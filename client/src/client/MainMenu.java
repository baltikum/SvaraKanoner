package client;

import client.ui.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainMenu extends AwesomePanel {

    private Image wham;
    private Image rocket, flame0, flame1;

    MainMenu() {
        super();
        setLayout(null);

        setBackground(new Color(0xe67e22));

        try {
            BufferedImage spriteSheet = ImageIO.read(new File("c:/users/spankarn/dropbox/data/dat055/mainmenu.png"));
            wham = spriteSheet.getSubimage(0, 0, 128 * 3, 128);
            rocket = spriteSheet.getSubimage(0, 128, 768, 256);
            flame0 = spriteSheet.getSubimage(0, 384, 128, 128);
            flame1 = spriteSheet.getSubimage(128, 384, 128, 128);
            System.out.println(spriteSheet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AwesomeButton joinGameButton = new AwesomeButton("Join Game", wham);
        AwesomeButton createGameButton = new AwesomeButton("Create Game", wham);
        AwesomeButton quitButton = new AwesomeButton("Quit", rocket);
        AwesomeText title = new AwesomeText("Hello!");

        joinGameButton.setFontFactor(.8f);
        createGameButton.setFontFactor(.8f);
        quitButton.setFontFactor(.4f);

        quitButton.addActionListener(e -> {
            AwesomeEffect.create()
                    .animateSprite(0, 384, 128, 128, 100)
                    .animateSprite(128, 384, 128, 128, 100)
                    .animateX(getWidth(), 1000).animate(quitButton);
            quitButton.setEnabled(false);
        });

        add(joinGameButton, 0.05f, 0.3f, 0.4f, 0.2f);
        add(createGameButton, 0.55f, 0.3f, 0.4f, 0.2f);
        add(quitButton, 0.2f, 0.6f, 0.6f, 0.3f);
        add(title, 0.2f, 0.05f, 0.6f, 0.2f);
    }
}
