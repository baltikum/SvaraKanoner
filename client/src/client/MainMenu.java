package client;

import client.ui.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import common.*;

/**
 * Represents the main menu ui and logic. This is the first phase the game starts in.
 *
 * Can send a CREATE_GAME or JOIN_GAME message to the server and enters join phase on successfull response.
 */
public class MainMenu extends JPanel {

    private boolean hasPlayedQuitAnimation = false;
    private final Image wham, leftArrow, rightArrow;
    private final Image rocket, flame0, flame1, block;
    private final Game game;
    private final GameSettings gameSettings = new GameSettings();

    /**
     * Initiates a MainMenu phase and sets up the ui.
     */
    public MainMenu(Game game) {
        super(new CardLayout());
        this.game = game;
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


    /**
     * Add a panel as a card to root with the main menu buttons.
     */
    private void initMainMenu() {
        game.getChat().setVisible(false);
        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0xe67e22));
        this.add(panel);

        AwesomeText title = new AwesomeText("Hello!");
        AwesomeButton joinGameButton = new AwesomeButton("Join Game", wham);
        AwesomeButton createGameButton = new AwesomeButton("Create Game", wham);
        AwesomeButton quitButton = new AwesomeButton("Quit", rocket);
        AwesomeImage rocketFlame = new AwesomeImage(flame0);
        rocketFlame.setVisible(false);

        title.setTextColor(Color.RED);

        AwesomeUtil.dynamicFont(title, 1.0f);
        AwesomeUtil.dynamicFont(joinGameButton, .2f);
        AwesomeUtil.dynamicFont(createGameButton, .2f);
        AwesomeUtil.dynamicFont(quitButton, .3f);

        AwesomeUtil.wiggleOnHover(joinGameButton, 10.0f);
        AwesomeUtil.scaleOnHover(createGameButton, 1.3f);

        joinGameButton.addActionListener(e -> ((CardLayout)this.getLayout()).next(this) );
        createGameButton.addActionListener(e -> ((CardLayout)this.getLayout()).last(this) );

