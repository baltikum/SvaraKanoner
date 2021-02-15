package client;

import client.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import common.GameSettings;

public class MainMenu extends JPanel {

    private boolean hasPlayedQuitAnimation = false;
    private Image wham, leftArrow, rightArrow;
    private Image rocket, flame0, flame1, block;

    private GameSettings gameSettings = new GameSettings();

    MainMenu() {
        super(new CardLayout());
        setBackground(new Color(0xe67e22));


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
        panel.setBackground(new Color(0xe67e22));

        AwesomeText title = new AwesomeText("Hello!", AwesomeUtil.BIG_TEXT);
        AwesomeButton joinGameButton = new AwesomeButton("Join Game", wham, AwesomeUtil.MEDIUM_TEXT);
        AwesomeButton createGameButton = new AwesomeButton("Create Game", wham, AwesomeUtil.MEDIUM_TEXT);
        AwesomeButton quitButton = new AwesomeButton("Quit", rocket, AwesomeUtil.BIG_TEXT);
        AwesomeImage rocketFlame = new AwesomeImage(flame0);
        rocketFlame.setVisible(false);

        AwesomeUtil.wiggleOnHover(joinGameButton, 10.0f);
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

        layout.setConstraintsRatioByWidth(title, 0.5f, 0.15f, .5f, .2f);
        layout.setConstraintsRatioByWidth(joinGameButton, 0.25f, 0.4f, .4f, .5f);
        layout.setConstraintsRatioByWidth(createGameButton, 0.75f, 0.4f, .4f, .5f);
        layout.setConstraintsRatioByWidth(quitButton, 0.5f, 0.7f, .6f, .5f);
        layout.setConstraintsRatioByWidth(rocketFlame, 0.28f, 0.7f, .1f, 1.0f);

        add(panel);
    }

    private void initJoinGamePanel() {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0xe67e22));

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

        layout.setConstraintsRatioByWidth(back, 0.3f, 0.8f, 0.3f, 0.5f);
        layout.setConstraintsRatioByWidth(accept, 0.7f, 0.8f, 0.3f, 0.5f);
        layout.setConstraintsRatioByWidth(code, 0.5f, 0.3f, 0.8f, 0.2f);
        layout.setConstraintsRatioByWidth(input, 0.5f, 0.5f, 0.8f, 0.2f);
        layout.setConstraintsRatioByWidth(bg, 0.5f, 0.5f, 1.0f, 1.0f);

        AwesomeUtil.wiggleOnHover(back, 10.0f);
        AwesomeUtil.wiggleOnHover(accept, 10.0f);

        back.addActionListener(e -> {
            ((CardLayout)getLayout()).previous(this);
        });

        add(panel);
    }

    public void initCreateGamePanel() {
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0xe67e22));

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
        layout.setConstraintsRatioByWidth(maxPlayersLabel, 0.3f, 0.15f, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(increaseMaxPlayers, 0.9f, 0.15f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(decreaseMaxPlayers, 0.6f, 0.15f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(maxPlayers, 0.75f, 0.15f, 0.2f, 0.5f);

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
        layout.setConstraintsRatioByWidth(numRoundsLabel, 0.3f, 0.25f, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(decrease, 0.6f, 0.25f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(increase, 0.9f, 0.25f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(numRounds, 0.75f, 0.25f, 0.2f, 0.5f);

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
        layout.setConstraintsRatioByWidth(drawTimeLabel, 0.3f, 0.35f, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(drawTimeDecrease, 0.6f, 0.35f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(drawTimeIncrease, 0.9f, 0.35f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(drawTime, 0.75f, 0.35f, 0.2f, 0.5f);

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
        layout.setConstraintsRatioByWidth(guessTimeLabel, 0.3f, 0.45f, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(guessTimeDecrease, 0.6f, 0.45f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(guessTimeIncrease, 0.9f, 0.45f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(guessTime, 0.75f, 0.45f, 0.2f, 0.5f);

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
        layout.setConstraintsRatioByWidth(getChoicesLabel, 0.3f, 0.55f, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(choicesCountIncrease, 0.9f, 0.55f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(choicesCountDecrease, 0.6f, 0.55f, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(getChoices, 0.75f, 0.55f, 0.2f, 0.5f);

        AwesomeButton create = new AwesomeButton("Create", AwesomeUtil.BIG_TEXT);
        AwesomeButton back = new AwesomeButton("Back", AwesomeUtil.BIG_TEXT);
        panel.add(create);
        panel.add(back);
        layout.setConstraintsRatioByWidth(create, 0.75f, 0.8f, 0.3f, 0.5f);
        layout.setConstraintsRatioByWidth(back, 0.25f, 0.8f, 0.3f, 0.5f);
        create.addActionListener(e -> System.out.println("You created a server! (almost)"));
        back.addActionListener(e -> ((CardLayout)getLayout()).first(this));
        AwesomeUtil.wiggleOnHover(create, 10.0f);
        AwesomeUtil.wiggleOnHover(back, 10.0f);

        add(panel);
    }
}
