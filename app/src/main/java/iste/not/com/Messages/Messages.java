package iste.not.com.Messages;

public class Messages
{
   long time;
   String message;
   String seen;
   String from;

    public Messages() {
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(Long time, String message, String seen, String from) {
        this.time = time;
        this.message = message;
        this.seen = seen;
        this.from = from;
    }
}
