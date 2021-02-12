package client;

import client.ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class MainMenu extends JPanel {

    private boolean hasPlayedQuitAnimation = false;
    private Image wham;
    private Image rocket, flame0, flame1, block;

    MainMenu() {
        super(new CardLayout());

        try {
            BufferedImage spriteSheet = ImageIO.read(new File("c:/users/spankarn/dropbox/data/dat055/mainmenu.png"));
            wham = spriteSheet.getSubimage(0, 0, 128 * 3, 128);
            rocket = spriteSheet.getSubimage(0, 128, 768, 256);
            flame0 = spriteSheet.getSubimage(0, 384, 128, 128);
            flame1 = spriteSheet.getSubimage(128, 384, 128, 128);
            block = spriteSheet.getSubimage(512, 384, 512, 640);
        } catch (IOException e) {
            e.printStackTrace();
        }

        initMainMenu();
        initJoinGamePanel();
    }

    private void initMainMenu() {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 0));

        AwesomeText title = new AwesomeText("Hello!");
        AwesomeButton joinGameButton = new AwesomeButton("Join Game", wham);
        AwesomeButton createGameButton = new AwesomeButton("Create Game", wham);
        AwesomeButton quitButton = new AwesomeButton("Quit", rocket);
        AwesomeImage rocketFlame = new AwesomeImage(flame0);
        rocketFlame.setVisible(false);

        joinGameButton.setFontFactor(.8f);
        createGameButton.setFontFactor(.8f);
        quitButton.setFontFactor(.4f);

        AwesomeUtil.wiggleOnHover(joinGameButton, (float)Math.PI * .1f);
        AwesomeUtil.scaleOnHover(createGameButton, 1.3f);

        joinGameButton.addActionListener(e -> {
            ((CardLayout)getLayout()).last(this);
        });

        quitButton.addActionListener(e -> {
            if (!hasPlayedQuitAnimation) {
                AwesomeEffect.Builder builder = AwesomeEffect.create();
                builder .addTranslationXKey(getWidth(), 1000)
                        .addTranslationXKey(-getWidth(), 1001)
                        .addTranslationXKey(0, 2000).animate(quitButton);
                for (int i = 0; i < 10; i++) {
                    builder.addSpriteKey(flame0, i * 200);
                    builder.addSpriteKey(flame1, i * 200 + 100);
                }
                builder.animate(rocketFlame);
                rocketFlame.setVisible(true);
                hasPlayedQuitAnimation = true;
            } else {
                System.exit(0);
            }
        });

        panel.add(title);
        panel.add(joinGameButton);
        panel.add(createGameButton);
        panel.add(quitButton);
        panel.add(rocketFlame);

        layout.getConstraints(title).setPosition(0.5f, 0.15f).setSize(.5f, .2f);
        layout.getConstraints(joinGameButton).setPosition(0.25f, 0.4f).setSize(.4f, .5f);
        layout.getConstraints(createGameButton).setPosition(0.75f, 0.4f).setSize(.4f, .5f);
        layout.getConstraints(quitButton).setPosition(0.5f, 0.7f).setSize(.6f, .5f);
        layout.getConstraints(rocketFlame).setPosition(0.28f, 0.7f).setSize(.1f, 1.0f);

        add(panel);
    }

    private void initJoinGamePanel() {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(true);

        AwesomeImage bg = new AwesomeImage(block);
        AwesomeText code = new AwesomeText("Enter code");
        AwesomeButton accept = new AwesomeButton("Go!");
        AwesomeButton back = new AwesomeButton("Back");
        JTextField input = new JTextField();
        input.setFont(AwesomeUtil.getFont().deriveFont(64.0f));
        input.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(back);
        panel.add(accept);
        panel.add(input);
        panel.add(code);
        panel.add(bg);

        layout.getConstraints(back).setPosition(0.3f, 0.7f).setSize(0.3f, 0.5f);
        layout.getConstraints(accept).setPosition(0.7f, 0.7f).setSize(0.3f, 0.5f);
        layout.getConstraints(code).setPosition(0.5f, 0.3f).setSize(0.8f, 0.2f);
        layout.getConstraints(input).setPosition(0.5f, 0.5f).setSize(0.8f, 0.2f);
        layout.getConstraints(bg).setPosition(0.5f, 0.5f).setSize(1.0f, 1.0f);

        AwesomeUtil.wiggleOnHover(back, (float)Math.PI * .1f);
        AwesomeUtil.wiggleOnHover(accept, (float)Math.PI * .1f);

        back.addActionListener(e -> {
            ((CardLayout)getLayout()).first(this);
        });

        add(panel);
    }

    public void initCreateGamePanel() {

    }
}
