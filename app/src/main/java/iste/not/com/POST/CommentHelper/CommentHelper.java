package iste.not.com.POST.CommentHelper;

public class CommentHelper
{
    String comment;
    String from;
    String sendTime;

    public CommentHelper(String comment, String from, String sendTime) {
        this.comment = comment;
        this.from = from;
        this.sendTime = sendTime;
    }

    public CommentHelper() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
