/**
 * message is used to transfer object and data between client and server,
 * the client and server use socket to send data, so message will contains
 * the data and its type, then covert the string data back to object use Gson
 */
public class Message {
    private String option;
    private Object data;
    private String classname;

    public Message() {

    }

    public Message(String option, Object data) {
        this.option = option;
        this.data = data;
    }

    public Message(String option, String classname, Object data) {
        this.option = option;
        this.data = data;
        this.classname = classname;
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

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
