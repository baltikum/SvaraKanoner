package client;
import client.ui.*;

import common.Message;
import common.Phase;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 *
 *
 * @author johnnla
 *
 */

public class DrawPhase extends Phase implements ActionListener {


    private String wordToDraw;
    private JPanel panelTop;


    public DrawPhase(Message msg ) {    //  ?

        super();

        // JFrame mainFrame = new JFrame("Ryktet går!");


        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());



        //mainFrame.add(panel);
        Game.game.setContentPanel(panel);


        panelTop = new JPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;

        panelTop.setLayout(new GridBagLayout());


        panel.add(panelTop, BorderLayout.NORTH);


        addWord("hej");

        JPanel panelBottom = new JPanel();
        panel.add(panelBottom, BorderLayout.SOUTH);
        ///   panelBottom.setLayout(new BorderLayout());

        DrawPanel drawPanel = new DrawPanel();

        JPanel panelRight = new JPanel();
        panel.add(panelRight, BorderLayout.EAST);
        panelRight.setLayout(new GridBagLayout());

        JPanel panelCenter = new JPanel();
        panel.add(panelCenter);
        panelCenter.setLayout(null);

        panelCenter.add(drawPanel);

        drawPanel.setOpaque(true);
        panelCenter.setOpaque(true);

        drawPanel.setBackground(Color.WHITE);

        //    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //  mainFrame.setLocation(500, 200);

        //  mainFrame.setPreferredSize(new Dimension(1000, 1000));
        //    mainFrame.setMinimumSize(new Dimension(600, 600));

        panelCenter.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {

                int height = panelCenter.getHeight();
                int width = panelCenter.getWidth();

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



        JLabel blank = new JLabel("");
        c.gridx = 0;
        c.gridy = 1;
        panelRight.add(blank, c);


        JButton btnGreen = new JButton(new ImageIcon("client\\assets\\greenColor.png"));
        btnGreen.setContentAreaFilled(false);
        btnGreen.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 2;
        panelRight.add(btnGreen, c);

        btnGreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColorGreen();
            }
        });

        JButton btnBlue = new JButton(new ImageIcon("client\\assets\\blueColor.png"));
        btnBlue.setContentAreaFilled(false);
        btnBlue.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 3;
        panelRight.add(btnBlue, c);
        btnBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColorBlue();
            }
        });

        JButton btnBlack = new JButton(new ImageIcon("client\\assets\\blackColor.png"));
        btnBlack.setContentAreaFilled(false);
        btnBlack.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 4;
        panelRight.add(btnBlack, c);

        btnBlack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColorBlack();
            }
        });

        JButton btnRed = new JButton(new ImageIcon("client\\assets\\redColor.png"));
        btnRed.setContentAreaFilled(false);
        btnRed.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 6;
        panelRight.add(btnRed, c);

        btnRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColorRed();
            }
        });

        JButton btnYellow = new JButton(new ImageIcon("client\\assets\\yellowColor.png"));
        btnYellow.setContentAreaFilled(false);
        btnYellow.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 5;
        panelRight.add(btnYellow, c);

        btnYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColorYellow();
            }
        });

        JButton btnBrown = new JButton(new ImageIcon("client\\assets\\brownColor.png"));
        btnBrown.setContentAreaFilled(false);
        btnBrown.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 7;
        panelRight.add(btnBrown, c);

        btnBrown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColorBrown();
            }
        });

        JButton smallBrushSize = new JButton(new ImageIcon("client\\assets\\smallBrush.png"));
        smallBrushSize.setContentAreaFilled(false);
        smallBrushSize.setBorderPainted(false);
        panelBottom.add(smallBrushSize);
        smallBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setSmallBrush();
            }
        });

        JButton mediumBrushSize = new JButton(new ImageIcon("client\\assets\\mediumBrush.png"));
        mediumBrushSize.setContentAreaFilled(false);
        mediumBrushSize.setBorderPainted(false);
        panelBottom.add(mediumBrushSize);
        mediumBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setNormalBrush();

            }
        });

        JButton bigBrushSize = new JButton(new ImageIcon("client\\assets\\bigBrush.png"));
        bigBrushSize.setContentAreaFilled(false);
        bigBrushSize.setBorderPainted(false);
        panelBottom.add(bigBrushSize);
        bigBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setBigBrush();
            }
        });

        JButton eraserBtn = new JButton(new ImageIcon("client\\assets\\eraser2.png"));
        eraserBtn.setContentAreaFilled(false);
        eraserBtn.setBorderPainted(false);
        panelBottom.add(eraserBtn);
        eraserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setEraser();
            }
        });

        JButton clearBTN = new JButton(new ImageIcon("client\\assets\\trashcan.png"));
        clearBTN.setContentAreaFilled(false);
        clearBTN.setBorderPainted(false);
        panelBottom.add(clearBTN);
        clearBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearPanel();
            }
        });


        AwesomeButton done = new AwesomeButton("Done!");
        panelBottom.add(done);
        done.addActionListener(e -> {
            Game.game.sendMessage(new Message(Message.Type.SUBMIT_PICTURE));
        });


        // mainFrame.pack();
        // mainFrame.setVisible(true);


        //  Game.game.setContentPanel(panel);      //  korrekt?

    }

    //   public static void main(String[] args) {
    //     new DrawPhase();
    //   }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void message(Message msg) {
        switch (msg.type) {
            case WORD_DATA -> {
                this.wordToDraw = (String) msg.data.get("word");
                addWord(wordToDraw);
                //      Game.game.sendMessage(new Message(Message.Type.WORD_DATA_RECEIVED));
            }
        }
    }

    private void addWord(String word) {
        JLabel jlabelWord = new JLabel("Word to draw: "+word);
        panelTop.add(jlabelWord);
    }




}
