package client;

import client.ui.AwesomeButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

/**
 * A panel for the chat input and displaying.
 *
 * @author Jesper Jansson
 * @version 19/02/21
 */
public class Chat extends JPanel {
    private boolean isOpen = false;
    private JButton closeChat;
    private JTextField inputField;
    private JTextArea messageData;
    private JScrollPane messageScrollPane;

    /**
     * Initiates the panel.
     * @param icons The image to get the appropriate icons from.
     */
    public Chat(BufferedImage icons) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(400, 32 * 3));
        setOpaque(false);

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

        messageData = new JTextArea();
        messageData.setOpaque(false);
        messageData.setFont(messageData.getFont().deriveFont(20.0f));
        messageData.setLineWrap(true);
        messageData.setEditable(false);
        messageData.setFocusable(false);
        messageScrollPane = new JScrollPane(messageData);
        messageScrollPane.setOpaque(false);
        messageScrollPane.getViewport().setOpaque(false);
        messageScrollPane.setBorder(null);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

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
        add(messageScrollPane, BorderLayout.CENTER);
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
        messageScrollPane.getVerticalScrollBar().setValue(messageScrollPane.getVerticalScrollBar().getValue() - 30);
    }

    /**
     * Scroll down a little bit.
     */
    public void scrollDown() {
        messageScrollPane.getVerticalScrollBar().setValue(messageScrollPane.getVerticalScrollBar().getValue() + 30);
    }

    /**
     * TODO: Implement this server vice.
     */
    public void send() {
        open();

        String msg = inputField.getText();
        if (!msg.isEmpty()) {
            messageData.append(msg + "\n");
            inputField.setText("");
        }
    }
}
