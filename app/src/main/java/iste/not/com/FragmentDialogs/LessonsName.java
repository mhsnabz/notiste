package iste.not.com.FragmentDialogs;

public class LessonsName
{
    String name;
    String teacher;
    String lessonName;
    String key;
    String teacherId;

    public LessonsName(String name, String teacher, String lessonName, String key, String teacherId) {
        this.name = name;
        this.teacher = teacher;
        this.lessonName = lessonName;
        this.key = key;
        this.teacherId = teacherId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public LessonsName()
    {

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
