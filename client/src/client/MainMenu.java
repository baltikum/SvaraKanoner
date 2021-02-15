package client;

import client.ui.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import common.GameSettings;

public class MainMenu extends JPanel {

    private boolean hasPlayedQuitAnimation = false;
    private Image wham, leftArrow, rightArrow;
    private Image rocket, flame0, flame1, block;

    private GameSettings gameSettings = new GameSettings();

    MainMenu() {
        super(new CardLayout());

        BufferedImage tileMap = Assets.loadImage("mainmenu.png");
        wham = Assets.getTile(tileMap, 0, 0, 3, 1, 8);
        leftArrow = Assets.getTile(tileMap, 0, 4, 1, 1,8);
        rightArrow = Assets.getTile(tileMap, 1, 4, 1, 1, 8);
        rocket = Assets.getTile(tileMap, 0, 1, 6, 2, 8);
        flame0 = Assets.getTile(tileMap, 0, 3, 1, 1, 8);
        flame1 = Assets.getTile(tileMap, 1, 3, 1, 1, 8);
        block = Assets.getTile(tileMap, 4, 3, 4, 5, 8);

        initMainMenu();
        initJoinGamePanel();
        initCreateGamePanel();
    }

    private void initMainMenu() {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setOpaque(true);
        panel.setBackground(new Color(0, 0, 0, 0));

        AwesomeText title = new AwesomeText("Hello!", AwesomeUtil.BIG_TEXT);
        AwesomeButton joinGameButton = new AwesomeButton("Join Game", wham, AwesomeUtil.MEDIUM_TEXT);
        AwesomeButton createGameButton = new AwesomeButton("Create Game", wham, AwesomeUtil.MEDIUM_TEXT);
        AwesomeButton quitButton = new AwesomeButton("Quit", rocket, AwesomeUtil.BIG_TEXT);
        AwesomeImage rocketFlame = new AwesomeImage(flame0);
        rocketFlame.setVisible(false);

        AwesomeUtil.wiggleOnHover(joinGameButton, (float)Math.PI * .1f);
        AwesomeUtil.scaleOnHover(createGameButton, 1.3f);

        joinGameButton.addActionListener(e -> ((CardLayout)getLayout()).next(this) );
        createGameButton.addActionListener(e -> ((CardLayout)getLayout()).last(this) );

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
        AwesomeText code = new AwesomeText("Enter code", AwesomeUtil.BIG_TEXT);
        AwesomeButton accept = new AwesomeButton("Go!", AwesomeUtil.BIG_TEXT);
        AwesomeButton back = new AwesomeButton("Back", AwesomeUtil.BIG_TEXT);
        JTextField input = new JTextField();
        input.setFont(AwesomeUtil.getFont(AwesomeUtil.BIG_TEXT));
        input.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(back);
        panel.add(accept);
        panel.add(input);
        panel.add(code);
        panel.add(bg);

        layout.getConstraints(back).setPosition(0.3f, 0.8f).setSize(0.3f, 0.5f);
        layout.getConstraints(accept).setPosition(0.7f, 0.8f).setSize(0.3f, 0.5f);
        layout.getConstraints(code).setPosition(0.5f, 0.3f).setSize(0.8f, 0.2f);
        layout.getConstraints(input).setPosition(0.5f, 0.5f).setSize(0.8f, 0.2f);
        layout.getConstraints(bg).setPosition(0.5f, 0.5f).setSize(1.0f, 1.0f);

        AwesomeUtil.wiggleOnHover(back, (float)Math.PI * .1f);
        AwesomeUtil.wiggleOnHover(accept, (float)Math.PI * .1f);

        back.addActionListener(e -> {
            ((CardLayout)getLayout()).previous(this);
        });

        add(panel);
    }

