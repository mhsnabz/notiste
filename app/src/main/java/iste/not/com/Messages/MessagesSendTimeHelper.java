package iste.not.com.Messages;

public class MessagesSendTimeHelper
{
    String seen;
    long time;

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MessagesSendTimeHelper() {

    }

    public MessagesSendTimeHelper(String seen, long time) {

        this.seen = seen;
        this.time = time;
    }
}
