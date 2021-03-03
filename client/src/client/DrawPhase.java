package client;
import client.ui.*;

import common.Message;
import common.Phase;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 *
 *
 *
 * @author johnnla
 *
 */

public class DrawPhase extends Phase implements ActionListener {


    private String wordToDraw;

    private final DrawPanel drawPanel;
    private final Image wham;
    //private Object AwesomeText;


    //  public DrawPhase( ) {    //  ?
    public DrawPhase(Message msg) {    //  ?
        super();

        // JFrame mainFrame = new JFrame("Ryktet går!");
        BufferedImage tileMap = Assets.loadImage("mainmenu.png");
       wham = Assets.getTile(tileMap, 0, 0, 3, 1, 8);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());



        //mainFrame.add(panel);


        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.5;
        c.weighty = 0.5;



        JPanel panelBottom = new JPanel();
        panel.add(panelBottom, BorderLayout.SOUTH);
        ///   panelBottom.setLayout(new BorderLayout());

        drawPanel = new DrawPanel();

        JPanel panelRight = new JPanel();
        panel.add(panelRight, BorderLayout.EAST);
        panelRight.setLayout(new GridBagLayout());

        JPanel panelCenter = new JPanel();
        panel.add(panelCenter);
        panelCenter.setLayout(null);

        panelCenter.add(drawPanel);

        drawPanel.setOpaque(true);
        panelCenter.setOpaque(true);


        panelRight.setBackground(new Color(0xe67e22));
        panelBottom.setBackground(new Color(0xe67e22));
        panelCenter.setBackground(new Color(0xe67e22));

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


        JButton btnGreen = new JButton(new ImageIcon("/greenColor.png"));
        btnGreen.setContentAreaFilled(false);
        btnGreen.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 2;
        panelRight.add(btnGreen, c);

        btnGreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("GREEN");
            }
        });

        JButton btnBlue = new JButton(new ImageIcon("/blueColor.png"));
        btnBlue.setContentAreaFilled(false);
        btnBlue.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 3;
        panelRight.add(btnBlue, c);
        btnBlue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("BLUE");
            }
        });

        JButton btnBlack = new JButton(new ImageIcon("/blackColor.png"));
        btnBlack.setContentAreaFilled(false);
        btnBlack.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 4;
        panelRight.add(btnBlack, c);

        btnBlack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("BLACK");
            }
        });

        JButton btnRed = new JButton(new ImageIcon("/redColor.png"));
        btnRed.setContentAreaFilled(false);
        btnRed.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 6;
        panelRight.add(btnRed, c);

        btnRed.addActionListener(e -> drawPanel.setColor("RED"));

        JButton btnYellow = new JButton(new ImageIcon("/yellowColor.png"));
        btnYellow.setContentAreaFilled(false);
        btnYellow.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 5;
        panelRight.add(btnYellow, c);

        btnYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("YELLOW");
            }
        });

        JButton btnBrown = new JButton(new ImageIcon("/brownColor.png"));
        btnBrown.setContentAreaFilled(false);
        btnBrown.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 7;
        panelRight.add(btnBrown, c);

        btnBrown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("BROWN");
            }
        });


        JButton btnPink = new JButton(new ImageIcon("/pinkColor.png"));
        btnPink.setContentAreaFilled(false);
        btnPink.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 8;
        panelRight.add(btnPink, c);

        btnPink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("PINK");
            }
        });


        JButton btnOrange = new JButton(new ImageIcon("/orangeColor.png"));
        btnOrange.setContentAreaFilled(false);
        btnOrange.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 9;
        panelRight.add(btnOrange, c);

        btnOrange.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("ORANGE");
            }
        });


        JButton btnGrey = new JButton(new ImageIcon("/greyColor.png"));
        btnGrey.setContentAreaFilled(false);
        btnGrey.setBorderPainted(false);
        c.gridx = 0;
        c.gridy = 10;
        panelRight.add(btnGrey, c);

        btnGrey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setColor("GRAY");
            }
        });

        JButton smallBrushSize = new JButton(new ImageIcon("/smallBrush.png"));
       smallBrushSize.setContentAreaFilled(false);
        smallBrushSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        smallBrushSize.setBorderPainted(true);
        panelBottom.add(smallBrushSize);
        smallBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setSmallBrush();
            }
        });

        JButton mediumBrushSize = new JButton(new ImageIcon("/mediumBrush.png"));
        mediumBrushSize.setContentAreaFilled(false);
        mediumBrushSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
       // mediumBrushSize.setBorderPainted(true);
        panelBottom.add(mediumBrushSize);
        mediumBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setNormalBrush();

            }
        });

        JButton bigBrushSize = new JButton(new ImageIcon("/bigBrush.png"));
        bigBrushSize.setContentAreaFilled(false);
        bigBrushSize.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelBottom.add(bigBrushSize);
        bigBrushSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setBigBrush();
            }
        });

        JButton eraserBtn = new JButton(new ImageIcon("/eraser2.png"));
        eraserBtn.setContentAreaFilled(false);
        eraserBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelBottom.add(eraserBtn);
        eraserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setEraser();
            }
        });

        JButton clearBTN = new JButton(new ImageIcon("/trashcan.png"));
        clearBTN.setContentAreaFilled(false);
        clearBTN.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelBottom.add(clearBTN);
        clearBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.clearPanel();
            }
        });





        AwesomeButton done = new AwesomeButton("Done!", wham);
     //   AwesomeButton done = new AwesomeButton("Done!", wham);
        panelBottom.add(done);
        done.addActionListener(e -> {
            submitPicture();
            panelCenter.remove(drawPanel);
            panelBottom.remove(done);
            AwesomeText pictureSent = new AwesomeText("Picture sent!! Waiting for others.. ");
            pictureSent.setBounds(400,200,200,30);
            pictureSent.setFont(Assets.getFont().deriveFont(40.0f));
            panelCenter.add(pictureSent);
            panel.revalidate();
        });


        // mainFrame.pack();
        // mainFrame.setVisible(true);


        //  Game.game.setContentPanel(panel);      //  korrekt?

        Game.game.getPhaseUI().setContent(panel);
        Game.game.getPhaseUI().startTimer((int)(Game.game.getGameSettings().getDrawTimeMilliseconds() / 1000));

        this.wordToDraw = (String) msg.data.get("word");
        addWord(wordToDraw);
        panel.revalidate();
    }

    //   public static void main(String[] args) {
    //     new DrawPhase();
    //   }



    public void clearPaintArea(){

    }


    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void message(Message msg) {
        if (msg.type == Message.Type.TIMES_UP) {
            submitPicture();
        }
    }

    private void addWord(String word) {
        Game.game.getPhaseUI().setTitle("Draw " + word);
    }

    private void submitPicture() {
        Message msg = new Message(Message.Type.SUBMIT_PICTURE);
        msg.addParameter("drawing", drawPanel.getDrawData());
        Game.game.sendMessage(msg);
    }
}
