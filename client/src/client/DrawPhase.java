package client;
import client.ui.*;

import common.Message;
import common.Phase;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * DrawPhase client side. Sets up the drawing UI with a word to draw.
 *
 * @author Johnny Larsson
 * @version 04/03/21
 */

public class DrawPhase extends Phase implements ActionListener {


    private String wordToDraw;

    private final GameSession session;
    private final DrawPanel drawPanel;



    /**
     * Constructor DrawPhase Client side.
     *
     * Sets up the drawing UI with buttons and panels.
     * @param msg Message containing the word to draw
     */
    public DrawPhase(Message msg) {
        super();
        session = Game.getInstance().getSession();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        panel.add(bottomPanel, BorderLayout.SOUTH);
        drawPanel = new DrawPanel();
        JPanel rightPanel = new JPanel();
        panel.add(rightPanel, BorderLayout.EAST);
        rightPanel.setLayout(new GridBagLayout());
        JPanel centerPanel = new JPanel();
        panel.add(centerPanel);
        centerPanel.setLayout(null);
        centerPanel.add(drawPanel);

        drawPanel.setOpaque(true);
        centerPanel.setOpaque(true);

        rightPanel.setBackground(new Color(0xe67e22));
        bottomPanel.setBackground(new Color(0xe67e22));
        centerPanel.setBackground(new Color(0xe67e22));

        centerPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                int height = centerPanel.getHeight();
                int width = centerPanel.getWidth();

                if (height > width) {
                    drawPanel.setSize(width, width);
                    drawPanel.setLocation(0, (height - width) / 2);
                } else {
                    drawPanel.setSize(height, height);
                    drawPanel.setLocation((width - height) / 2, 0);
                }
                drawPanel.updateBrushSize();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;

        JLabel blank = new JLabel("");
        c.gridx = 0;
        c.gridy = 1;
        rightPanel.add(blank, c);

        JButton btnGreen = new JButton(new ImageIcon("client\\assets\\greenColor.png"));
        btnGreen.setContentAreaFilled(false);
        btnGreen.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 2;
        rightPanel.add(btnGreen, c);

        btnGreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("GREEN");
            }
        });

        JButton btnBlue = new JButton(new ImageIcon("client\\assets\\blueColor.png"));
        btnBlue.setContentAreaFilled(false);
        btnBlue.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 3;
        rightPanel.add(btnBlue, c);
        btnBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("BLUE");
            }
        });

        JButton btnBlack = new JButton(new ImageIcon("client\\assets\\blackColor.png"));
        btnBlack.setContentAreaFilled(false);
        btnBlack.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 4;
        rightPanel.add(btnBlack, c);

        btnBlack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("BLACK");
            }
        });

        JButton btnYellow = new JButton(new ImageIcon("client\\assets\\yellowColor.png"));
        btnYellow.setContentAreaFilled(false);
        btnYellow.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 5;
        rightPanel.add(btnYellow, c);

        btnYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("YELLOW");
            }
        });

        JButton btnRed = new JButton(new ImageIcon("client\\assets\\redColor.png"));
        btnRed.setContentAreaFilled(false);
        btnRed.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 6;
        rightPanel.add(btnRed, c);
        btnRed.addActionListener(e -> drawPanel.setColor("RED"));

        JButton btnBrown = new JButton(new ImageIcon("client\\assets\\brownColor.png"));
        btnBrown.setContentAreaFilled(false);
        btnBrown.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 7;
        rightPanel.add(btnBrown, c);
        btnBrown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("BROWN");
            }
        });

        JButton btnPink = new JButton(new ImageIcon("client\\assets\\pinkColor.png"));
        btnPink.setContentAreaFilled(false);
        btnPink.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 8;
        rightPanel.add(btnPink, c);

        btnPink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("PINK");
            }
        });

        JButton btnOrange = new JButton(new ImageIcon("client\\assets\\orangeColor.png"));
        btnOrange.setContentAreaFilled(false);
        btnOrange.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 9;
        rightPanel.add(btnOrange, c);

        btnOrange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("ORANGE");
            }
        });

        JButton btnGrey = new JButton(new ImageIcon("client\\assets\\greyColor.png"));
        btnGrey.setContentAreaFilled(false);
        btnGrey.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 10;
        rightPanel.add(btnGrey, c);

        btnGrey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("GRAY");
            }
        });

        JButton smallBrushSize = new JButton(new ImageIcon("client\\assets\\smallBrush.png"));
        smallBrushSize.setContentAreaFilled(false);
        smallBrushSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        smallBrushSize.setBorderPainted(true);
        bottomPanel.add(smallBrushSize);
        smallBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setSmallBrush();
            }
        });

        JButton mediumBrushSize = new JButton(new ImageIcon("client\\assets\\mediumBrush.png"));
        mediumBrushSize.setContentAreaFilled(false);
        mediumBrushSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomPanel.add(mediumBrushSize);
        mediumBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setNormalBrush();
            }
        });

        JButton bigBrushSize = new JButton(new ImageIcon("client\\assets\\bigBrush.png"));
        bigBrushSize.setContentAreaFilled(false);
        bigBrushSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomPanel.add(bigBrushSize);
        bigBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setBigBrush();
            }
        });

        JButton eraserBtn = new JButton(new ImageIcon("client\\assets\\eraser2.png"));
        eraserBtn.setContentAreaFilled(false);
        eraserBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomPanel.add(eraserBtn);
        eraserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setEraser();
            }
        });

        JButton clearBTN = new JButton(new ImageIcon("client\\assets\\trashcan.png"));
        clearBTN.setContentAreaFilled(false);
        clearBTN.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        bottomPanel.add(clearBTN);
        clearBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearPanel();
            }
        });

        AwesomeButton done = new AwesomeButton("Done!", Assets.getMainmenuIcon(Assets.MENU_WHAM));
        bottomPanel.add(done);
        done.addActionListener(e -> {
            submitPicture();
            centerPanel.remove(drawPanel);
            bottomPanel.remove(done);
            AwesomeText pictureSent = new AwesomeText("Picture sent!! Waiting for others.. ");
            pictureSent.setBounds(400,200,200,30);
            pictureSent.setFont(Assets.getFont().deriveFont(40.0f));
            centerPanel.add(pictureSent);
            panel.revalidate();
        });

        session.getPhaseUI().setContent(panel);
        session.getPhaseUI().startTimer((int)(session.getGameSettings().getDrawTimeMilliseconds() / 1000));

        this.wordToDraw = (String) msg.data.get("word");
        addWord(wordToDraw);
        panel.revalidate();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }


    /**
     * Handles messages recieved, TIMES UP triggers a submit of the picture.
     * @param msg
     */
    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.TIMES_UP) {
            submitPicture();
        }
    }

    /**
     * Sets up a word for the player to draw.
     * @param word
     */
    private void addWord(String word) {
        session.getPhaseUI().setTitle("Draw " + word);
    }


    /**
     * Sends a message to submit the picture and to stop drawing.
     */
    private void submitPicture() {
        Message msg = new Message(Message.Type.SUBMIT_PICTURE);
        msg.addParameter("drawing", drawPanel.getPictureAndStopPainting());
        Game.getInstance().sendMessage(msg);
    }
}
