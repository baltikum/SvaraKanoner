package common;

public class Player {
    private int id;
    private String name;
    private int avatarId;

    public Player(int id, String name, int avatarId) {
        this.id = id;
        this.name = name;
        this.avatarId = avatarId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int id) {
        avatarId = id;
    }
}
