public class User {
    private String ID;
    private String username;
    private String role;
    private Boolean isKickout;

    public User(String ID, String username, String role, Boolean isKickout) {
        this.ID = ID;
        this.username = username;
        this.role = role;
        this.isKickout = isKickout;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsKickout() {
        return isKickout;
    }

    public void setIsKickout(Boolean isKickout) {
        this.isKickout = isKickout;
    }
}
