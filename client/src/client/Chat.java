package client;

import client.ui.AwesomeButton;
import common.Message;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * A panel for the chat input and displaying.
 *
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class Chat extends JPanel {
    private boolean isOpen = false;
    private final JButton closeChat;
    private final JTextField inputField;
    private final ArrayList<String> lines = new ArrayList<>();
    private int scrolledLines = 0;

    /**
     * Initiates the panel.
     * @param icons The image to get the appropriate icons from.
     */
    public Chat(BufferedImage icons) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(400, 32 * 3));
        setOpaque(false);
        setFont(getFont().deriveFont(17.0f));

        Image upIcon = Assets.getTile(icons, 2, 0, 1, 1, 4);
        Image downIcon = Assets.getTile(icons, 3, 0, 1, 1, 4);
        Image sendIcon = Assets.getTile(icons, 0, 1, 1, 1, 4);
        Image closeIcon = Assets.getTile(icons, 1, 1, 1, 1, 4);

        JPanel buttonsPanel = new JPanel(null);
        JPanel inputPanel = new JPanel(null);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);
        inputPanel.setOpaque(false);

        inputField = new JTextField();
        closeChat = new AwesomeButton(closeIcon);
        AwesomeButton upInChat = new AwesomeButton(upIcon);
        AwesomeButton downInChat = new AwesomeButton(downIcon);
        AwesomeButton sendChat = new AwesomeButton(sendIcon);
        closeChat.setPreferredSize(new Dimension(32, 32));
        upInChat.setPreferredSize(new Dimension(32, 32));
        downInChat.setPreferredSize(new Dimension(32, 32));
        sendChat.setSize(32, 32);
        inputField.setPreferredSize(new Dimension(0, 32));
        buttonsPanel.add(closeChat);
        buttonsPanel.add(Box.createVerticalGlue());
        buttonsPanel.add(upInChat);
        buttonsPanel.add(downInChat);

        inputPanel.add(inputField);
        inputPanel.add(sendChat);

        add(inputPanel, BorderLayout.PAGE_END);
        add(buttonsPanel, BorderLayout.LINE_END);

        closeChat.setVisible(false);
        closeChat.addActionListener(e -> close());

        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                open();
            }
            @Override public void focusLost(FocusEvent e) { }
        });
        inputField.addActionListener(e -> send());
        upInChat.addActionListener(e -> scrollUp());
        downInChat.addActionListener(e -> scrollDown());
        sendChat.addActionListener(e -> send());
    }

    /**
     * Makes the chat small.
     */
    public void close() {
        if (!isOpen) return;
        isOpen = false;
        Dimension size = getPreferredSize();
        size.height = 32 * 3;
        setPreferredSize(size);
        closeChat.setVisible(false);
    }

    /**
     * Makes the chat big.
     */
    public void open() {
        if (isOpen) return;
        isOpen = true;
        closeChat.setVisible(true);
        Dimension size = getPreferredSize();
        size.height = 300;
        setPreferredSize(size);
    }

    /**
     * Scroll up a little bit.
     */
    public void scrollUp() {
        scrolledLines = Math.max(Math.min(scrolledLines + 3, lines.size() - 4), 0);
    }

    /**
     * Scroll down a little bit.
     */
    public void scrollDown() {
        scrolledLines = Math.max(scrolledLines - 3, 0);
    }

    /**
     * Send the content of the input filed
     */
    public void send() {
        open();

        String msg = inputField.getText();
        if (!msg.isEmpty()) {
            Game game = Game.getInstance();
            msg = game.getSession().getThisPlayer().getName() + ": " + msg + "\n";
            lines.add(msg);
            inputField.setText("");
            if (scrolledLines != 0) ++scrolledLines;

            Message serverMsg = new Message(Message.Type.CHAT_MESSAGE);
            serverMsg.addParameter("message", msg);
            Game.getInstance().sendMessage(serverMsg);
        }
    }

    /**
     * Recieve a message
     * @param msg The server message
     */
    public void message(Message msg) {
        lines.add((String) msg.data.get("message"));
        if (scrolledLines != 0) ++scrolledLines;
    }

    public void clear() {
        lines.clear();
        scrolledLines = 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(this.getFont());
        FontMetrics metrics = g.getFontMetrics();
        int y = getHeight() - metrics.getHeight() - 32;
        for (int i = lines.size() - 1 - scrolledLines; i >= 0; i--) {
            g.drawString(lines.get(i), 5, y);
            y -= metrics.getHeight();
            if (y <= 0) break;
        }
    }
}