        quitButton.addActionListener(e -> {
            if (!hasPlayedQuitAnimation) {
                AwesomeEffect.Builder builder = AwesomeEffect.create();
                builder .addTranslationXKey(this.getWidth(), 1000)
                        .addTranslationXKey(-this.getWidth(), 1001)
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

        Settings settings = Settings.getSettings();
        AwesomeButton nextIcon = new AwesomeButton(rightArrow);
        AwesomeButton prevIcon = new AwesomeButton(leftArrow);
        AwesomeImage playerIcon = new AwesomeImage(Assets.getPlayerIcons()[settings.getPreferredAvatarId()]);
        nextIcon.addActionListener( e -> settings.nextPreferredIcon());
        prevIcon.addActionListener( e -> settings.prevPreferredIcon());

        panel.add(nextIcon);
        panel.add(prevIcon);
        panel.add(playerIcon);
        layout.setConstraintsRatioByWidth(prevIcon, 0.1f, 0.1f, 0.05f, 1.0f);
        layout.setConstraintsRatioByWidth(playerIcon, 0.15f, 0.1f, 0.05f, 1.0f);
        layout.setConstraintsRatioByWidth(nextIcon, 0.2f, 0.1f, 0.05f, 1.0f);
        settings.addListener((property, clientSettings) -> playerIcon.setImage(Assets.getPlayerIcons()[clientSettings.getPreferredAvatarId()]));

        panel.add(title);
        panel.add(joinGameButton);
        panel.add(createGameButton);
        panel.add(quitButton);
        panel.add(rocketFlame);

        layout.setConstraintsRatioByWidth(title, 0.5f, 0.10f, .5f, .2f);
        layout.setConstraintsRatioByWidth(joinGameButton, 0.25f, 0.4f, .4f, .5f);
        layout.setConstraintsRatioByWidth(createGameButton, 0.75f, 0.4f, .4f, .5f);
        layout.setConstraintsRatioByWidth(quitButton, 0.5f, 0.7f, .6f, .5f);
        layout.setConstraintsRatioByWidth(rocketFlame, 0.28f, 0.7f, .1f, 1.0f);
    }

    /**
     * Add a panel as a card to root with the join game inputs.
     */
    private void initJoinGamePanel() {
        Settings clientSettings = Settings.getSettings();

        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0xe67e22));
        this.add(panel);

        AwesomeImage bg = new AwesomeImage(block);
        AwesomeText codeLabel = new AwesomeText("Enter code");
        AwesomeText nameLabel = new AwesomeText("Enter name");
        AwesomeButton accept = new AwesomeButton("Go!");
        AwesomeButton back = new AwesomeButton("Back");
        JTextField codeInput = new JTextField();
        JTextField nameInput = new JTextField(clientSettings.getPreferredName());
        codeInput.setHorizontalAlignment(SwingConstants.CENTER);
        nameInput.setHorizontalAlignment(SwingConstants.CENTER);

        AwesomeUtil.dynamicFont(codeLabel, .8f);
        AwesomeUtil.dynamicFont(nameLabel, .8f);
        AwesomeUtil.dynamicFont(accept, .8f);
        AwesomeUtil.dynamicFont(back, .8f);
        AwesomeUtil.dynamicFont(codeInput, .6f);
        AwesomeUtil.dynamicFont(nameInput, .6f);

        panel.add(codeLabel);
        panel.add(nameLabel);
        panel.add(codeInput);
        panel.add(nameInput);
        panel.add(back);
        panel.add(accept);
        panel.add(bg);

        layout.setConstraintsRatioByWidth(codeLabel, 0.5f, 0.15f, 0.7f, 0.2f);
        layout.setConstraintsRatioByWidth(codeInput, 0.5f, 0.3f, 0.7f, 0.2f);
        layout.setConstraintsRatioByWidth(nameLabel, 0.5f, 0.45f, 0.7f, 0.2f);
        layout.setConstraintsRatioByWidth(nameInput, 0.5f, 0.6f, 0.7f, 0.2f);
        layout.setConstraintsRatioByWidth(back, 0.3f, 0.8f, 0.3f, 0.5f);
        layout.setConstraintsRatioByWidth(accept, 0.7f, 0.8f, 0.3f, 0.5f);
        layout.setConstraintsRatioByWidth(bg, 0.5f, 0.5f, 1.0f, 1.0f);

        AwesomeUtil.wiggleOnHover(back, 10.0f);
        AwesomeUtil.wiggleOnHover(accept, 10.0f);

        back.addActionListener(e -> ((CardLayout)this.getLayout()).previous(this) );
        accept.addActionListener(e -> joinGame(codeInput.getText()));

        // Allow uppercase only
        AbstractDocument document = (AbstractDocument) codeInput.getDocument();
        final int maxCharacters = 6;
        document.setDocumentFilter(new DocumentFilter() {
            public void replace(FilterBypass fb, int offs, int length,
                                String str, AttributeSet a) throws BadLocationException {
                if ((fb.getDocument().getLength() + str.length() - length) <= maxCharacters) {
                    super.replace(fb, offs, length, str.toUpperCase(), a);
                }
            }

            public void insertString(FilterBypass fb, int offs, String str,
                                     AttributeSet a) throws BadLocationException {
                if ((fb.getDocument().getLength() + str.length()) <= maxCharacters) {
                    super.insertString(fb, offs, str.toUpperCase(), a);
                }
            }
        });
        nameInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { clientSettings.setPreferredName(nameInput.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { clientSettings.setPreferredName(nameInput.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { clientSettings.setPreferredName(nameInput.getText()); }
        });
    }

    /**
     * Add a panel as a card to root with the create game inputs.
     */
    private void initCreateGamePanel() {
        Settings clientSettings = Settings.getSettings();

        PercentLayout layout = new PercentLayout(1.0f);
        JPanel panel = new JPanel(layout);
        panel.setBackground(new Color(0xe67e22));
        this.add(panel);

        float y = 0.15f;
        AwesomeText nameInputLabel = new AwesomeText("YOUR NAME: ");
        JTextField nameInput = new JTextField(clientSettings.getPreferredName());
        nameInput.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(nameInput);
        panel.add(nameInputLabel);
        layout.setConstraintsRatioByWidth(nameInputLabel, 0.3f, y, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(nameInput, 0.75f, y, 0.4f, 0.25f);
        nameInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { clientSettings.setPreferredName(nameInput.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { clientSettings.setPreferredName(nameInput.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { clientSettings.setPreferredName(nameInput.getText()); }
        });

        y += .1f;
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
        layout.setConstraintsRatioByWidth(maxPlayersLabel, 0.3f, y, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(increaseMaxPlayers, 0.9f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(decreaseMaxPlayers, 0.6f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(maxPlayers, 0.75f, y, 0.2f, 0.5f);

        y += .1f;
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
        layout.setConstraintsRatioByWidth(numRoundsLabel, 0.3f, y, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(decrease, 0.6f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(increase, 0.9f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(numRounds, 0.75f, y, 0.2f, 0.5f);

        y += .1f;
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
        layout.setConstraintsRatioByWidth(drawTimeLabel, 0.3f, y, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(drawTimeDecrease, 0.6f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(drawTimeIncrease, 0.9f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(drawTime, 0.75f, y, 0.2f, 0.5f);

        y += .1f;
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
        layout.setConstraintsRatioByWidth(guessTimeLabel, 0.3f, y, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(guessTimeDecrease, 0.6f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(guessTimeIncrease, 0.9f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(guessTime, 0.75f, y, 0.2f, 0.5f);

        y += .1f;
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
        layout.setConstraintsRatioByWidth(getChoicesLabel, 0.3f, y, 0.4f, 0.25f);
        layout.setConstraintsRatioByWidth(choicesCountIncrease, 0.9f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(choicesCountDecrease, 0.6f, y, 0.1f, 1.0f);
        layout.setConstraintsRatioByWidth(getChoices, 0.75f, y, 0.2f, 0.5f);

        AwesomeButton create = new AwesomeButton("Create");
        AwesomeButton back = new AwesomeButton("Back");
        panel.add(create);
        panel.add(back);
        layout.setConstraintsRatioByWidth(create, 0.75f, 0.8f, 0.3f, 0.5f);
        layout.setConstraintsRatioByWidth(back, 0.25f, 0.8f, 0.3f, 0.5f);
        create.addActionListener(e -> createGame() );
        back.addActionListener(e -> ((CardLayout)this.getLayout()).first(this));
        AwesomeUtil.wiggleOnHover(create, 10.0f);
        AwesomeUtil.wiggleOnHover(back, 10.0f);
    }


    private boolean joinGameClicked = false;
    private void joinGame(String code) {
        if (joinGameClicked) return;
        joinGameClicked = true;
        Settings clientSettings = Settings.getSettings();
        Message msg = new Message(Message.Type.JOIN_GAME);
        msg.data.put("sessionId", code);
        msg.data.put("requestedName", clientSettings.getPreferredName());
        msg.data.put("requestedAvatarId", clientSettings.getPreferredAvatarId());
        game.sendMessage(msg, new MessageResponseListener() {
            @Override
            public void onSuccess(Message msg) {
                int playerId = (int) msg.data.get("playerId");
                String playerName = (String) msg.data.get("playerName");
                int avatarId = (int) msg.data.get("playerAvatarId");
                Player player = new Player(playerId, playerName, avatarId);

                game.startSession(player, (GameSettings) msg.data.get("gameSettings"), (String) msg.data.get("sessionId"));

                JoinPhase joinPhase = new JoinPhase();
                game.getSession().setCurrentPhase(joinPhase);
                int[] existingPlayerIds = (int[]) msg.data.get("existingPlayerIds");
                String[] existingPlayerNames = (String[]) msg.data.get("existingPlayerNames");
                int[] existingPlayerAvatarIds = (int[]) msg.data.get("existingPlayerAvatarIds");
                for (int i = 0; i < existingPlayerIds.length; i++) {
                    joinPhase.addPlayer(new Player(existingPlayerIds[i], existingPlayerNames[i], existingPlayerAvatarIds[i]));
                }

                Settings.getSettings().removeListener(settingsListener);
            }

            @Override
            public void onError(String errorMsg) {
                joinGameClicked = false;
                game.setErrorMsg(msg.error);
            }
        });
    }


    private boolean createGameClicked = false;
    private void createGame() {
        if (createGameClicked) return;
        createGameClicked = true;
        Settings clientSettings = Settings.getSettings();
        Message msg =  new Message(Message.Type.CREATE_GAME);
        msg.data.put("settings", gameSettings);
        msg.data.put("requestedName", clientSettings.getPreferredName());
        msg.data.put("requestedAvatarId", clientSettings.getPreferredAvatarId());
        game.sendMessage(msg, new MessageResponseListener() {
            @Override
            public void onSuccess(Message msg) {
                int playerId = (int) msg.data.get("playerId");
                String playerName = (String) msg.data.get("playerName");
                int avatarId = (int) msg.data.get("playerAvatarId");
                Player player = new Player(playerId, playerName, avatarId);

                game.startSession(player, gameSettings, (String) msg.data.get("sessionId"));
                game.getSession().setCurrentPhase(new JoinPhase());

                Settings.getSettings().removeListener(settingsListener);
            }

            @Override
            public void onError(String errorMsg) {
                createGameClicked = false;
                game.setErrorMsg(errorMsg);
            }
        });
    }
}
