/**
 * message is used to transfer object and data between client and server,
 * the client and server use socket to send data, so message will contains
 * the data and its type, then covert the string data back to object use Gson
 */
public class Message {
    private String option;
    private Object data;
    private String ID;

    public Message() {

    }

    public Message(String option, Object data) {
        this.option = option;
        this.data = data;
    }

    public Message(String ID, String option, Object data) {
        this.ID = ID;
        this.option = option;
        this.data = data;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
