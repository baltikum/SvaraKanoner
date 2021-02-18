package client;

import java.awt.*;

public class Player {
    private final int id;
    private String name;
    private Image avatar;

    public Player(int id, String name, Image avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Image getAvatar() {
        return avatar;
    }
}