    public void initCreateGamePanel() {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(true);

        AwesomeText maxPlayersLabel = new AwesomeText("MAX PLAYERS:");
        AwesomeButton increaseMaxPlayers = new AwesomeButton(rightArrow);
        AwesomeButton decreaseMaxPlayers = new AwesomeButton(leftArrow);
        AwesomeText maxPlayers = new AwesomeText(String.valueOf(gameSettings.getMaxPlayers()));
        increaseMaxPlayers.addActionListener(e -> {
            if (gameSettings.setMaxPlayers(gameSettings.getMaxPlayers() + 1)) {
                maxPlayers.setText(String.valueOf(gameSettings.getMaxPlayers()));
            } else {
                AwesomeUtil.shakeHorizontally(maxPlayers, 20);
            }
        });
        decreaseMaxPlayers.addActionListener(e -> {
            if (gameSettings.setMaxPlayers(gameSettings.getMaxPlayers() - 1)) {
                maxPlayers.setText(String.valueOf(gameSettings.getMaxPlayers()));
            } else {
                AwesomeUtil.shakeHorizontally(maxPlayers, 20);
            }
        });
        panel.add(maxPlayersLabel);
        panel.add(increaseMaxPlayers);
        panel.add(decreaseMaxPlayers);
        panel.add(maxPlayers);
        layout.getConstraints(maxPlayersLabel).setPosition(0.3f, 0.15f).setSize(0.4f, 0.25f);
        layout.getConstraints(increaseMaxPlayers).setPosition(0.9f, 0.15f).setSize(0.1f, 1.0f);
        layout.getConstraints(decreaseMaxPlayers).setPosition(0.6f, 0.15f).setSize(0.1f, 1.0f);
        layout.getConstraints(maxPlayers).setPosition(0.75f, 0.15f).setSize(0.2f, 0.5f);

        AwesomeText numRoundsLabel = new AwesomeText("ROUNDS:");
        AwesomeButton increase = new AwesomeButton(rightArrow);
        AwesomeButton decrease = new AwesomeButton(leftArrow);
        AwesomeText numRounds = new AwesomeText(String.valueOf(gameSettings.getNumRounds()));
        increase.addActionListener(e -> {
            if (gameSettings.setRounds(gameSettings.getNumRounds() + 1)) {
                numRounds.setText(String.valueOf(gameSettings.getNumRounds()));
            } else {
                AwesomeUtil.shakeHorizontally(numRounds, 20);
            }
        });
        decrease.addActionListener(e -> {
            if (gameSettings.setRounds(gameSettings.getNumRounds() - 1)) {
                numRounds.setText(String.valueOf(gameSettings.getNumRounds()));
            } else {
                AwesomeUtil.shakeHorizontally(numRounds, 20);
            }
        });
        panel.add(numRoundsLabel);
        panel.add(increase);
        panel.add(decrease);
        panel.add(numRounds);
        layout.getConstraints(numRoundsLabel).setPosition(0.3f, 0.25f).setSize(0.4f, 0.25f);
        layout.getConstraints(decrease).setPosition(0.6f, 0.25f).setSize(0.1f, 1.0f);
        layout.getConstraints(increase).setPosition(0.9f, 0.25f).setSize(0.1f, 1.0f);
        layout.getConstraints(numRounds).setPosition(0.75f, 0.25f).setSize(0.2f, 0.5f);

        AwesomeText drawTimeLabel = new AwesomeText("DRAW TIME:");
        AwesomeButton drawTimeIncrease = new AwesomeButton(rightArrow);
        AwesomeButton drawTimeDecrease = new AwesomeButton(leftArrow);
        AwesomeText drawTime = new AwesomeText(String.valueOf(gameSettings.getDrawTimeMilliseconds() / 1000L));
        drawTimeIncrease.addActionListener(e -> {
            int newSeconds = (int)(gameSettings.getDrawTimeMilliseconds() / 1000L) + 5;
            if (gameSettings.setDrawTime(newSeconds)) {
                drawTime.setText(String.valueOf(newSeconds));
            } else {
                AwesomeUtil.shakeHorizontally(drawTime, 20);
            }
        });
        drawTimeDecrease.addActionListener(e -> {
            int newSeconds = (int)(gameSettings.getDrawTimeMilliseconds() / 1000L) - 5;
            if (gameSettings.setDrawTime(newSeconds)) {
                drawTime.setText(String.valueOf(newSeconds));
            } else {
                AwesomeUtil.shakeHorizontally(drawTime, 20);
            }
        });
        panel.add(drawTimeLabel);
        panel.add(drawTimeIncrease);
        panel.add(drawTimeDecrease);
        panel.add(drawTime);
        layout.getConstraints(drawTimeLabel).setPosition(0.3f, 0.35f).setSize(0.4f, 0.25f);
        layout.getConstraints(drawTimeDecrease).setPosition(0.6f, 0.35f).setSize(0.1f, 1.0f);
        layout.getConstraints(drawTimeIncrease).setPosition(0.9f, 0.35f).setSize(0.1f, 1.0f);
        layout.getConstraints(drawTime).setPosition(0.75f, 0.35f).setSize(0.2f, 0.5f);

        AwesomeText guessTimeLabel = new AwesomeText("GUESS TIME:");
        AwesomeButton guessTimeIncrease = new AwesomeButton(rightArrow);
        AwesomeButton guessTimeDecrease = new AwesomeButton(leftArrow);
        AwesomeText guessTime = new AwesomeText(String.valueOf(gameSettings.getGuessTimeMilliseconds() / 1000L));
        guessTimeIncrease.addActionListener(e -> {
            int newSeconds = (int)(gameSettings.getGuessTimeMilliseconds() / 1000L) + 5;
            if (gameSettings.setGuessTime(newSeconds)) {
                guessTime.setText(String.valueOf(newSeconds));
            } else {
                AwesomeUtil.shakeHorizontally(guessTime, 20);
            }
        });
        guessTimeDecrease.addActionListener(e -> {
            int newSeconds = (int)(gameSettings.getGuessTimeMilliseconds() / 1000L) - 5;
            if (gameSettings.setGuessTime(newSeconds)) {
                guessTime.setText(String.valueOf(newSeconds));
            } else {
                AwesomeUtil.shakeHorizontally(guessTime, 20);
            }
        });
        panel.add(guessTimeLabel);
        panel.add(guessTimeIncrease);
        panel.add(guessTimeDecrease);
        panel.add(guessTime);
        layout.getConstraints(guessTimeLabel).setPosition(0.3f, 0.45f).setSize(0.4f, 0.25f);
        layout.getConstraints(guessTimeDecrease).setPosition(0.6f, 0.45f).setSize(0.1f, 1.0f);
        layout.getConstraints(guessTimeIncrease).setPosition(0.9f, 0.45f).setSize(0.1f, 1.0f);
        layout.getConstraints(guessTime).setPosition(0.75f, 0.45f).setSize(0.2f, 0.5f);

        AwesomeText getChoicesLabel = new AwesomeText("CHOICES ENABLED:");
        AwesomeButton choicesCountIncrease = new AwesomeButton(rightArrow);
        AwesomeButton choicesCountDecrease = new AwesomeButton(leftArrow);
        AwesomeText getChoices = new AwesomeText(gameSettings.getChooseWords() ? "Yes" : "No");
        choicesCountIncrease.addActionListener(e -> { if (!gameSettings.getChooseWords())  { gameSettings.toggleChooseWords(); getChoices.setText("Yes"); } } );
        choicesCountDecrease.addActionListener(e -> { if (gameSettings.getChooseWords()) { gameSettings.toggleChooseWords(); getChoices.setText("No"); } } );
        panel.add(getChoicesLabel);
        panel.add(choicesCountIncrease);
        panel.add(choicesCountDecrease);
        panel.add(getChoices);
        layout.getConstraints(getChoicesLabel).setPosition(0.3f, 0.55f).setSize(0.4f, 0.25f);
        layout.getConstraints(choicesCountIncrease).setPosition(0.9f, 0.55f).setSize(0.1f, 1.0f);
        layout.getConstraints(choicesCountDecrease).setPosition(0.6f, 0.55f).setSize(0.1f, 1.0f);
        layout.getConstraints(getChoices).setPosition(0.75f, 0.55f).setSize(0.2f, 0.5f);

        AwesomeButton create = new AwesomeButton("Create", AwesomeUtil.BIG_TEXT);
        AwesomeButton back = new AwesomeButton("Back", AwesomeUtil.BIG_TEXT);
        panel.add(create);
        panel.add(back);
        layout.getConstraints(create).setPosition(0.75f, 0.8f).setSize(0.3f, 0.5f);
        layout.getConstraints(back).setPosition(0.25f, 0.8f).setSize(0.3f, 0.5f);
        create.addActionListener(e -> System.out.println("You created a server! (almost)"));
        back.addActionListener(e -> ((CardLayout)getLayout()).first(this));
        AwesomeUtil.wiggleOnHover(create, 0.1f);
        AwesomeUtil.wiggleOnHover(back, 0.1f);

        add(panel);
    }
}
