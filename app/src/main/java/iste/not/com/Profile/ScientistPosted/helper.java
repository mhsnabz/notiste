package iste.not.com.Profile.ScientistPosted;

public class helper
{
    String key;
    String teacherId;

    public helper(String key, String teacherId) {
        this.key = key;
        this.teacherId = teacherId;
    }

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

    public helper() {
    }
}
