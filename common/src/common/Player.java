package common;

/**
 * Keeps data of a given player.
 *
 * @author Jesper Jansson
 * @version 04/03/21
 */
public class Player {
    private final int id;
    private String name;
    private int avatarId;

    /**
     * Get avatar id of the player.
     * @param id The player identification number, should be unique.
     * @param name The name of the player.
     * @param avatarId The avatar id of the player.
     */
    public Player(int id, String name, int avatarId) {
        this.id = id;
        this.name = name;
        this.avatarId = avatarId;
    }

    /**
     * Get the id of the player. Used to identify a player inside a gamesession.
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Get the name of the player.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the player.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get avatar id of the player.
     * @return The avatar id.
     */
    public int getAvatarId() {
        return avatarId;
    }

    /**
     * Set avatar id of the player.
     * @param id The new avatar id.
     */
    public void setAvatarId(int id) {
        avatarId = id;
    }
}
