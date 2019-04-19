package iste.not.com.Main.askNoteHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Notes
{
    public String userId;
    public String comment;
    public String lessonName;
    public long time;
    public long count;
    public String image;
    public String username;
    public String lessonKey;
    public String teacherID;
    public String key;

    public Notes(String userId, String comment, String lessonName, long time, long count, String image, String username, String lessonKey, String teacherID, String key) {
        this.userId = userId;
        this.comment = comment;
        this.lessonName = lessonName;
        this.time = time;
        this.count = count;
        this.image = image;
        this.username = username;
        this.lessonKey = lessonKey;
        this.teacherID = teacherID;
        this.key = key;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getLessonKey() {
        return lessonKey;
    }

    public void setLessonKey(String lessonKey) {
        this.lessonKey = lessonKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image, String s) {
        this.image = image;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public String getUserId() {
        return userId;
    }

    public Notes() {
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
  /*  public String lessonName;

    public String  data;
    public String comment;
    public String time;

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getUserId() {
        return userId;
    }

    public String getData() {
        return data;
    }

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }

    public Notes(String lessonName, String userId, String data, String comment, String time) {
        this.lessonName = lessonName;
        this.userId = userId;
        this.data = data;
        this.comment = comment;
        this.time = time;
    }

    public Notes()
    {

    }
*/

}




               // 121503031 30
               // 121503069 40





































