public class ChatMessage {
    private String user_id;
    private String username;
    private String content;

    public ChatMessage(String user_id, String username, String content) {
        this.user_id = user_id;
        this.username = username;
        this.content = content;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
