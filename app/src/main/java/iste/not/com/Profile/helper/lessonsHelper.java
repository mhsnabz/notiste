package iste.not.com.Profile.helper;

public class lessonsHelper
{
    String key;
    String teacherId;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public lessonsHelper() {
    }

    public lessonsHelper(String key, String teacherId) {
        this.key = key;
        this.teacherId = teacherId;
    }
}
